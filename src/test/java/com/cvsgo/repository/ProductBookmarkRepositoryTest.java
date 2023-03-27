package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
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

    @Test
    @DisplayName("상품 북마크를 삭제한다")
    void succeed_to_delete_product_bookmark() {
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
        productBookmarkRepository.save(productBookmark);
        Optional<ProductBookmark> foundProductBookmark = productBookmarkRepository.findByProductAndUser(
            product, user);

        // when
        foundProductBookmark.ifPresent(selectProductBookmark ->
            productBookmarkRepository.delete(selectProductBookmark)
        );
        Optional<ProductBookmark> deletedProductBookmark = productBookmarkRepository.findByProductAndUser(
            product, user);

        // then
        assertThat(deletedProductBookmark).isNotPresent();
    }

    @Test
    @DisplayName("해당하는 상품 북마크를 조회한다")
    void succeed_to_find_product_bookmark_by_product_and_user() {
        // given
        User user = User.builder()
            .userId("abc@gmail.com")
            .password("12345678a!")
            .nickname("닉네임1")
            .role(Role.ASSOCIATE)
            .build();

        Product product1 = Product.builder()
            .name("상품1")
            .price(1000)
            .build();

        Product product2 = Product.builder()
            .name("상품2")
            .price(5000)
            .build();

        ProductBookmark product1UserBookmark = ProductBookmark.builder()
            .product(product1)
            .user(user)
            .build();

        userRepository.save(user);
        productRepository.saveAll(List.of(product1, product2));
        productBookmarkRepository.save(product1UserBookmark);

        // when
        Optional<ProductBookmark> productBookmark1 = productBookmarkRepository.findByProductAndUser(
            product1, user);
        Optional<ProductBookmark> productBookmark2 = productBookmarkRepository.findByProductAndUser(
            product2, user);

        // then
        assertThat(productBookmark1).isPresent();
        assertThat(productBookmark2).isNotPresent();
    }
}
