package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.dto.product.ConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.SearchProductDetailQueryDto;
import com.cvsgo.dto.product.SearchProductQueryDto;
import com.cvsgo.dto.product.SearchProductRequestDto;
import com.cvsgo.entity.BogoEvent;
import com.cvsgo.entity.BtgoEvent;
import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.DiscountEvent;
import com.cvsgo.entity.Manufacturer;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.SellAt;
import com.cvsgo.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Test
    @DisplayName("ID를 통해 상품을 조회한다")
    void succeed_to_find_product_by_id() {
        // given
        Product product1 = Product.builder()
            .id(1L)
            .name("상품1")
            .price(1000)
            .build();

        Product product2 = Product.builder()
            .id(2L)
            .name("상품2")
            .price(5000)
            .build();

        productRepository.saveAll(List.of(product1, product2));

        Optional<Product> foundProduct1 = productRepository.findById(product1.getId());
        Optional<Product> foundProduct2 = productRepository.findById(product2.getId());

        assertThat(foundProduct1.get().getPrice()).isEqualTo(product1.getPrice());
        assertThat(foundProduct2.get().getPrice()).isEqualTo(product2.getPrice());
    }

    @Test
    @DisplayName("상품 판매 편의점을 조회한다")
    void succeed_to_find_first_by_order_by_price_desc() {
        // given
        Product product1 = Product.builder()
            .name("상품1")
            .price(1000)
            .build();

        Product product2 = Product.builder()
            .name("상품2")
            .price(5000)
            .build();

        productRepository.saveAll(List.of(product1, product2));

        // when
        Product highestPriceProduct = productRepository.findFirstByOrderByPriceDesc();

        // then
        assertThat(highestPriceProduct.getPrice()).isEqualTo(product2.getPrice());
    }

    @Test
    @DisplayName("상품 판매 편의점 목록을 조회한다")
    void succeed_to_search_by_filter() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        User user = User.builder()
            .userId("abc@gmail.com")
            .password("12345678a!")
            .nickname("닉네임1")
            .role(Role.ASSOCIATE)
            .build();

        Category category = Category.builder()
            .name("유제품")
            .build();

        Manufacturer manufacturer = Manufacturer.builder()
            .name("칠성")
            .build();

        Product product1 = Product.builder()
            .name("상품1")
            .price(1000)
            .category(category)
            .manufacturer(manufacturer)
            .build();

        Product product2 = Product.builder()
            .name("상품2")
            .price(5000)
            .category(category)
            .manufacturer(manufacturer)
            .build();

        ConvenienceStore cvs = ConvenienceStore.builder()
            .name("GS25")
            .build();

        SellAt sellAt1 = SellAt.builder()
            .product(product1)
            .convenienceStore(cvs)
            .build();

        SellAt sellAt2 = SellAt.builder()
            .product(product2)
            .convenienceStore(cvs)
            .build();

        BogoEvent bogoEvent = BogoEvent.builder()
            .convenienceStore(cvs)
            .product(product1)
            .build();

        BtgoEvent btgoEvent = BtgoEvent.builder()
            .convenienceStore(cvs)
            .product(product2)
            .build();

        userRepository.save(user);
        categoryRepository.save(category);
        manufacturerRepository.save(manufacturer);
        productRepository.saveAll(List.of(product1, product2));
        convenienceStoreRepository.save(cvs);
        sellAtRepository.saveAll(List.of(sellAt1, sellAt2));
        eventRepository.saveAll(List.of(bogoEvent, btgoEvent));

        SearchProductRequestDto request = SearchProductRequestDto.builder()
            .convenienceStoreIds(null)
            .categoryIds(null)
            .eventTypes(null)
            .lowestPrice(0)
            .highestPrice(1000)
            .keyword(null)
            .build();

        SearchProductQueryDto productResponse1 = new SearchProductQueryDto(product1.getId(),
            product1.getName(), product1.getPrice(), product1.getImageUrl(),
            product1.getCategory().getId(), product1.getManufacturer().getName(), null, null,
            5L, 3.5, 4.5);
        SearchProductQueryDto productResponse2 = new SearchProductQueryDto(product2.getId(),
            product2.getName(), product2.getPrice(), product2.getImageUrl(),
            product2.getCategory().getId(), product2.getManufacturer().getName(), null, null,
            5L, 3.5, 4.5);

        // when
        List<SearchProductQueryDto> foundProducts = productRepository.searchByFilter(user, request,
            pageable);
        List<Long> foundProductIds = foundProducts.stream().map(SearchProductQueryDto::getProductId)
            .toList();

        // then
        assertThat(productResponse1.getProductId()).isIn(foundProductIds);
        assertThat(productResponse2.getProductId()).isNotIn(foundProductIds);
    }

    @Test
    @DisplayName("상품 판매 편의점 목록 요소의 전체 개수를 조회한다")
    void succeed_to_count_by_filter() {
        // given
        Category category = Category.builder()
            .name("유제품")
            .build();

        Manufacturer manufacturer = Manufacturer.builder()
            .name("칠성")
            .build();

        Product product1 = Product.builder()
            .name("상품1")
            .price(1000)
            .category(category)
            .manufacturer(manufacturer)
            .build();

        Product product2 = Product.builder()
            .name("상품2")
            .price(5000)
            .category(category)
            .manufacturer(manufacturer)
            .build();

        ConvenienceStore cvs = ConvenienceStore.builder()
            .name("GS25")
            .build();

        SellAt sellAt1 = SellAt.builder()
            .product(product1)
            .convenienceStore(cvs)
            .build();

        SellAt sellAt2 = SellAt.builder()
            .product(product2)
            .convenienceStore(cvs)
            .build();

        BogoEvent bogoEvent = BogoEvent.builder()
            .convenienceStore(cvs)
            .product(product1)
            .build();

        BtgoEvent btgoEvent = BtgoEvent.builder()
            .convenienceStore(cvs)
            .product(product2)
            .build();

        categoryRepository.save(category);
        manufacturerRepository.save(manufacturer);
        productRepository.saveAll(List.of(product1, product2));
        convenienceStoreRepository.save(cvs);
        sellAtRepository.saveAll(List.of(sellAt1, sellAt2));
        eventRepository.saveAll(List.of(bogoEvent, btgoEvent));

        SearchProductRequestDto request = SearchProductRequestDto.builder()
            .convenienceStoreIds(null)
            .categoryIds(null)
            .eventTypes(null)
            .lowestPrice(0)
            .highestPrice(1000)
            .keyword(null)
            .build();

        // when
        Long totalCount = productRepository.countByFilter(request);

        // then
        assertThat(totalCount).isEqualTo(1);
    }

    @Test
    @DisplayName("상품 ID 리스트에 따른 편의점 행사를 조회한다")
    void succeed_to_find_convenience_store_events_by_product_ids() {
        // given
        Category category = Category.builder()
            .name("유제품")
            .build();

        Manufacturer manufacturer = Manufacturer.builder()
            .name("칠성")
            .build();

        Product product1 = Product.builder()
            .name("상품1")
            .price(1000)
            .category(category)
            .manufacturer(manufacturer)
            .build();

        Product product2 = Product.builder()
            .name("상품2")
            .price(5000)
            .category(category)
            .manufacturer(manufacturer)
            .build();

        ConvenienceStore cvs = ConvenienceStore.builder()
            .name("GS25")
            .build();

        ConvenienceStore cvs2 = ConvenienceStore.builder()
            .name("CU")
            .build();

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

        BogoEvent bogoEvent = BogoEvent.builder()
            .convenienceStore(cvs)
            .product(product1)
            .build();

        DiscountEvent discountEvent = DiscountEvent.builder()
            .product(product1)
            .convenienceStore(cvs2)
            .discountAmount(500)
            .build();

        BtgoEvent btgoEvent = BtgoEvent.builder()
            .convenienceStore(cvs)
            .product(product2)
            .build();

        categoryRepository.save(category);
        manufacturerRepository.save(manufacturer);
        productRepository.saveAll(List.of(product1, product2));
        convenienceStoreRepository.saveAll(List.of(cvs, cvs2));
        sellAtRepository.saveAll(List.of(sellAt1, sellAt2, sellAt3));
        eventRepository.saveAll(List.of(bogoEvent, discountEvent, btgoEvent));

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
        User user = User.builder()
            .userId("abc@gmail.com")
            .password("12345678a!")
            .nickname("닉네임1")
            .role(Role.ASSOCIATE)
            .build();

        Category category = Category.builder()
            .name("유제품")
            .build();

        Manufacturer manufacturer = Manufacturer.builder()
            .name("칠성")
            .build();

        Product product = Product.builder()
            .name("상품1")
            .price(1000)
            .category(category)
            .manufacturer(manufacturer)
            .build();

        userRepository.save(user);
        categoryRepository.save(category);
        manufacturerRepository.save(manufacturer);
        productRepository.save(product);

        SearchProductDetailQueryDto productDetailResponse = new SearchProductDetailQueryDto(
            product.getId(), product.getName(), product.getPrice(), product.getImageUrl(),
            product.getManufacturer().getName(), null, null);

        // when
        Optional<SearchProductDetailQueryDto> foundProduct = productRepository.findByProductId(user,
            product.getId());

        // then
        assertThat(foundProduct.get().getProductId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("상품 ID에 따른 편의점 행사를 조회한다")
    void succeed_to_find_convenience_store_events_by_product_id() {
        // given
        Category category = Category.builder()
            .name("유제품")
            .build();

        Manufacturer manufacturer = Manufacturer.builder()
            .name("칠성")
            .build();

        Product product = Product.builder()
            .name("상품1")
            .price(1000)
            .category(category)
            .manufacturer(manufacturer)
            .build();

        ConvenienceStore cvs = ConvenienceStore.builder()
            .name("GS25")
            .build();

        ConvenienceStore cvs2 = ConvenienceStore.builder()
            .name("CU")
            .build();

        SellAt sellAt1 = SellAt.builder()
            .product(product)
            .convenienceStore(cvs)
            .build();

        SellAt sellAt2 = SellAt.builder()
            .product(product)
            .convenienceStore(cvs2)
            .build();

        BogoEvent bogoEvent = BogoEvent.builder()
            .convenienceStore(cvs)
            .product(product)
            .build();

        DiscountEvent discountEvent = DiscountEvent.builder()
            .product(product)
            .convenienceStore(cvs2)
            .discountAmount(500)
            .build();

        categoryRepository.save(category);
        manufacturerRepository.save(manufacturer);
        productRepository.save(product);
        convenienceStoreRepository.saveAll(List.of(cvs, cvs2));
        sellAtRepository.saveAll(List.of(sellAt1, sellAt2));
        eventRepository.saveAll(List.of(bogoEvent, discountEvent));

        ConvenienceStoreEventQueryDto product1CvsEvent1 =
            new ConvenienceStoreEventQueryDto(bogoEvent.getProduct().getId(),
                bogoEvent.getConvenienceStore().getName(), bogoEvent);
        ConvenienceStoreEventQueryDto product1CvsEvent2 =
            new ConvenienceStoreEventQueryDto(discountEvent.getProduct().getId(),
                discountEvent.getConvenienceStore().getName(), discountEvent);

        // when
        List<ConvenienceStoreEventQueryDto> foundProducts = productRepository.findConvenienceStoreEventsByProductId(
            product.getId());

        // then
        assertThat(foundProducts).hasSize(2);
        assertThat(product1CvsEvent1.getConvenienceStoreName()).isIn(
            foundProducts.stream().map(ConvenienceStoreEventQueryDto::getConvenienceStoreName)
                .toList());
        assertThat(product1CvsEvent2.getEvent()).isIn(
            foundProducts.stream().map(ConvenienceStoreEventQueryDto::getEvent).toList());
    }

}
