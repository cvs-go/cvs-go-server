package com.cvsgo.service;

import com.cvsgo.dto.product.CategoryResponseDto;
import com.cvsgo.dto.product.ConvenienceStoreResponseDto;
import com.cvsgo.dto.product.ProductFilterResponseDto;
import com.cvsgo.dto.product.ProductResponseDto;
import com.cvsgo.dto.product.ProductSearchFilter;
import com.cvsgo.dto.product.ProductSearchRequestDto;
import com.cvsgo.dto.product.SellAtResponseDto;
import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.User;
import com.cvsgo.repository.CategoryRepository;
import com.cvsgo.repository.ConvenienceStoreRepository;
import com.cvsgo.repository.EventRepository;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.SellAtRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    /**
     * 사용자가 적용한 필터를 적용해 상품을 조회한다.
     *
     * @param request 사용자가 적용한 필터 dto
     * @return 상품 목록
     */
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductList(User user,
        ProductSearchRequestDto request, Pageable pageable) {
        Page<ProductResponseDto> products = productRepository.searchByFilter(user,
            convertRequestToFilter(request), pageable);
        products.stream().forEach(productResponseDto -> productResponseDto.setSellAt(
            sellAtRepository.findByProductId(productResponseDto.getProductId()).stream().map(
                sellAt -> SellAtResponseDto.of(sellAt.getConvenienceStore(),
                    eventRepository.findByProductAndConvenienceStore(sellAt.getProduct(),
                        sellAt.getConvenienceStore()))).toList()));
        return products;
    }

    /**
     * 상품 조회 시 적용할 필터를 조회한다.
     * @return 상품 필터
     */
    @Transactional(readOnly = true)
    public ProductFilterResponseDto getProductFilter() {
        List<ConvenienceStoreResponseDto> convenienceStoreNames = convenienceStoreRepository.findAll()
            .stream().map(ConvenienceStoreResponseDto::from).toList();
        List<CategoryResponseDto> categoryNames = categoryRepository.findAll().stream()
            .map(CategoryResponseDto::from).toList();
        EventType[] eventTypes = EventType.values();
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
