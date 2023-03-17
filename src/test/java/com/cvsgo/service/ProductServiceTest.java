package com.cvsgo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;

import com.cvsgo.dto.product.ProductResponseDto;
import com.cvsgo.dto.product.ProductSearchRequestDto;
import com.cvsgo.entity.BogoEvent;
import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.EventType.Values;
import com.cvsgo.entity.Manufacturer;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.SellAt;
import com.cvsgo.entity.User;
import com.cvsgo.repository.CategoryRepository;
import com.cvsgo.repository.ConvenienceStoreRepository;
import com.cvsgo.repository.EventRepository;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.SellAtRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SellAtRepository sellAtRepository;

    @Mock
    private ConvenienceStoreRepository convenienceStoreRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 목록을 정상적으로 조회한다")
    void succeed_to_read_product_list() {
        Pageable pageable = PageRequest.of(0, 20);
        ProductSearchRequestDto request = ProductSearchRequestDto.builder()
            .convenienceStoreIds(List.of(1L))
            .categoryIds(List.of(1L))
            .eventTypes(List.of(EventType.BOGO))
            .lowestPrice(0)
            .highestPrice(1000)
            .build();

        given(productRepository.searchByFilter(any(), any(), any())).willReturn(getProductList());
        given(sellAtRepository.findByProductId(any())).willReturn(List.of(sellAt1));
        given(eventRepository.findByProductAndConvenienceStore(any(), any())).willReturn(bogoEvent);

        Page<ProductResponseDto> result = productService.getProductList(user, request, pageable);

        assertEquals(result.getTotalElements(), getProductList().getTotalElements());
        then(productRepository).should(times(1)).searchByFilter(any(), any(), any());
        then(eventRepository).should(times(1)).findByProductAndConvenienceStore(any(), any());
    }

    @Test
    @DisplayName("상품 필터를 정상적으로 조회한다")
    void succeed_to_read_product_filter() {
        given(convenienceStoreRepository.findAll()).willReturn(List.of(cvs1));
        given(categoryRepository.findAll()).willReturn(List.of(category1));
        given(productRepository.findFirstByOrderByPriceDesc()).willReturn(product1);

        productService.getProductFilter();

        then(convenienceStoreRepository).should(atLeastOnce()).findAll();
        then(categoryRepository).should(atLeastOnce()).findAll();
        then(productRepository).should(atLeastOnce()).findFirstByOrderByPriceDesc();
    }

    Category category1 = Category.builder()
        .id(1L)
        .name("아이스크림")
        .build();

    Manufacturer manufacturer1 = Manufacturer.builder()
        .name("롯데")
        .build();

    Product product1 = Product.builder()
        .id(1L)
        .name("월드콘")
        .price(2000)
        .category(category1)
        .imageUrl("")
        .manufacturer(manufacturer1)
        .build();

    ConvenienceStore cvs1 = ConvenienceStore.builder()
        .name("CU")
        .build();

    BogoEvent bogoEvent = BogoEvent.builder()
        .product(product1)
        .convenienceStore(cvs1)
        .build();

    SellAt sellAt1 = SellAt.builder()
        .product(product1)
        .convenienceStore(cvs1)
        .build();

    User user = User.builder().build();

    private Page<ProductResponseDto> getProductList() {
        Pageable pageable = PageRequest.of(0, 20);
        ProductResponseDto productResponse1 = ProductResponseDto.of(product1, 1, null, 13L, 2.5);
        return new PageImpl<>(List.of(productResponse1), pageable, 2);
    }

}
