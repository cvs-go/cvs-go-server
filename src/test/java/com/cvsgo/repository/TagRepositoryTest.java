package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.dto.product.ReadProductLikeTagResponseDto;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductLike;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import java.util.List;
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
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductLikeRepository productLikeRepository;

    @BeforeEach
    void initData() {
        tag1 = Tag.builder().name("맵찔이").group(1).build();
        tag2 = Tag.builder().name("맵부심").group(1).build();
        tag3 = Tag.builder().name("초코러버").group(2).build();
        tag4 = Tag.builder().name("비건").group(3).build();
        tag5 = Tag.builder().name("다이어터").group(4).build();
        tag6 = Tag.builder().name("대식가").group(5).build();
        tag7 = Tag.builder().name("소식가").group(5).build();
        savedTags = tagRepository.saveAll(List.of(tag1, tag2, tag3, tag4, tag5, tag6, tag7));

        user1 = User.create("abc@naver.com", "password1!", "닉네임1", List.of(tag1, tag3, tag6));
        user2 = User.create("xyz@gmail.com", "password1!", "닉네임2", List.of(tag2, tag4, tag6));
        user3 = User.create("qwer@gmail.com", "password1!", "닉네임3", List.of(tag3, tag4, tag6));
        userRepository.saveAll(List.of(user1, user2, user3));

        product1 = Product.builder().name("상품1").build();
        product2 = Product.builder().name("상품2").build();
        productRepository.saveAll(List.of(product1, product2));

        productLike1 = ProductLike.builder().user(user1).product(product1).build();
        productLike2 = ProductLike.builder().user(user2).product(product1).build();
        productLike3 = ProductLike.builder().user(user3).product(product1).build();
        productLike4 = ProductLike.builder().user(user3).product(product2).build();
        productLikeRepository.saveAll(
            List.of(productLike1, productLike2, productLike3, productLike4));
    }

    @Test
    @DisplayName("태그를 조회한다")
    void find_tags() {
        // when
        List<Tag> foundTags = tagRepository.findAll();

        // then
        assertThat(foundTags.size()).isEqualTo(savedTags.size());
    }

    @Test
    @DisplayName("상품 좋아요 태그 3개를 조회한다")
    void find_tags_by_product() {
        // when
        List<ReadProductLikeTagResponseDto> topTags = tagRepository.findTop3ByProduct(product1);

        // then
        assertThat(topTags).hasSize(3);
        assertThat(topTags).extracting("name").containsExactly("대식가", "비건", "초코러버");
    }

    private Tag tag1;
    private Tag tag2;
    private Tag tag3;
    private Tag tag4;
    private Tag tag5;
    private Tag tag6;
    private Tag tag7;
    private User user1;
    private User user2;
    private User user3;
    private Product product1;
    private Product product2;
    private ProductLike productLike1;
    private ProductLike productLike2;
    private ProductLike productLike3;
    private ProductLike productLike4;
    private List<Tag> savedTags;
}
