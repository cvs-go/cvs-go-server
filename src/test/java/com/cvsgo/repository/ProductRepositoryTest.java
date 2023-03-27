package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Product;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(TestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

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

}
