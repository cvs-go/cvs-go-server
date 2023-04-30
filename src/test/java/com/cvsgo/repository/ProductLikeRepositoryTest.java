package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductLike;
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
class ProductLikeRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductLikeRepository productLikeRepository;

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

        productLike1 = ProductLike.builder()
            .product(product1)
            .user(user1)
            .build();
        productLikeRepository.save(productLike1);
    }

    @Test
    @DisplayName("상품 좋아요를 추가한다")
    void succeed_to_save_product_like() {
        // given
        ProductLike productLike = ProductLike.builder()
            .product(product2)
            .user(user1)
            .build();

        // when
        productLikeRepository.save(productLike);

        // then
        assertThat(productLike.getId()).isNotNull();
    }

    @Test
    @DisplayName("상품 좋아요를 삭제한다")
    void succeed_to_delete_product_like() {
        // given
        Optional<ProductLike> foundProductLike = productLikeRepository.findByProductAndUser(product1,
            user1);

        // when
        foundProductLike.ifPresent(selectProductLike ->
            productLikeRepository.delete(selectProductLike)
        );
        Optional<ProductLike> deletedProductLike = productLikeRepository.findByProductAndUser(
            product1, user1);

        // then
        assertThat(foundProductLike).isPresent();
        assertThat(deletedProductLike).isNotPresent();
    }

    @Test
    @DisplayName("해당하는 상품 좋아요를 조회한다")
    void succeed_to_find_product_like_by_product_and_user() {
        Optional<ProductLike> productLike1 = productLikeRepository.findByProductAndUser(product1,
            user1);
        Optional<ProductLike> productLike2 = productLikeRepository.findByProductAndUser(product2,
            user1);

        assertThat(productLike1).isPresent();
        assertThat(productLike2).isNotPresent();
    }

    @Test
    @DisplayName("해당하는 상품 좋아요가 있는지 확인한다")
    void succeed_to_find_existing_product_like_by_product_and_user() {
        boolean user1ProductLike = productLikeRepository.existsByProductAndUser(product1, user1);
        boolean user2ProductLike = productLikeRepository.existsByProductAndUser(product1, user2);

        assertThat(user1ProductLike).isTrue();
        assertThat(user2ProductLike).isFalse();
    }

    User user1;
    User user2;
    Product product1;
    Product product2;
    ProductLike productLike1;
}
