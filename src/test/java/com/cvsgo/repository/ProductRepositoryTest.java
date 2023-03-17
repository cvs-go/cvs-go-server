package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Product;
import java.util.List;
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

    //findFirstByOrderByPriceDesc
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
