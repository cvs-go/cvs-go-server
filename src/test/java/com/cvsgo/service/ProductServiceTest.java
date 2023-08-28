package com.cvsgo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;

import com.cvsgo.dto.product.ConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.ProductSortBy;
import com.cvsgo.dto.product.ReadProductLikeTagResponseDto;
import com.cvsgo.dto.product.ReadUserProductRequestDto;
import com.cvsgo.dto.product.ReadProductDetailQueryDto;
import com.cvsgo.dto.product.ReadProductQueryDto;
import com.cvsgo.dto.product.ReadProductRequestDto;
import com.cvsgo.dto.product.ReadProductResponseDto;
import com.cvsgo.entity.BogoEvent;
import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.DiscountEvent;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.Manufacturer;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.ProductLike;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.exception.DuplicateException;
import com.cvsgo.exception.NotFoundException;
import com.cvsgo.repository.CategoryRepository;
import com.cvsgo.repository.ConvenienceStoreRepository;
import com.cvsgo.repository.ProductBookmarkRepository;
import com.cvsgo.repository.ProductLikeRepository;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.TagRepository;
import com.cvsgo.repository.UserRepository;
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
    private ConvenienceStoreRepository convenienceStoreRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ProductLikeRepository productLikeRepository;

    @Mock
    private ProductBookmarkRepository productBookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 목록을 정상적으로 조회한다")
    void succeed_to_read_product_list() {
        Pageable pageable = PageRequest.of(0, 20);
        ReadProductRequestDto request = new ReadProductRequestDto(ProductSortBy.SCORE,
            List.of(1L), List.of(1L), List.of(EventType.BOGO), 0, 10000, null);

        given(productRepository.findAllByFilter(any(), any(), any())).willReturn(getProductList());
        given(productRepository.countByFilter(any())).willReturn((long) getProductList().size());
        given(productRepository.findConvenienceStoreEventsByProductIds(anyList())).willReturn(getCvsEventList());

        Page<ReadProductResponseDto> result = productService.readProductList(user, request, pageable);
        assertEquals(result.getTotalElements(), getProductList().size());

        then(productRepository).should(times(1)).findAllByFilter(any(), any(), any());
        then(productRepository).should(times(1)).countByFilter(any());
        then(productRepository).should(times(1)).findConvenienceStoreEventsByProductIds(anyList());
    }

    @Test
    @DisplayName("상품을 정상적으로 조회한다")
    void succeed_to_read_product() {
        given(productRepository.findByProductId(any(), any())).willReturn(Optional.of(productDetailResponse));
        given(productRepository.findConvenienceStoreEventsByProductId(any())).willReturn(getCvsEventList());

        productService.readProduct(user, 1L);

        then(productRepository).should(times(1)).findByProductId(any(), any());
        then(productRepository).should(times(1)).findConvenienceStoreEventsByProductId(any());
    }

    @Test
    @DisplayName("상품 조회 시 존재하지 않는 상품이면 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_read_product_but_product_does_not_exist() {
        given(productRepository.findByProductId(any(), any())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.readProduct(user, 1000L));

        then(productRepository).should(times(1)).findByProductId(any(), any());
    }

    @Test
    @DisplayName("상품 좋아요 상위 태그 3개를 정상적으로 조회한다")
    void succeed_to_read_product_like_tags() {
        given(productRepository.findById(any())).willReturn(Optional.of(product1));
        given(tagRepository.findTop3ByProduct(any())).willReturn(getProductLikeTagsResponse());

        List<ReadProductLikeTagResponseDto> result = productService.readProductLikeTags(product1.getId());
        assertThat(result).hasSize(3);

        then(productRepository).should(times(1)).findById(any());
        then(tagRepository).should(times(1)).findTop3ByProduct(any());
    }

    @Test
    @DisplayName("상품 좋아요 상위 태그 조회 시 존재하지 않는 상품이면 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_read_product_like_tags_but_product_does_not_exist() {
        given(productRepository.findById(any())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.readProductLikeTags(1000L));

        then(productRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("상품 좋아요를 정상적으로 생성한다")
    void succeed_to_create_product_like() {
        given(productRepository.findByIdWithOptimisticLock(anyLong())).willReturn(Optional.of(product1));
        given(productLikeRepository.existsByProductAndUser(any(), any())).willReturn(false);
        given(productLikeRepository.save(any())).willReturn(any());

        Long beforeLikeCount = product1.getLikeCount();
        productService.createProductLike(user, 1L);
        Long afterLikeCount = product1.getLikeCount();

        then(productRepository).should(times(1)).findByIdWithOptimisticLock(1L);
        then(productLikeRepository).should(times(1)).existsByProductAndUser(any(), any());
        then(productLikeRepository).should(times(1)).save(any());
        assertThat(afterLikeCount).isSameAs(beforeLikeCount + 1);
    }

    @Test
    @DisplayName("상품 좋아요 생성 시 해당 ID의 상품이 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_create_product_like_but_product_does_not_exist() {
        given(productRepository.findByIdWithOptimisticLock(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> productService.createProductLike(user, 1000L));

        then(productRepository).should(times(1)).findByIdWithOptimisticLock(any());
    }

    @Test
    @DisplayName("상품 좋아요 생성 시 이미 해당하는 상품 좋아요가 있을 경우 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_product_like_but_product_like_already_exists() {
        given(productRepository.findByIdWithOptimisticLock(anyLong())).willReturn(Optional.of(product1));
        given(productLikeRepository.existsByProductAndUser(any(), any())).willReturn(true);

        assertThrows(DuplicateException.class, () -> productService.createProductLike(user, 1L));

        then(productRepository).should(times(1)).findByIdWithOptimisticLock(any());
        then(productLikeRepository).should(times(1)).existsByProductAndUser(any(), any());
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
    @DisplayName("상품 좋아요 삭제 시 해당 ID의 상품이 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_delete_product_like_but_product_does_not_exist() {
        given(productRepository.findByIdWithOptimisticLock(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> productService.deleteProductLike(user, 1000L));

        then(productRepository).should(times(1)).findByIdWithOptimisticLock(any());
    }


    @Test
    @DisplayName("상품 좋아요 삭제 시 해당하는 상품 좋아요가 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_delete_product_like_but_product_like_does_not_exist() {
        given(productRepository.findByIdWithOptimisticLock(anyLong())).willReturn(Optional.of(product1));
        given(productLikeRepository.findByProductAndUser(any(), any())).willReturn(
            Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.deleteProductLike(user, 1L));

        then(productRepository).should(times(1)).findByIdWithOptimisticLock(any());
        then(productLikeRepository).should(times(1)).findByProductAndUser(any(), any());
    }

    @Test
    @DisplayName("상품 북마크를 정상적으로 생성한다")
    void succeed_to_create_product_bookmark() {
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product1));
        given(productBookmarkRepository.existsByProductAndUser(any(), any())).willReturn(false);
        given(productBookmarkRepository.save(any())).willReturn(any());

        productService.createProductBookmark(user, 1L);

        then(productRepository).should(times(1)).findById(1L);
        then(productBookmarkRepository).should(times(1)).existsByProductAndUser(any(), any());
        then(productBookmarkRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("상품 북마크 생성 시 해당 ID의 상품이 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_create_product_bookmark_but_product_does_not_exist() {
        given(productRepository.findById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> productService.createProductBookmark(user, 1000L));

        then(productRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("상품 북마크 생성 시 이미 해당하는 상품 북마크가 있을 경우 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_product_bookmark_but_product_bookmark_already_exists() {
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product1));
        given(productBookmarkRepository.existsByProductAndUser(any(), any())).willReturn(true);

        assertThrows(DuplicateException.class, () -> productService.createProductBookmark(user, 1L));

        then(productRepository).should(times(1)).findById(1L);
        then(productBookmarkRepository).should(times(1)).existsByProductAndUser(any(), any());
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
    @DisplayName("상품 북마크 삭제 시 해당 ID의 상품이 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_delete_product_bookmark_but_product_does_not_exist() {
        given(productRepository.findById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
            () -> productService.deleteProductBookmark(user, 1000L));

        then(productRepository).should(times(1)).findById(any());
    }


    @Test
    @DisplayName("상품 북마크 삭제 시 해당하는 상품 북마크가 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_delete_product_bookmark_but_product_bookmark_does_not_exist() {
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product1));
        given(productBookmarkRepository.findByProductAndUser(any(), any())).willReturn(
            Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.deleteProductBookmark(user, 1L));

        then(productRepository).should(times(1)).findById(any());
        then(productBookmarkRepository).should(times(1)).findByProductAndUser(any(), any());
    }

    @Test
    @DisplayName("상품 필터를 정상적으로 조회한다")
    void succeed_to_read_product_filter() {
        given(convenienceStoreRepository.findAll()).willReturn(List.of(cvs1));
        given(categoryRepository.findAll()).willReturn(List.of(category1));
        given(productRepository.findFirstByOrderByPriceDesc()).willReturn(product1);

        productService.readProductFilter();

        then(convenienceStoreRepository).should(atLeastOnce()).findAll();
        then(categoryRepository).should(atLeastOnce()).findAll();
        then(productRepository).should(atLeastOnce()).findFirstByOrderByPriceDesc();
    }

    @Test
    @DisplayName("특정 회원의 상품 좋아요 목록을 정상적으로 조회한다")
    void succeed_to_read_liked_product_list_user_not_null() {
        Pageable pageable = PageRequest.of(0, 20);
        ReadUserProductRequestDto request = new ReadUserProductRequestDto(ProductSortBy.SCORE);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(productRepository.findAllByUserProductLike(any(), any(), any())).willReturn(getProductList());
        given(productRepository.countByUserProductLike(any())).willReturn((long) getProductList().size());
        given(productRepository.findConvenienceStoreEventsByProductIds(anyList())).willReturn(getCvsEventList());

        Page<ReadProductResponseDto> result = productService.readLikedProductList(user.getId(), request, pageable);
        assertEquals(result.getTotalElements(), getProductList().size());

        then(userRepository).should(times(1)).findById(any());
        then(productRepository).should(times(1)).findAllByUserProductLike(any(), any(), any());
        then(productRepository).should(times(1)).countByUserProductLike(any());
        then(productRepository).should(times(1)).findConvenienceStoreEventsByProductIds(anyList());
    }

    @Test
    @DisplayName("특정 회원의 상품 좋아요 목록 조회 시 해당 회원이 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_read_liked_product_but_user_does_not_exist() {
        Pageable pageable = PageRequest.of(0, 20);
        ReadUserProductRequestDto request = new ReadUserProductRequestDto(ProductSortBy.SCORE);

        given(userRepository.findById(any())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.readLikedProductList(1000L, request, pageable));

        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("특정 회원의 상품 북마크 목록을 정상적으로 조회한다")
    void succeed_to_read_bookmarked_product_list_user_not_null() {
        Pageable pageable = PageRequest.of(0, 20);
        ReadUserProductRequestDto request = new ReadUserProductRequestDto(ProductSortBy.SCORE);

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(productRepository.findAllByUserProductBookmark(any(), any(), any())).willReturn(getProductList());
        given(productRepository.countByUserProductBookmark(any())).willReturn((long) getProductList().size());
        given(productRepository.findConvenienceStoreEventsByProductIds(anyList())).willReturn(getCvsEventList());

        Page<ReadProductResponseDto> result = productService.readBookmarkedProductList(user.getId(), request, pageable);
        assertEquals(result.getTotalElements(), getProductList().size());

        then(userRepository).should(times(1)).findById(any());
        then(productRepository).should(times(1)).findAllByUserProductBookmark(any(), any(), any());
        then(productRepository).should(times(1)).countByUserProductBookmark(any());
        then(productRepository).should(times(1)).findConvenienceStoreEventsByProductIds(anyList());
    }

    @Test
    @DisplayName("특정 회원의 상품 북마크 목록 조회 시 해당 회원이 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_read_bookmarked_product_but_user_does_not_exist() {
        Pageable pageable = PageRequest.of(0, 20);
        ReadUserProductRequestDto request = new ReadUserProductRequestDto(ProductSortBy.SCORE);

        given(userRepository.findById(any())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.readBookmarkedProductList(1000L, request, pageable));

        then(userRepository).should(times(1)).findById(any());
    }

    Tag tag1 = Tag.builder()
        .id(1L)
        .name("맵부심")
        .build();

    Tag tag2 = Tag.builder()
        .id(2L)
        .name("다이어터")
        .build();

    Tag tag3 = Tag.builder()
        .id(3L)
        .name("초코러버")
        .build();

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

    ConvenienceStore cvs2 = ConvenienceStore.builder()
        .name("GS25")
        .build();

    BogoEvent bogoEvent = BogoEvent.builder()
        .product(product1)
        .convenienceStore(cvs1)
        .build();

    DiscountEvent discountEvent = DiscountEvent.builder()
        .product(product1)
        .convenienceStore(cvs2)
        .discountAmount(100)
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

    ReadProductQueryDto productResponse1 = new ReadProductQueryDto(product1.getId(),
        product1.getName(), product1.getPrice(), product1.getImageUrl(),
        product1.getCategory().getId(), product1.getManufacturer().getName(), productLike, productBookmark,
        5L, 3.5, 4.5);

    ReadProductDetailQueryDto productDetailResponse = new ReadProductDetailQueryDto(product1.getId(),
        product1.getName(), product1.getPrice(), product1.getImageUrl(),
        product1.getManufacturer().getName(), productLike, productBookmark);

    ConvenienceStoreEventQueryDto cvsEvent1 =
        new ConvenienceStoreEventQueryDto(productResponse1.getProductId(),
            "GS25", bogoEvent);

    ConvenienceStoreEventQueryDto cvsEvent2 =
        new ConvenienceStoreEventQueryDto(productResponse1.getProductId(),
            "CU", discountEvent);

    ConvenienceStoreEventQueryDto cvsEvent3 =
        new ConvenienceStoreEventQueryDto(productResponse1.getProductId(),
            "Emart24", null);

    private List<ReadProductQueryDto> getProductList() {
        return List.of(productResponse1);
    }

    private List<ConvenienceStoreEventQueryDto> getCvsEventList() {
        return List.of(cvsEvent1, cvsEvent2, cvsEvent3);
    }

    private List<ReadProductLikeTagResponseDto> getProductLikeTagsResponse() {
        ReadProductLikeTagResponseDto productLikeTagResponseDto1 = new ReadProductLikeTagResponseDto(
            tag1.getId(), tag1.getName(), 215L
        );
        ReadProductLikeTagResponseDto productLikeTagResponseDto2 = new ReadProductLikeTagResponseDto(
            tag2.getId(), tag2.getName(), 163L
        );
        ReadProductLikeTagResponseDto productLikeTagResponseDto3 = new ReadProductLikeTagResponseDto(
            tag3.getId(), tag3.getName(), 71L
        );
        return List.of(productLikeTagResponseDto1, productLikeTagResponseDto2, productLikeTagResponseDto3);
    }

}
