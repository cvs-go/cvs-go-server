package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.dto.review.ReadReviewQueryDto;
import com.cvsgo.dto.review.ReadReviewRequestDto;
import com.cvsgo.dto.review.ReviewSortBy;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.Tag;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Import(TestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaAuditing
class ReviewRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ReviewImageRepository reviewImageRepository;

    @BeforeEach
    void initData() {
        tag1 = Tag.builder().name("맵찔이").group(1).build();
        tag2 = Tag.builder().name("맵부심").group(1).build();
        tag3 = Tag.builder().name("초코러버").group(2).build();
        tag4 = Tag.builder().name("비건").group(3).build();
        tag5 = Tag.builder().name("다이어터").group(4).build();
        tag6 = Tag.builder().name("대식가").group(5).build();
        tag7 = Tag.builder().name("소식가").group(5).build();
        tagRepository.saveAll(List.of(tag1, tag2, tag3, tag4, tag5, tag6, tag7));
        user1 = User.create("abc@naver.com", "password1!", "닉네임", List.of(tag2, tag3, tag7));
        user2 = User.create("abcd@naver.com", "password1!", "닉네임2", new ArrayList<>());
        userRepository.save(user1);
        userRepository.save(user2);
        product1 = Product.builder()
            .name("불닭볶음면")
            .price(1800)
            .build();
        productRepository.save(product1);
        review1 = Review.builder()
            .user(user1)
            .product(product1)
            .content("맛있어요")
            .rating(5)
            .imageUrls(new ArrayList<>())
            .build();
        review2 = Review.builder()
            .user(user2)
            .product(product1)
            .rating(2)
            .content("맛없어요")
            .imageUrls(List.of("https://blahblah/review/image1.png"))
            .build();
        reviewRepository.saveAll(List.of(review1, review2));
    }

    @Test
    @DisplayName("리뷰 ID로 리뷰를 조회한다")
    void succeed_to_find_review_by_id() {
        reviewRepository.flush();
        Review foundReview = reviewRepository.findById(review1.getId()).orElseThrow();
        assertThat(foundReview).isEqualTo(review1);
    }

    @Test
    @DisplayName("5점이었던 리뷰를 4점으로 수정한 후 해당 리뷰를 조회하면 4점이어야 한다")
    void succeed_to_update_review_rating() {
        review1.updateRating(4);
        reviewRepository.saveAndFlush(review1);
        Review foundReview = reviewRepository.findById(review1.getId()).orElseThrow();
        assertThat(foundReview.getRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("리뷰 내용을 수정한 후 해당 리뷰를 조회하면 수정 내용이 반영되어 있어야 한다")
    void succeed_to_update_review_content() {
        final String content = "진짜 맛있어요!!";
        review1.updateContent(content);
        reviewRepository.saveAndFlush(review1);
        Review foundReview = reviewRepository.findById(review1.getId()).orElseThrow();
        assertThat(foundReview.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("리뷰 이미지를 수정한 후 해당 리뷰를 조회하면 수정 내용이 반영되어 있어야 한다")
    void succeed_to_update_review_images() {
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://blahblah/review/불닭볶음면1.png");
        imageUrls.add("https://blahblah/review/불닭볶음면2.png");
        review1.updateReviewImages(imageUrls);
        reviewRepository.saveAndFlush(review1);
        Review foundReview = reviewRepository.findById(review1.getId()).orElseThrow();
        assertThat(foundReview.getReviewImages().size()).isEqualTo(imageUrls.size());
    }

    @Test
    @DisplayName("필터를 적용하지 않고 특정 상품의 리뷰를 조회하면 특정 상품의 모든 리뷰가 검색된다")
    void should_return_all_reviews_of_the_product() {
        ReadReviewRequestDto requestDto = new ReadReviewRequestDto(List.of(), List.of(), null);
        List<ReadReviewQueryDto> reviews = reviewRepository.findAllByProductIdAndFilter(user1,
            product1.getId(), requestDto, PageRequest.of(0, 20));

        assertThat(reviews.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("별점 5점 필터를 적용하여 특정 상품의 리뷰를 조회하면 5점인 리뷰들만 조회된다")
    void success_to_read_product_reviews() {
        ReadReviewRequestDto requestDto = new ReadReviewRequestDto(
            List.of(tag1.getId(), tag2.getId(), tag3.getId()), List.of(5), null);
        List<ReadReviewQueryDto> reviews = reviewRepository.findAllByProductIdAndFilter(user1,
            product1.getId(), requestDto, PageRequest.of(0, 20));

        assertThat(reviews.size()).isEqualTo(1);
        assertThat(reviews.get(0).getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("별점 내림차순으로 정렬하여 특정 상품의 리뷰를 조회하면 첫번째 리뷰 별점이 마지막 리뷰 별점보다 크거나 같아야 한다")
    void rating_of_first_review_should_greater_than_or_equal_to_rating_of_last_when_sorted_by_rating_descending_order() {
        ReadReviewRequestDto requestDto = new ReadReviewRequestDto(
            List.of(tag1.getId(), tag2.getId(), tag3.getId()), List.of(5), ReviewSortBy.RATING);

        List<ReadReviewQueryDto> reviews = reviewRepository.findAllByProductIdAndFilter(user1,
            product1.getId(), requestDto, PageRequest.of(0, 20));

        assertThat(reviews.get(0).getRating()).isGreaterThanOrEqualTo(
            reviews.get(reviews.size() - 1).getRating());
    }

    @Test
    @DisplayName("최신순으로 정렬하여 특정 상품의 리뷰를 조회하면 첫번째 리뷰의 생성시각이 마지막 리뷰의 생성 시각보다 크거나 같아야 한다")
    void created_time_of_first_review_should_greater_than_or_equal_to_created_time_of_last_review_when_sorted_by_review_createdAt_descending_order() {
        ReadReviewRequestDto requestDto = new ReadReviewRequestDto(
            List.of(tag1.getId(), tag2.getId(), tag3.getId()), List.of(5), ReviewSortBy.LATEST);

        List<ReadReviewQueryDto> reviews = reviewRepository.findAllByProductIdAndFilter(user1,
            product1.getId(), requestDto, PageRequest.of(0, 20));

        assertThat(reviews.get(0).getCreatedAt()
            .compareTo(reviews.get(reviews.size() - 1).getCreatedAt())).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("필터를 적용한 특정 상품의 리뷰 개수를 조회한다")
    void succeed_to_get_total_count() {
        ReadReviewRequestDto requestDto = new ReadReviewRequestDto(List.of(), List.of(), null);
        Long totalCount = reviewRepository.countByProductIdAndFilter(product1.getId(), requestDto);

        assertThat(totalCount).isEqualTo(2L);
    }

    private Review review1;
    private Review review2;

    private Product product1;

    private User user1;
    private User user2;

    private Tag tag1;
    private Tag tag2;
    private Tag tag3;
    private Tag tag4;
    private Tag tag5;
    private Tag tag6;
    private Tag tag7;

}
