package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductLike;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import com.cvsgo.exception.product.DuplicateProductLikeException;
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
class ProductLikeRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductLikeRepository productLikeRepository;

    @Test
    @DisplayName("상품 좋아요를 추가한다")
    void succeed_to_save_product_like() {
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

        ProductLike productLike = ProductLike.builder()
            .product(product)
            .user(user)
            .build();

        userRepository.save(user);
        productRepository.save(product);

        // when
        productLikeRepository.save(productLike);

        // then
        assertThat(productLike.getId()).isNotNull();
    }

    @Test
    @DisplayName("상품 좋아요를 삭제한다")
    void succeed_to_delete_product_like() {
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

        ProductLike productLike = ProductLike.builder()
            .product(product)
            .user(user)
            .build();

        userRepository.save(user);
        productRepository.save(product);
        productLikeRepository.save(productLike);
        Optional<ProductLike> foundProductLike = productLikeRepository.findByProductAndUser(product,
            user);

        // when
        foundProductLike.ifPresent(selectProductLike ->
            productLikeRepository.delete(selectProductLike)
        );
        Optional<ProductLike> deletedProductLike = productLikeRepository.findByProductAndUser(
            product, user);

        // then
        assertThat(deletedProductLike).isNotPresent();
    }

    @Test
    @DisplayName("해당하는 상품 좋아요를 조회한다")
    void succeed_to_find_product_like_by_product_and_user() {
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

        ProductLike product1UserLike = ProductLike.builder()
            .product(product1)
            .user(user)
            .build();

        userRepository.save(user);
        productRepository.saveAll(List.of(product1, product2));
        productLikeRepository.save(product1UserLike);

        // when
        Optional<ProductLike> productLike1 = productLikeRepository.findByProductAndUser(product1,
            user);
        Optional<ProductLike> productLike2 = productLikeRepository.findByProductAndUser(product2,
            user);

        // then
        assertThat(productLike1).isPresent();
        assertThat(productLike2).isNotPresent();
    }
}