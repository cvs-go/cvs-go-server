package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(TestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductBookmarkRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductBookmarkRepository productBookmarkRepository;

    @Test
    @DisplayName("상품 북마크를 추가한다")
    void succeed_to_save_product_bookmark() {
        // given
        User user = User.builder()
            .userId("abc@gmail.com")
            .password("12345678a!")
            .nickname("닉네임1")
            .role(Role.ASSOCIATE)
            .build();

        Product product = Product.builder()
            .name("상품1")
            .price(1000)
            .build();

        ProductBookmark productBookmark = ProductBookmark.builder()
            .product(product)
            .user(user)
            .build();

        userRepository.save(user);
        productRepository.save(product);

        // when
        productBookmarkRepository.save(productBookmark);

        // then
        assertThat(productBookmark.getId()).isNotNull();
    }

}
