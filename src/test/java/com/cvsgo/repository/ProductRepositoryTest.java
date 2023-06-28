package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.dto.product.ConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.ProductSortBy;
import com.cvsgo.dto.product.ReadLikedProductRequestDto;
import com.cvsgo.dto.product.ReadProductDetailQueryDto;
import com.cvsgo.dto.product.ReadProductQueryDto;
import com.cvsgo.dto.product.ReadProductRequestDto;
import com.cvsgo.entity.BogoEvent;
import com.cvsgo.entity.BtgoEvent;
import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.DiscountEvent;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.Manufacturer;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductLike;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.SellAt;
import com.cvsgo.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

@Import(TestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ManufacturerRepository manufacturerRepository;

    @Autowired
    ConvenienceStoreRepository convenienceStoreRepository;

    @Autowired
    SellAtRepository sellAtRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ProductLikeRepository productLikeRepository;

    User user1;
    User user2;
    Category category1;
    Category category2;
    Product product1;
    Product product2;
    BogoEvent bogoEvent;
    BtgoEvent btgoEvent;
    DiscountEvent discountEvent;
    Review review1;
    Review review2;
    Review review3;

    @BeforeEach
    void initData() {
        user1 = User.create("abc@naver.com", "password1!", "닉네임", new ArrayList<>());
        user2 = User.create("abcd@naver.com", "password1!", "닉네임2", new ArrayList<>());
        userRepository.save(user1);
        userRepository.save(user2);

        category1 = Category.builder()
            .name("유제품")
            .build();
        category2 = Category.builder()
            .name("즉석식품")
            .build();
        categoryRepository.saveAll(List.of(category1, category2));

        Manufacturer manufacturer = Manufacturer.builder()
            .name("칠성")
            .build();
        manufacturerRepository.save(manufacturer);

        product1 = Product.builder()
            .name("상품1")
            .price(1000)
            .category(category1)
            .manufacturer(manufacturer)
            .build();

        product2 = Product.builder()
            .name("상품2")
            .price(5000)
            .category(category1)
            .manufacturer(manufacturer)
            .build();
        productRepository.saveAll(List.of(product1, product2));

        ConvenienceStore cvs = ConvenienceStore.builder()
            .name("GS25")
            .build();
        ConvenienceStore cvs2 = ConvenienceStore.builder()
            .name("CU")
            .build();
        convenienceStoreRepository.saveAll(List.of(cvs, cvs2));

        SellAt sellAt1 = SellAt.builder()
            .product(product1)
            .convenienceStore(cvs)
            .build();
        SellAt sellAt2 = SellAt.builder()
            .product(product1)
            .convenienceStore(cvs2)
            .build();
        SellAt sellAt3 = SellAt.builder()
            .product(product2)
            .convenienceStore(cvs)
            .build();
        sellAtRepository.saveAll(List.of(sellAt1, sellAt2, sellAt3));

        bogoEvent = BogoEvent.builder()
            .convenienceStore(cvs)
            .product(product1)
            .build();
        discountEvent = DiscountEvent.builder()
            .product(product1)
            .convenienceStore(cvs2)
            .discountAmount(500)
            .build();
        btgoEvent = BtgoEvent.builder()
            .convenienceStore(cvs)
            .product(product2)
            .build();
        eventRepository.saveAll(List.of(bogoEvent, discountEvent, btgoEvent));

        review1 = Review.builder()
            .user(user1)
            .product(product1)
            .rating(5)
            .content("맛있어요")
            .imageUrls(new ArrayList<>())
            .build();
        review2 = Review.builder()
            .user(user2)
            .product(product1)
            .rating(3)
            .content("그냥 그래요")
            .imageUrls(new ArrayList<>())
            .build();
        review3 = Review.builder()
            .user(user1)
            .product(product2)
            .rating(2)
            .content("굿")
            .imageUrls(new ArrayList<>())
            .build();
        reviewRepository.saveAll(List.of(review1, review2, review3));
    }

    @Test
    @DisplayName("ID를 통해 상품을 조회한다")
    void succeed_to_find_product_by_id() {
        Optional<Product> foundProduct1 = productRepository.findById(product1.getId());
        Optional<Product> foundProduct2 = productRepository.findById(product2.getId());

        assertThat(foundProduct1.get().getPrice()).isEqualTo(product1.getPrice());
        assertThat(foundProduct2.get().getPrice()).isEqualTo(product2.getPrice());
    }

    @Test
    @DisplayName("상품 판매 편의점을 조회한다")
    void succeed_to_find_first_by_order_by_price_desc() {
        Product highestPriceProduct = productRepository.findFirstByOrderByPriceDesc();

        assertThat(highestPriceProduct.getPrice()).isEqualTo(product2.getPrice());
    }

    @Test
    @DisplayName("상품 목록을 조회한다")
    void succeed_to_find_all_by_filter() {
        // given
        ReadProductRequestDto request = new ReadProductRequestDto(null, null,
            null, null, 0, 1000, null);

        ReadProductQueryDto productResponse1 = new ReadProductQueryDto(product1.getId(),
            product1.getName(), product1.getPrice(), product1.getImageUrl(),
            product1.getCategory().getId(), product1.getManufacturer().getName(), null, null,
            5L, 3.5, 4.5);
        ReadProductQueryDto productResponse2 = new ReadProductQueryDto(product2.getId(),
            product2.getName(), product2.getPrice(), product2.getImageUrl(),
            product2.getCategory().getId(), product2.getManufacturer().getName(), null, null,
            5L, 3.5, 4.5);

        // when
        List<ReadProductQueryDto> foundProducts = productRepository.findAllByFilter(user1, request,
            PageRequest.of(0, 20));

        // then
        List<Long> foundProductIds = foundProducts.stream().map(ReadProductQueryDto::getProductId)
            .toList();
        assertThat(productResponse1.getProductId()).isIn(foundProductIds);
        assertThat(productResponse2.getProductId()).isNotIn(foundProductIds);
    }

    @Test
    @DisplayName("랭킹 내림차순으로 정렬하여 특정 상품을 조회하면 첫번째 상품 랭킹이 마지막 상품 랭킹보다 크거나 같아야 한다")
    void score_of_first_product_should_greater_than_or_equal_to_score_of_last_product_when_sorted_by_score_descending_order() {
        ReadProductRequestDto request = new ReadProductRequestDto(ProductSortBy.SCORE, null,
            null, null, 0, 10000, null);

        List<ReadProductQueryDto> foundProducts = productRepository.findAllByFilter(user1, request,
            PageRequest.of(0, 20));

        assertThat(foundProducts.get(0).getScore()).isGreaterThanOrEqualTo(
            foundProducts.get(foundProducts.size() - 1).getScore());
    }

    @Test
    @DisplayName("별점 내림차순으로 정렬하여 특정 상품을 조회하면 첫번째 상품 평균 별점이 마지막 상품 평균 별점보다 크거나 같아야 한다")
    void rating_of_first_product_should_greater_than_or_equal_to_rating_of_last_product_when_sorted_by_rating_descending_order() {
        ReadProductRequestDto request = new ReadProductRequestDto(ProductSortBy.RATING, null,
            null, null, 0, 1000, null);

        List<ReadProductQueryDto> foundProducts = productRepository.findAllByFilter(user1, request,
            PageRequest.of(0, 20));

        assertThat(foundProducts.get(0).getAvgRating()).isGreaterThanOrEqualTo(
            foundProducts.get(foundProducts.size() - 1).getAvgRating());
    }

    @Test
    @DisplayName("리뷰순으로 정렬하여 특정 상품을 조회하면 첫번째 상품의 리뷰 개수가 마지막 상품의 리뷰 개수보다 크거나 같아야 한다")
    void review_count_of_first_product_should_greater_than_or_equal_to_review_count_of_last_product_when_sorted_by_review_count_descending_order() {
        ReadProductRequestDto request = new ReadProductRequestDto(ProductSortBy.REVIEW_COUNT, null,
            null, null, 0, 1000, null);

        List<ReadProductQueryDto> foundProducts = productRepository.findAllByFilter(user1, request,
            PageRequest.of(0, 20));

        assertThat(foundProducts.get(0).getReviewCount()).isGreaterThanOrEqualTo(
            foundProducts.get(foundProducts.size() - 1).getReviewCount());
    }

    @Test
    @DisplayName("카테고리 필터를 적용하여 특정 상품을 조회하면 해당하는 상품들만 조회된다 ")
    void succeed_to_find_product_by_category_filter() {
        Long categoryId = category1.getId();
        ReadProductRequestDto request = new ReadProductRequestDto(null, null,
            List.of(categoryId), null, 0, 1000, null);

        List<ReadProductQueryDto> foundProducts = productRepository.findAllByFilter(user1, request,
            PageRequest.of(0, 20));

        assertThat(foundProducts).hasSize(1);
        assertThat(foundProducts.get(0).getCategoryId()).isEqualTo(categoryId);
    }

    @Test
    @DisplayName("이벤트 타입 필터를 적용하여 특정 상품을 조회하면 해당하는 상품들만 조회된다 ")
    void succeed_to_find_product_by_event_type_filter() {
        EventType eventType = EventType.BOGO;
        ReadProductRequestDto request = new ReadProductRequestDto(null, null,
            null, List.of(eventType), 0, 1000, null);

        List<ReadProductQueryDto> foundProducts = productRepository.findAllByFilter(user1, request,
            PageRequest.of(0, 20));

        assertThat(foundProducts).hasSize(1);
    }

    @Test
    @DisplayName("키워드를 입력하여 특정 상품을 조회하면 해당하는 상품들만 조회된다 ")
    void succeed_to_find_product_by_keyword_filter() {
        String keyword = "상품1";
        ReadProductRequestDto request = new ReadProductRequestDto(null, null,
            null, null, 0, 1000, keyword);

        List<ReadProductQueryDto> foundProducts = productRepository.findAllByFilter(user1, request,
            PageRequest.of(0, 20));

        assertThat(foundProducts).hasSize(1);
    }

    @Test
    @DisplayName("상품 목록 요소의 전체 개수를 조회한다")
    void succeed_to_count_by_filter() {
        // given
        ReadProductRequestDto request = new ReadProductRequestDto(null, null,
            null, null, 0, 1000, null);

        // when
        Long totalCount = productRepository.countByFilter(request);

        // then
        assertThat(totalCount).isEqualTo(1);
    }

    @Test
    @DisplayName("회원이 좋아요 한 상품 목록을 조회한다")
    void succeed_to_find_all_by_user_not_null() {
        // given
        ReadLikedProductRequestDto request = new ReadLikedProductRequestDto(null);
        ProductLike productLike1 = ProductLike.builder()
            .user(user1)
            .product(product1)
            .build();
        productLikeRepository.save(productLike1);

        ReadProductQueryDto productResponse1 = new ReadProductQueryDto(product1.getId(),
            product1.getName(), product1.getPrice(), product1.getImageUrl(),
            product1.getCategory().getId(), product1.getManufacturer().getName(), productLike1, null,
            5L, 3.5, 4.5);
        ReadProductQueryDto productResponse2 = new ReadProductQueryDto(product2.getId(),
            product2.getName(), product2.getPrice(), product2.getImageUrl(),
            product2.getCategory().getId(), product2.getManufacturer().getName(), null, null,
            5L, 3.5, 4.5);

        // when
        List<ReadProductQueryDto> foundProducts = productRepository.findAllByUser(user1,
            request.getSortBy(), PageRequest.of(0, 20));

        // then
        List<Long> foundProductIds = foundProducts.stream().map(ReadProductQueryDto::getProductId)
            .toList();
        assertThat(productResponse1.getProductId()).isIn(foundProductIds);
        assertThat(productResponse2.getProductId()).isNotIn(foundProductIds);
    }

    @Test
    @DisplayName("상품 ID 리스트에 따른 편의점 행사를 조회한다")
    void succeed_to_find_convenience_store_events_by_product_ids() {
        // given
        ConvenienceStoreEventQueryDto product1CvsEvent1 =
            new ConvenienceStoreEventQueryDto(bogoEvent.getProduct().getId(),
                bogoEvent.getConvenienceStore().getName(), bogoEvent);
        ConvenienceStoreEventQueryDto product1CvsEvent2 =
            new ConvenienceStoreEventQueryDto(discountEvent.getProduct().getId(),
                discountEvent.getConvenienceStore().getName(), discountEvent);
        ConvenienceStoreEventQueryDto product2CvsEvent1 =
            new ConvenienceStoreEventQueryDto(btgoEvent.getProduct().getId(),
                btgoEvent.getConvenienceStore().getName(), btgoEvent);

        // when
        List<ConvenienceStoreEventQueryDto> foundProducts = productRepository.findConvenienceStoreEventsByProductIds(
            List.of(product1.getId(), product2.getId()));

        // then
        assertThat(foundProducts).hasSize(3);
        assertThat(product1CvsEvent1.getProductId()).isIn(
            foundProducts.stream().map(ConvenienceStoreEventQueryDto::getProductId).toList());
        assertThat(product1CvsEvent2.getConvenienceStoreName()).isIn(
            foundProducts.stream().map(ConvenienceStoreEventQueryDto::getConvenienceStoreName)
                .toList());
        assertThat(product2CvsEvent1.getEvent()).isIn(
            foundProducts.stream().map(ConvenienceStoreEventQueryDto::getEvent).toList());
    }

    @Test
    @DisplayName("상품 ID를 통해 상품 정보를 조회한다")
    void succeed_to_find_by_product_id() {
        // given
        ReadProductDetailQueryDto productDetailResponse = new ReadProductDetailQueryDto(
            product1.getId(), product1.getName(), product1.getPrice(), product1.getImageUrl(),
            product1.getManufacturer().getName(), null, null);

        // when
        Optional<ReadProductDetailQueryDto> foundProduct = productRepository.findByProductId(user1,
            product1.getId());

        // then
        assertThat(foundProduct.get().getProductId()).isEqualTo(
            productDetailResponse.getProductId());
    }

    @Test
    @DisplayName("상품 ID에 따른 편의점 행사를 조회한다")
    void succeed_to_find_convenience_store_events_by_product_id() {
        // given
        ConvenienceStoreEventQueryDto product1CvsEvent1 =
            new ConvenienceStoreEventQueryDto(bogoEvent.getProduct().getId(),
                bogoEvent.getConvenienceStore().getName(), bogoEvent);
        ConvenienceStoreEventQueryDto product1CvsEvent2 =
            new ConvenienceStoreEventQueryDto(discountEvent.getProduct().getId(),
                discountEvent.getConvenienceStore().getName(), discountEvent);

        // when
        List<ConvenienceStoreEventQueryDto> foundProducts = productRepository.findConvenienceStoreEventsByProductId(
            product1.getId());

        // then
        assertThat(foundProducts).hasSize(2);
        assertThat(product1CvsEvent1.getConvenienceStoreName()).isIn(
            foundProducts.stream().map(ConvenienceStoreEventQueryDto::getConvenienceStoreName)
                .toList());
        assertThat(product1CvsEvent2.getEvent()).isIn(
            foundProducts.stream().map(ConvenienceStoreEventQueryDto::getEvent).toList());
    }

}