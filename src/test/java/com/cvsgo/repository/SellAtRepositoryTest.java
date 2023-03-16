package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.SellAt;
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
class SellAtRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ConvenienceStoreRepository convenienceStoreRepository;

    @Autowired
    SellAtRepository tagRepository;

    @Test
    @DisplayName("상품 판매 편의점을 조회한다")
    void succeed_to_find_sell_at_by_product_id() {
        // given
        Product product1 = Product.builder()
            .name("상품1")
            .build();

        Product product2 = Product.builder()
            .name("상품2")
            .build();

        ConvenienceStore cvs1 = ConvenienceStore.builder()
            .name("cvs1")
            .build();

        ConvenienceStore cvs2 = ConvenienceStore.builder()
            .name("cvs2")
            .build();

        ConvenienceStore cvs3 = ConvenienceStore.builder()
            .name("cvs3")
            .build();

        SellAt sellAt1 = SellAt.builder()
            .product(product1)
            .convenienceStore(cvs1)
            .build();

        SellAt sellAt2 = SellAt.builder()
            .product(product1)
            .convenienceStore(cvs2)
            .build();

        SellAt sellAt3 = SellAt.builder()
            .product(product2)
            .convenienceStore(cvs3)
            .build();

        productRepository.saveAll(List.of(product1, product2));
        convenienceStoreRepository.saveAll(List.of(cvs1, cvs2, cvs3));
        tagRepository.saveAll(List.of(sellAt1, sellAt2, sellAt3));

        // when
        List<SellAt> sellAtsByProduct1 = tagRepository.findByProductId(product1.getId());
        List<SellAt> sellAtsByProduct2 = tagRepository.findByProductId(product2.getId());

        // then
        assertThat(sellAtsByProduct1).hasSize(2);
        assertThat(sellAtsByProduct2).hasSize(1);
    }
}
