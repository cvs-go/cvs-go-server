package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.User;
import java.util.ArrayList;
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
class ReviewRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ReviewImageRepository reviewImageRepository;

    Review review;

    @BeforeEach
    void initData() {
        User user1 = User.create("abc@naver.com", "password1!", "닉네임", new ArrayList<>());
        User user2 = User.create("abcd@naver.com", "password1!", "닉네임2", new ArrayList<>());
        userRepository.save(user1);
        userRepository.save(user2);
        Product product1 = Product.builder()
            .name("불닭볶음면")
            .price(1800)
            .build();
        productRepository.save(product1);
        review = Review.builder()
            .user(user1)
            .product(product1)
            .content("맛있어요")
            .rating(5)
            .imageUrls(new ArrayList<>())
            .build();
        reviewRepository.save(review);
    }

    @Test
    @DisplayName("리뷰 ID로 리뷰를 조회한다")
    void succeed_to_find_review_by_id() {
        reviewRepository.flush();
        Review foundReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertThat(foundReview).isEqualTo(review);
    }

    @Test
    @DisplayName("5점이었던 리뷰를 4점으로 수정한 후 해당 리뷰를 조회하면 4점이어야 한다")
    void succeed_to_update_review_rating() {
        review.updateRating(4);
        reviewRepository.saveAndFlush(review);
        Review foundReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertThat(foundReview.getRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("리뷰 내용을 수정한 후 해당 리뷰를 조회하면 수정 내용이 반영되어 있어야 한다")
    void succeed_to_update_review_content() {
        final String content = "진짜 맛있어요!!";
        review.updateContent(content);
        reviewRepository.saveAndFlush(review);
        Review foundReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertThat(foundReview.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("리뷰 이미지를 수정한 후 해당 리뷰를 조회하면 수정 내용이 반영되어 있어야 한다")
    void succeed_to_update_review_images() {
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://blahblah/review/불닭볶음면1.png");
        imageUrls.add("https://blahblah/review/불닭볶음면2.png");
        review.updateReviewImages(imageUrls);
        reviewRepository.saveAndFlush(review);
        Review foundReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertThat(foundReview.getReviewImages().size()).isEqualTo(imageUrls.size());
    }

}
