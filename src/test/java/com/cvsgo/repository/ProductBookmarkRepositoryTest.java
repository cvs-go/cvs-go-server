package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductBookmark;
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

    @BeforeEach
    void initData() {
        user1 = User.create("abc@naver.com", "password1!", "닉네임", new ArrayList<>());
        user2 = User.create("abcd@naver.com", "password1!", "닉네임2", new ArrayList<>());
        userRepository.save(user1);
        userRepository.save(user2);

        product1 = Product.builder()
            .name("상품1")
            .price(1000)
            .build();
        product2 = Product.builder()
            .name("상품2")
            .price(2000)
            .build();
        productRepository.saveAll(List.of(product1, product2));

        productBookmark1 = ProductBookmark.builder()
            .product(product1)
            .user(user1)
            .build();
        productBookmarkRepository.save(productBookmark1);
    }

    @Test
    @DisplayName("상품 북마크를 추가한다")
    void succeed_to_save_product_bookmark() {
        // given
        ProductBookmark productBookmark = ProductBookmark.builder()
            .product(product2)
            .user(user1)
            .build();

        // when
        productBookmarkRepository.save(productBookmark);

        // then
        assertThat(productBookmark.getId()).isNotNull();
    }

    @Test
    @DisplayName("상품 북마크를 삭제한다")
    void succeed_to_delete_product_bookmark() {
        // given
        Optional<ProductBookmark> foundProductBookmark = productBookmarkRepository.findByProductAndUser(
            product1, user1);

        // when
        foundProductBookmark.ifPresent(selectProductBookmark ->
            productBookmarkRepository.delete(selectProductBookmark)
        );
        Optional<ProductBookmark> deletedProductBookmark = productBookmarkRepository.findByProductAndUser(
            product1, user1);

        // then
        assertThat(foundProductBookmark).isPresent();
        assertThat(deletedProductBookmark).isNotPresent();
    }

    @Test
    @DisplayName("해당하는 상품 북마크를 조회한다")
    void succeed_to_find_product_bookmark_by_product_and_user() {
        Optional<ProductBookmark> productBookmark1 = productBookmarkRepository.findByProductAndUser(
            product1, user1);
        Optional<ProductBookmark> productBookmark2 = productBookmarkRepository.findByProductAndUser(
            product2, user1);

        // then
        assertThat(productBookmark1).isPresent();
        assertThat(productBookmark2).isNotPresent();
    }

    @Test
    @DisplayName("해당하는 상품 북마크가 있는지 확인한다")
    void succeed_to_find_existing_product_bookmark_by_product_and_user() {
        boolean user1ProductBookmark = productBookmarkRepository.existsByProductAndUser(product1, user1);
        boolean user2ProductBookmark = productBookmarkRepository.existsByProductAndUser(product1, user2);

        assertThat(user1ProductBookmark).isTrue();
        assertThat(user2ProductBookmark).isFalse();
    }

    User user1;
    User user2;
    Product product1;
    Product product2;
    ProductBookmark productBookmark1;
}
