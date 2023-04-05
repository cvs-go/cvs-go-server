package com.cvsgo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;

import com.cvsgo.dto.product.ConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.ProductDetailResponseDto;
import com.cvsgo.dto.product.ProductResponseDto;
import com.cvsgo.dto.product.SearchProductRequestDto;
import com.cvsgo.dto.product.SearchProductQueryDto;
import com.cvsgo.entity.BogoEvent;
import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.Manufacturer;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.ProductLike;
import com.cvsgo.entity.SellAt;
import com.cvsgo.entity.User;
import com.cvsgo.exception.product.NotFoundProductBookmarkException;
import com.cvsgo.exception.product.NotFoundProductException;
import com.cvsgo.exception.product.NotFoundProductLikeException;
import com.cvsgo.repository.CategoryRepository;
import com.cvsgo.repository.ConvenienceStoreRepository;
import com.cvsgo.repository.EventRepository;
import com.cvsgo.repository.ProductBookmarkRepository;
import com.cvsgo.repository.ProductLikeRepository;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.SellAtRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
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

    @Mock
    private ProductLikeRepository productLikeRepository;

    @Mock
    private ProductBookmarkRepository productBookmarkRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 목록을 정상적으로 조회한다")
    void succeed_to_read_product_list() {
        Pageable pageable = PageRequest.of(0, 20);
        SearchProductRequestDto request = SearchProductRequestDto.builder()
            .convenienceStoreIds(List.of(1L))
            .categoryIds(List.of(1L))
            .eventTypes(List.of(EventType.BOGO))
            .lowestPrice(0)
            .highestPrice(1000)
            .build();

        given(productRepository.searchByFilter(any(), any(), any())).willReturn(getProductList());
        given(productRepository.countByFilter(any())).willReturn((long) getProductList().size());
        given(productRepository.findConvenienceStoreEventsByProductIds(anyList())).willReturn(getCvsEventList());

        Page<ProductResponseDto> result = productService.readProductList(user, request, pageable);
        assertEquals(result.getTotalElements(), getProductList().size());

        then(productRepository).should(times(1)).searchByFilter(any(), any(), any());
        then(productRepository).should(times(1)).countByFilter(any());
        then(productRepository).should(times(1)).findConvenienceStoreEventsByProductIds(anyList());
    }

    @Test
    @DisplayName("상품을 정상적으로 조회한다")
    void succeed_to_read_product() {
        given(productRepository.findByProductId(any(), any())).willReturn(
            Optional.of(ProductDetailResponseDto.of(product1, manufacturer1, productLike,
                productBookmark)));
        given(sellAtRepository.findByProductId(any())).willReturn(List.of(sellAt1));
        given(eventRepository.findByProductAndConvenienceStore(any(), any())).willReturn(bogoEvent);

        productService.readProduct(user, 1L);

        then(productRepository).should(times(1)).findByProductId(any(), any());
        then(sellAtRepository).should(times(1)).findByProductId(any());
        then(eventRepository).should(times(1)).findByProductAndConvenienceStore(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 상품이면 NotFoundProductException이 발생한다")
    void should_throw_NotFoundProductException_when_product_does_not_exist() {
        given(productRepository.findByProductId(any(), any())).willReturn(Optional.empty());

        assertThrows(NotFoundProductException.class, () -> productService.readProduct(user, 1000L));

        then(productRepository).should(times(1)).findByProductId(any(), any());
    }

    @Test
    @DisplayName("상품 좋아요를 정상적으로 생성한다")
    void succeed_to_create_product_like() {
        given(productRepository.findByIdWithOptimisticLock(anyLong())).willReturn(Optional.of(product1));
        given(productLikeRepository.save(any())).willReturn(any());

        Long beforeLikeCount = product1.getLikeCount();
        productService.createProductLike(user, 1L);
        Long afterLikeCount = product1.getLikeCount();

        then(productRepository).should(times(1)).findByIdWithOptimisticLock(1L);
        then(productLikeRepository).should(times(1)).save(any());
        assertThat(afterLikeCount).isSameAs(beforeLikeCount + 1);
    }

    @Test
    @DisplayName("상품 좋아요 생성 API를 조회했을 때 해당 ID의 상품이 없는 경우 NotFoundProductException이 발생한다")
    void should_throw_NotFoundProductException_when_create_product_like_and_product_does_not_exist() {
        given(productRepository.findByIdWithOptimisticLock(anyLong())).willThrow(NotFoundProductException.class);

        assertThrows(NotFoundProductException.class,
            () -> productService.createProductLike(user, 1000L));

        then(productRepository).should(times(1)).findByIdWithOptimisticLock(any());
    }

    @Test
    @DisplayName("상품 좋아요를 정상적으로 삭제한다")
    void succeed_to_delete_product_like() {
        given(productRepository.findByIdWithOptimisticLock(anyLong())).willReturn(Optional.of(product1));
        given(productLikeRepository.findByProductAndUser(any(), any())).willReturn(
            Optional.of(productLike));

        Long beforeLikeCount = product1.getLikeCount();
        productService.deleteProductLike(user, 1L);
        Long afterLikeCount = product1.getLikeCount();

        then(productRepository).should(times(1)).findByIdWithOptimisticLock(1L);
        then(productLikeRepository).should(times(1)).findByProductAndUser(any(), any());
        then(productLikeRepository).should(times(1)).delete(any());
        assertThat(afterLikeCount).isSameAs(beforeLikeCount - 1);
    }

    @Test
    @DisplayName("상품 좋아요 삭제 API를 조회했을 때 해당 ID의 상품이 없는 경우 NotFoundProductException이 발생한다")
    void should_throw_NotFoundProductException_when_delete_product_like_and_product_does_not_exist() {
        given(productRepository.findByIdWithOptimisticLock(anyLong())).willThrow(NotFoundProductException.class);

        assertThrows(NotFoundProductException.class,
            () -> productService.deleteProductLike(user, 1000L));

        then(productRepository).should(times(1)).findByIdWithOptimisticLock(any());
    }


    @Test
    @DisplayName("해당하는 상품 좋아요가 없는 경우 NotFoundProductLikeException이 발생한다")
    void should_throw_NotFoundProductLikeException_when_product_like_does_not_exist() {
        given(productRepository.findByIdWithOptimisticLock(anyLong())).willReturn(Optional.of(product1));
        given(productLikeRepository.findByProductAndUser(any(), any())).willReturn(
            Optional.empty());

        assertThrows(NotFoundProductLikeException.class,
            () -> productService.deleteProductLike(user, 1L));

        then(productRepository).should(times(1)).findByIdWithOptimisticLock(any());
        then(productLikeRepository).should(times(1)).findByProductAndUser(any(), any());
    }

    @Test
    @DisplayName("상품 북마크를 정상적으로 생성한다")
    void succeed_to_create_product_bookmark() {
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product1));
        given(productBookmarkRepository.save(any())).willReturn(any());

        productService.createProductBookmark(user, 1L);

        then(productRepository).should(times(1)).findById(1L);
        then(productBookmarkRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("상품 북마크 생성 API를 조회했을 때 해당 ID의 상품이 없는 경우 NotFoundProductException이 발생한다")
    void should_throw_NotFoundProductException_when_create_product_bookmark_and_product_does_not_exist() {
        given(productRepository.findById(anyLong())).willThrow(NotFoundProductException.class);

        assertThrows(NotFoundProductException.class,
            () -> productService.createProductBookmark(user, 1000L));

        then(productRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("상품 북마크를 정상적으로 삭제한다")
    void succeed_to_delete_product_bookmark() {
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product1));
        given(productBookmarkRepository.findByProductAndUser(any(), any())).willReturn(
            Optional.of(productBookmark));

        productService.deleteProductBookmark(user, 1L);

        then(productRepository).should(times(1)).findById(1L);
        then(productBookmarkRepository).should(times(1)).findByProductAndUser(any(), any());
        then(productBookmarkRepository).should(times(1)).delete(any());
    }

    @Test
    @DisplayName("상품 북마크 삭제 API를 조회했을 때 해당 ID의 상품이 없는 경우 NotFoundProductException이 발생한다")
    void should_throw_NotFoundProductException_when_delete_product_bookmark_and_product_does_not_exist() {
        given(productRepository.findById(anyLong())).willThrow(NotFoundProductException.class);

        assertThrows(NotFoundProductException.class,
            () -> productService.deleteProductBookmark(user, 1000L));

        then(productRepository).should(times(1)).findById(any());
    }


    @Test
    @DisplayName("해당하는 상품 북마크가 없는 경우 NotFoundProductBookmarkException이 발생한다")
    void should_throw_NotFoundProductBookmarkException_when_product_bookmark_does_not_exist() {
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product1));
        given(productBookmarkRepository.findByProductAndUser(any(), any())).willReturn(
            Optional.empty());

        assertThrows(NotFoundProductBookmarkException.class,
            () -> productService.deleteProductBookmark(user, 1L));

        then(productRepository).should(times(1)).findById(any());
        then(productBookmarkRepository).should(times(1)).findByProductAndUser(any(), any());
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

    ProductLike productLike = ProductLike.builder()
        .user(user)
        .product(product1)
        .build();

    ProductBookmark productBookmark = ProductBookmark.builder()
        .user(user)
        .product(product1)
        .build();

    SearchProductQueryDto productResponse1 = new SearchProductQueryDto(product1.getId(),
        product1.getName(), product1.getPrice(), product1.getImageUrl(),
        product1.getCategory().getId(), product1.getManufacturer().getName(), productLike, productBookmark,
        5L, 3.5, 4.5);

    ConvenienceStoreEventQueryDto cvsEvent1 =
        new ConvenienceStoreEventQueryDto(productResponse1.getProductId(),
            "GS25", EventType.BOGO);

    ConvenienceStoreEventQueryDto cvsEvent2 =
        new ConvenienceStoreEventQueryDto(productResponse1.getProductId(),
            "CU", EventType.DISCOUNT);

    ConvenienceStoreEventQueryDto cvsEvent3 =
        new ConvenienceStoreEventQueryDto(productResponse1.getProductId(),
            "Emart24", null);

    private List<SearchProductQueryDto> getProductList() {
        return List.of(productResponse1);
    }

    private List<ConvenienceStoreEventQueryDto> getCvsEventList() {
        return List.of(cvsEvent1, cvsEvent2, cvsEvent3);
    }

}
