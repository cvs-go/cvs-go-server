package com.cvsgo.service;

import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_PRODUCT_BOOKMARK;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_PRODUCT_LIKE;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_PRODUCT;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_PRODUCT_LIKE;

import com.cvsgo.dto.product.CategoryResponseDto;
import com.cvsgo.dto.product.ConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.ConvenienceStoreResponseDto;
import com.cvsgo.dto.product.EventTypeResponseDto;
import com.cvsgo.dto.product.ProductDetailResponseDto;
import com.cvsgo.dto.product.ProductFilterResponseDto;
import com.cvsgo.dto.product.ProductResponseDto;
import com.cvsgo.dto.product.ProductSearchFilter;
import com.cvsgo.dto.product.ProductSearchRequestDto;
import com.cvsgo.dto.product.SearchProductQueryDto;
import com.cvsgo.dto.product.SellAtEventResponseDto;
import com.cvsgo.dto.product.SellAtResponseDto;
import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.ProductLike;
import com.cvsgo.entity.User;
import com.cvsgo.exception.product.DuplicateProductBookmarkException;
import com.cvsgo.exception.product.DuplicateProductLikeException;
import com.cvsgo.exception.product.NotFoundProductException;
import com.cvsgo.exception.product.NotFoundProductLikeException;
import com.cvsgo.repository.CategoryRepository;
import com.cvsgo.repository.ConvenienceStoreRepository;
import com.cvsgo.repository.EventRepository;
import com.cvsgo.repository.ProductBookmarkRepository;
import com.cvsgo.repository.ProductLikeRepository;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.SellAtRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final SellAtRepository sellAtRepository;
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
    public List<ProductResponseDto> getProductList(User user, ProductSearchRequestDto request,
        Pageable pageable) {
        List<SearchProductQueryDto> products = productRepository.searchByFilter(user,
            request, pageable);
        List<Long> productIds = products.stream().map(SearchProductQueryDto::getProductId).toList();

        List<ConvenienceStoreEventQueryDto> convenienceStoreEvents =
            productRepository.findConvenienceStoreEventsByProductIds(productIds);

        Map<Long, List<ConvenienceStoreEventQueryDto>> productCvsEventsMap =
            convenienceStoreEvents.stream().collect(Collectors.groupingBy(
                ConvenienceStoreEventQueryDto::getProductId));

        return products.stream().map(p -> ProductResponseDto.builder()
            .productId(p.getProductId())
            .productName(p.getProductName())
            .productPrice(p.getProductPrice())
            .productImageUrl(p.getProductImageUrl())
            .categoryId(p.getCategoryId())
            .manufacturerName(p.getManufacturerName())
            .isLiked(p.getIsLiked())
            .isBookmarked(p.getIsBookmarked())
            .reviewCount(p.getReviewCount())
            .reviewRating(p.getAvgRating())
            .sellAt(productCvsEventsMap.get(p.getProductId()).stream()
                .map(c -> SellAtResponseDto.of(c.getConvenienceStoreName(), c.getEventType()))
                .toList())
            .build()
        ).toList();
    }

    /**
     * 상품 ID를 통해 상품을 상세 조회한다.
     *
     * @param user      로그인한 사용자
     * @param productId 상품 ID
     * @return 상품 상세 정보
     * @throws NotFoundProductException 해당하는 아이디를 가진 상품이 없는 경우
     */
    @Transactional(readOnly = true)
    public ProductDetailResponseDto readProduct(User user, Long productId) {
        ProductDetailResponseDto product = productRepository.findByProductId(user, productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);
        product.setSellAts(sellAtRepository.findByProductId(productId).stream().map(
            sellAt -> SellAtEventResponseDto.of(sellAt.getConvenienceStore(),
                eventRepository.findByProductAndConvenienceStore(sellAt.getProduct(),
                    sellAt.getConvenienceStore()))).toList());
        return product;
    }

    /**
     * 상품 좋아요를 생성한다.
     *
     * @param user      로그인한 사용자
     * @param productId 상품 ID
     * @throws NotFoundProductException      해당하는 아이디를 가진 상품이 없는 경우
     * @throws DuplicateProductLikeException 이미 해당하는 상품 좋아요가 존재하는 경우
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
     * @throws NotFoundProductException     해당하는 아이디를 가진 상품이 없는 경우
     * @throws NotFoundProductLikeException 해당하는 상품 좋아요가 없는 경우
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
     * @throws NotFoundProductException          해당하는 아이디를 가진 상품이 없는 경우
     * @throws DuplicateProductBookmarkException 이미 해당하는 상품 북마크가 존재하는 경우
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
     * 상품 조회 시 적용할 필터를 조회한다.
     *
     * @return 상품 필터
     */
    @Transactional(readOnly = true)
    public ProductFilterResponseDto getProductFilter() {
        List<ConvenienceStoreResponseDto> convenienceStoreNames = convenienceStoreRepository.findAll()
            .stream().map(ConvenienceStoreResponseDto::from).toList();
        List<CategoryResponseDto> categoryNames = categoryRepository.findAll().stream()
            .map(CategoryResponseDto::from).toList();
        List<EventTypeResponseDto> eventTypes = Arrays.stream(EventType.values())
            .map(EventTypeResponseDto::from).toList();
        Integer highestPrice = productRepository.findFirstByOrderByPriceDesc().getPrice();

        return ProductFilterResponseDto.of(convenienceStoreNames, categoryNames, eventTypes,
            highestPrice);
    }

    private ProductSearchFilter convertRequestToFilter(ProductSearchRequestDto request) {
        List<ConvenienceStore> convenienceStores = convenienceStoreRepository.findAllById(
            request.getConvenienceStoreIds());
        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
        return ProductSearchFilter.of(convenienceStores, categories, request.getEventTypes(),
            request.getLowestPrice(), request.getHighestPrice());
    }

}
