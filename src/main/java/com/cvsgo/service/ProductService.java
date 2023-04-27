package com.cvsgo.service;

import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_PRODUCT_BOOKMARK;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_PRODUCT_LIKE;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_PRODUCT;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_PRODUCT_BOOKMARK;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_PRODUCT_LIKE;

import com.cvsgo.dto.product.CategoryDto;
import com.cvsgo.dto.product.ConvenienceStoreDto;
import com.cvsgo.dto.product.ConvenienceStoreEventDto;
import com.cvsgo.dto.product.ConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.EventTypeDto;
import com.cvsgo.dto.product.ReadProductDetailResponseDto;
import com.cvsgo.dto.product.ReadProductFilterResponseDto;
import com.cvsgo.dto.product.ReadProductResponseDto;
import com.cvsgo.dto.product.ReadProductDetailQueryDto;
import com.cvsgo.dto.product.ReadProductQueryDto;
import com.cvsgo.dto.product.ReadProductRequestDto;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.ProductLike;
import com.cvsgo.entity.User;
import com.cvsgo.exception.DuplicateException;
import com.cvsgo.exception.NotFoundException;
import com.cvsgo.repository.CategoryRepository;
import com.cvsgo.repository.ConvenienceStoreRepository;
import com.cvsgo.repository.ProductBookmarkRepository;
import com.cvsgo.repository.ProductLikeRepository;
import com.cvsgo.repository.ProductRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ConvenienceStoreRepository convenienceStoreRepository;
    private final ProductLikeRepository productLikeRepository;
    private final ProductBookmarkRepository productBookmarkRepository;

    /**
     * 사용자가 적용한 필터를 적용해 상품을 조회한다.
     *
     * @param request 사용자가 적용한 필터 dto
     * @return 상품 목록
     */
    @Transactional(readOnly = true)
    public Page<ReadProductResponseDto> readProductList(User user, ReadProductRequestDto request,
        Pageable pageable) {
        List<ReadProductQueryDto> products = productRepository.searchByFilter(user, request,
            pageable);
        Long totalCount = productRepository.countByFilter(request);

        List<ConvenienceStoreEventQueryDto> convenienceStoreEvents = productRepository.findConvenienceStoreEventsByProductIds(
            products.stream().map(ReadProductQueryDto::getProductId).toList());

        Map<Long, List<ConvenienceStoreEventQueryDto>> cvsEventsByProduct =
            convenienceStoreEvents.stream().collect(Collectors.groupingBy(
                ConvenienceStoreEventQueryDto::getProductId));

        List<ReadProductResponseDto> results = products.stream().map(
            productDto -> ReadProductResponseDto.of(productDto,
                cvsEventsByProduct.get(productDto.getProductId()).stream().map(
                        c -> ConvenienceStoreEventDto.of(c.getConvenienceStoreName(), c.getEvent()))
                    .toList())).toList();
        return new PageImpl<>(results, pageable, totalCount);
    }

    /**
     * 상품 ID를 통해 상품을 상세 조회한다.
     *
     * @param user      로그인한 사용자
     * @param productId 상품 ID
     * @return 상품 상세 정보
     * @throws NotFoundException 해당하는 아이디를 가진 상품이 없는 경우
     */
    @Transactional(readOnly = true)
    public ReadProductDetailResponseDto readProduct(User user, Long productId) {
        ReadProductDetailQueryDto product = productRepository.findByProductId(user, productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);

        List<ConvenienceStoreEventQueryDto> convenienceStoreEvents = productRepository.findConvenienceStoreEventsByProductId(
            product.getProductId());

        return ReadProductDetailResponseDto.of(product, convenienceStoreEvents.stream().map(
            c -> ConvenienceStoreEventDto.of(c.getConvenienceStoreName(), c.getEvent())).toList());
    }

    /**
     * 상품 좋아요를 생성한다.
     *
     * @param user      로그인한 사용자
     * @param productId 상품 ID
     * @throws NotFoundException  해당하는 아이디를 가진 상품이 없는 경우
     * @throws DuplicateException 이미 해당하는 상품 좋아요가 존재하는 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public void createProductLike(User user, Long productId) {
        Product product = productRepository.findByIdWithOptimisticLock(productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);

        ProductLike productLike = ProductLike.create(user, product);
        try {
            productLikeRepository.save(productLike);
        } catch (DataIntegrityViolationException e) {
            throw DUPLICATE_PRODUCT_LIKE;
        }
        product.plusLikeCount();
    }

    /**
     * 상품 좋아요를 삭제한다.
     *
     * @param user      로그인한 사용자
     * @param productId 상품 ID
     * @throws NotFoundException 해당하는 아이디를 가진 상품이 없는 경우
     * @throws NotFoundException 해당하는 상품 좋아요가 없는 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductLike(User user, Long productId) {
        Product product = productRepository.findByIdWithOptimisticLock(productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);

        ProductLike productLike = productLikeRepository.findByProductAndUser(product, user)
            .orElseThrow(() -> NOT_FOUND_PRODUCT_LIKE);
        productLikeRepository.delete(productLike);
        product.minusLikeCount();
    }

    /**
     * 상품 북마크를 생성한다.
     *
     * @param user      로그인한 사용자
     * @param productId 상품 ID
     * @throws NotFoundException  해당하는 아이디를 가진 상품이 없는 경우
     * @throws DuplicateException 이미 해당하는 상품 북마크가 존재하는 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public void createProductBookmark(User user, Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);

        ProductBookmark productBookmark = ProductBookmark.create(user, product);
        try {
            productBookmarkRepository.save(productBookmark);
        } catch (DataIntegrityViolationException e) {
            throw DUPLICATE_PRODUCT_BOOKMARK;
        }
    }

    /**
     * 상품 북마크를 삭제한다.
     *
     * @param user      로그인한 사용자
     * @param productId 상품 ID
     * @throws NotFoundException 해당하는 아이디를 가진 상품이 없는 경우
     * @throws NotFoundException 해당하는 상품 북마크가 없는 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductBookmark(User user, Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);

        ProductBookmark productBookmark = productBookmarkRepository.findByProductAndUser(product,
            user).orElseThrow(() -> NOT_FOUND_PRODUCT_BOOKMARK);
        productBookmarkRepository.delete(productBookmark);
    }

    /**
     * 상품 조회 시 적용할 필터를 조회한다.
     *
     * @return 상품 필터
     */
    @Transactional(readOnly = true)
    public ReadProductFilterResponseDto getProductFilter() {
        List<ConvenienceStoreDto> convenienceStoreNames = convenienceStoreRepository.findAll()
            .stream().map(ConvenienceStoreDto::from).toList();
        List<CategoryDto> categoryNames = categoryRepository.findAll().stream()
            .map(CategoryDto::from).toList();
        List<EventTypeDto> eventTypes = Arrays.stream(EventType.values())
            .map(com.cvsgo.dto.product.EventTypeDto::from).toList();
        Integer highestPrice = productRepository.findFirstByOrderByPriceDesc().getPrice();

        return ReadProductFilterResponseDto.of(convenienceStoreNames, categoryNames, eventTypes,
            highestPrice);
    }

}
