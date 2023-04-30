package com.cvsgo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;

import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.dto.review.ReadProductReviewQueryDto;
import com.cvsgo.dto.review.ReadProductReviewRequestDto;
import com.cvsgo.dto.review.ReadProductReviewResponseDto;
import com.cvsgo.dto.review.ReviewSortBy;
import com.cvsgo.dto.review.ReadReviewQueryDto;
import com.cvsgo.dto.review.ReadReviewRequestDto;
import com.cvsgo.dto.review.UpdateReviewRequestDto;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.ReviewImage;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserFollow;
import com.cvsgo.entity.UserTag;
import com.cvsgo.exception.DuplicateException;
import com.cvsgo.exception.ForbiddenException;
import com.cvsgo.exception.NotFoundException;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.ReviewImageRepository;
import com.cvsgo.repository.ReviewRepository;
import com.cvsgo.repository.UserTagRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserTagRepository userTagRepository;

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @InjectMocks
    ReviewService reviewService;

    @Mock
    private EntityManager entityManager;

    @Test
    @DisplayName("리뷰를 정상적으로 추가한다")
    void succeed_to_create_review() throws Exception {
        given(reviewRepository.save(any())).willReturn(any());
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        reviewService.createReview(user1, 1L, createReviewRequestDto);

        then(productRepository).should(times(1)).findById(1L);
        then(reviewRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("사용자가 해당 상품의 리뷰를 이미 작성한 경우 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_write_review_but_user_has_already_written_review() {
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(reviewRepository.save(any())).willThrow(DataIntegrityViolationException.class);
        given(reviewRepository.existsByProductAndUser(any(), any())).willReturn(true);

        assertThrows(DuplicateException.class,
            () -> reviewService.createReview(user1, 1L, createReviewRequestDto));

        then(productRepository).should(times(1)).findById(1L);
        then(reviewRepository).should(times(1)).save(any());
        then(reviewRepository).should(times(1)).existsByProductAndUser(any(), any());
    }

    @Test
    @DisplayName("사용자가 리뷰를 5개 작성하면 정회원이 된다")
    void should_be_upgraded_to_regular_member_when_user_write_5_reviews() throws Exception {
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(reviewRepository.save(any())).willReturn(any());
        given(reviewRepository.countByUser(user1)).willReturn(5L);

        reviewService.createReview(user1, 1L, createReviewRequestDto);

        assertThat(user1.getRole()).isEqualTo(Role.REGULAR);
        then(productRepository).should(times(1)).findById(anyLong());
        then(reviewRepository).should(times(1)).save(any());
        then(reviewRepository).should(times(1)).countByUser(any());
    }

    @Test
    @DisplayName("리뷰를 정상적으로 조회한다")
    void succeed_to_read_review() {
        given(reviewRepository.findAllByFilter(any(), any(), any()))
            .willReturn(List.of(readReviewQueryDto));
        given(userTagRepository.findByUserIn(anyList()))
            .willReturn(List.of(userTag));
        given(reviewImageRepository.findByReviewIdIn(anyList()))
            .willReturn(List.of(reviewImage));

        reviewService.getReviewList(user1, searchReviewRequest, PageRequest.of(0, 20));

        then(reviewRepository)
            .should(times(1)).findAllByFilter(any(), any(), any());
        then(userTagRepository).should(times(1)).findByUserIn(any());
        then(reviewImageRepository).should(times(1)).findByReviewIdIn(any());
    }

    @Test
    @DisplayName("특정 상품의 리뷰를 정상적으로 조회한다")
    void succeed_to_read_product_review() {
        ReadProductReviewQueryDto queryDto1 = new ReadProductReviewQueryDto(user1.getId(), review.getId(),
            user1.getNickname(), user1.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewRequestDto requestDto = new ReadProductReviewRequestDto(List.of(1L, 2L, 3L),
            List.of(4, 5), ReviewSortBy.LATEST);

        given(reviewRepository.findAllByProductIdAndFilter(any(), anyLong(), any(), any()))
            .willReturn(List.of(queryDto1));
        given(reviewRepository.countByProductIdAndFilter(anyLong(), any()))
            .willReturn(1L);
        given(userTagRepository.findByUserIdIn(anyList()))
            .willReturn(List.of(userTag));
        given(reviewImageRepository.findByReviewIdIn(anyList()))
            .willReturn(List.of(reviewImage));

        List<ReadProductReviewResponseDto> reviews = reviewService.readProductReviewList(user2, 1L,
            requestDto, PageRequest.of(0, 20)).getContent();

        assertThat(reviews.size()).isEqualTo(1);
        then(reviewRepository).should(times(1))
            .findAllByProductIdAndFilter(any(), anyLong(), any(), any());
        then(reviewRepository).should(times(1)).countByProductIdAndFilter(anyLong(), any());
        then(userTagRepository).should(times(1)).findByUserIdIn(any());
        then(reviewImageRepository).should(times(1)).findByReviewIdIn(any());
    }

    @Test
    @DisplayName("준회원인 사용자가 특정 상품의 리뷰 0페이지를 조회하면 5개만 조회된다")
    void should_get_only_five_reviews_when_associate_user_read_first_page_of_product_reviews() {
        ReadProductReviewQueryDto queryDto1 = new ReadProductReviewQueryDto(user1.getId(), review.getId(),
            user1.getNickname(), user1.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewQueryDto queryDto2 = new ReadProductReviewQueryDto(user1.getId(), review.getId(),
            user1.getNickname(), user1.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewQueryDto queryDto3 = new ReadProductReviewQueryDto(user1.getId(), review.getId(),
            user1.getNickname(), user1.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewQueryDto queryDto4 = new ReadProductReviewQueryDto(user1.getId(), review.getId(),
            user1.getNickname(), user1.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewQueryDto queryDto5 = new ReadProductReviewQueryDto(user1.getId(), review.getId(),
            user1.getNickname(), user1.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewQueryDto queryDto6 = new ReadProductReviewQueryDto(user1.getId(), review.getId(),
            user1.getNickname(), user1.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewRequestDto requestDto = new ReadProductReviewRequestDto(List.of(1L, 2L, 3L),
            List.of(4, 5), ReviewSortBy.LATEST);

        PageRequest size20 = PageRequest.of(0, 20);
        PageRequest size5 = PageRequest.of(0, 5);

        lenient().when(
                reviewRepository.findAllByProductIdAndFilter(any(), anyLong(), any(), eq(size20)))
            .thenReturn(List.of(queryDto1, queryDto2, queryDto3, queryDto4, queryDto5,
                queryDto6)); // page size가 20인 경우 6개
        lenient().when(reviewRepository.countByProductIdAndFilter(anyLong(), any())).thenReturn(6L);
        lenient().when(
                reviewRepository.findAllByProductIdAndFilter(any(), anyLong(), any(), eq(size5)))
            .thenReturn(List.of(queryDto1, queryDto2, queryDto3, queryDto4,
                queryDto5)); // page size가 5인 경우 5개
        lenient().when(reviewRepository.countByProductIdAndFilter(anyLong(), any())).thenReturn(5L);
        given(userTagRepository.findByUserIdIn(anyList()))
            .willReturn(List.of(userTag));
        given(reviewImageRepository.findByReviewIdIn(anyList()))
            .willReturn(List.of(reviewImage));

        List<ReadProductReviewResponseDto> reviews = reviewService.readProductReviewList(user1, 1L,
            requestDto, PageRequest.of(0, 20)).getContent();

        assertThat(reviews.size()).isLessThanOrEqualTo(5); // user1은 준회원이기 때문에 최대 5개까지만 조회됨
        then(reviewRepository).should(times(1))
            .findAllByProductIdAndFilter(any(), anyLong(), any(), any());
        then(userTagRepository).should(times(1)).findByUserIdIn(any());
        then(reviewImageRepository).should(times(1)).findByReviewIdIn(any());
    }

    @Test
    @DisplayName("정회원인 사용자가 특정 상품의 리뷰 0페이지를 조회하면 정상적으로 조회된다")
    void should_success_to_read_product_reviews_when_regular_user_read_first_page_of_product_reviews() {
        ReadProductReviewQueryDto queryDto1 = new ReadProductReviewQueryDto(user2.getId(), review.getId(),
            user2.getNickname(), user2.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewQueryDto queryDto2 = new ReadProductReviewQueryDto(user2.getId(), review.getId(),
            user2.getNickname(), user2.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewQueryDto queryDto3 = new ReadProductReviewQueryDto(user2.getId(), review.getId(),
            user2.getNickname(), user2.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewQueryDto queryDto4 = new ReadProductReviewQueryDto(user2.getId(), review.getId(),
            user2.getNickname(), user2.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewQueryDto queryDto5 = new ReadProductReviewQueryDto(user2.getId(), review.getId(),
            user2.getNickname(), user2.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewQueryDto queryDto6 = new ReadProductReviewQueryDto(user2.getId(), review.getId(),
            user2.getNickname(), user2.getProfileImageUrl(), userFollow, review.getContent(),
            review.getRating(), null, review.getLikeCount(), LocalDateTime.now());
        ReadProductReviewRequestDto requestDto = new ReadProductReviewRequestDto(List.of(1L, 2L, 3L),
            List.of(4, 5), ReviewSortBy.LATEST);

        PageRequest size20 = PageRequest.of(0, 20);
        PageRequest size5 = PageRequest.of(0, 5);

        lenient().when(
                reviewRepository.findAllByProductIdAndFilter(any(), anyLong(), any(), eq(size20)))
            .thenReturn(List.of(queryDto1, queryDto2, queryDto3, queryDto4, queryDto5,
                queryDto6)); // page size가 20인 경우 6개
        lenient().when(reviewRepository.countByProductIdAndFilter(anyLong(), any())).thenReturn(6L);
        lenient().when(
                reviewRepository.findAllByProductIdAndFilter(any(), anyLong(), any(), eq(size5)))
            .thenReturn(List.of(queryDto1, queryDto2, queryDto3, queryDto4,
                queryDto5)); // page size가 5인 경우 5개
        lenient().when(reviewRepository.countByProductIdAndFilter(anyLong(), any())).thenReturn(5L);
        given(userTagRepository.findByUserIdIn(anyList()))
            .willReturn(List.of(userTag));
        given(reviewImageRepository.findByReviewIdIn(anyList()))
            .willReturn(List.of(reviewImage));

        List<ReadProductReviewResponseDto> reviews = reviewService.readProductReviewList(user2, 1L,
            requestDto, PageRequest.of(0, 20)).getContent();

        assertThat(reviews.size()).isEqualTo(6); // user2는 정회원이므로 6개가 조회됨
        then(reviewRepository).should(times(1))
            .findAllByProductIdAndFilter(any(), anyLong(), any(), any());
        then(userTagRepository).should(times(1)).findByUserIdIn(any());
        then(reviewImageRepository).should(times(1)).findByReviewIdIn(any());
    }

    @Test
    @DisplayName("준회원인 사용자가 특정 상품의 리뷰 1페이지를 조회하면 ForbiddenException이 발생한다")
    void should_throw_ForbiddenException_when_associate_user_read_second_page_of_product_reviews() {
        ReadProductReviewRequestDto requestDto = new ReadProductReviewRequestDto(List.of(1L, 2L, 3L),
            List.of(4, 5), ReviewSortBy.LATEST);

        assertThrows(ForbiddenException.class,
            () -> reviewService.readProductReviewList(user1, 1L, requestDto, PageRequest.of(1, 20)));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 특정 상품의 리뷰 1페이지를 조회하면 ForbiddenException이 발생한다")
    void should_throw_ForbiddenException_when_non_login_user_read_second_page_of_product_reviews() {
        ReadProductReviewRequestDto requestDto = new ReadProductReviewRequestDto(List.of(1L, 2L, 3L),
            List.of(4, 5), null);

        assertThrows(ForbiddenException.class,
            () -> reviewService.readProductReviewList(null, 1L, requestDto, PageRequest.of(1, 20)));
    }

    @Test
    @DisplayName("해당 ID의 상품이 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_create_review_but_product_does_not_exist() {
        given(productRepository.findById(anyLong()))
            .willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
            () -> reviewService.createReview(user1, 100L, createReviewRequestDto));
    }

    @Test
    @DisplayName("리뷰 수정에 성공합니다.")
    void success_to_update_review() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile("images", "sample_image1.png",
            MediaType.IMAGE_PNG_VALUE, "image 1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "sample_image2.png",
            MediaType.IMAGE_PNG_VALUE, "image 2".getBytes());
        UpdateReviewRequestDto requestDto = new UpdateReviewRequestDto(4, "맛있어요",
            List.of(image1, image2));
        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));

        reviewService.updateReview(user2, 1L, requestDto);
        then(reviewRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("리뷰 수정시 해당 ID의 리뷰가 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_update_review_but_review_does_not_exist() {
        given(reviewRepository.findById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
            () -> reviewService.updateReview(user1, 100L, updateReviewRequestDto));
    }

    @Test
    @DisplayName("해당 리뷰 작성자 본인이 아닌 사용자가 리뷰 수정을 시도할 경우 ForbiddenException이 발생한다")
    void should_throw_ForbiddenException_when_update_review_but_user_is_not_the_reviewer() {
        given(reviewRepository.findById(anyLong()))
            .willReturn(Optional.of(review));

        assertThrows(ForbiddenException.class,
            () -> reviewService.updateReview(user1, 100L, updateReviewRequestDto));
    }

    User user1 = User.builder()
        .id(1L)
        .userId("abc@naver.com")
        .nickname("사용자1")
        .role(Role.ASSOCIATE)
        .build();

    User user2 = User.builder()
        .id(2L)
        .userId("abcd@naver.com")
        .nickname("사용자2")
        .role(Role.REGULAR)
        .build();

    Product product = Product.builder().build();

    Review review = Review.builder()
        .id(1L)
        .user(user2)
        .rating(5)
        .content("맛있어요")
        .imageUrls(List.of())
        .build();

    CreateReviewRequestDto createReviewRequestDto = CreateReviewRequestDto.builder().build();

    UpdateReviewRequestDto updateReviewRequestDto = new UpdateReviewRequestDto(5, "맛있어요",
        new ArrayList<>());

    ReadReviewRequestDto searchReviewRequest = ReadReviewRequestDto.builder().build();

    ReadReviewQueryDto readReviewQueryDto = ReadReviewQueryDto.builder()
        .reviewId(1L)
        .productId(2L)
        .productName("불닭볶음면큰컵")
        .manufacturerName("삼양")
        .productImageUrl("https://어쩌구저쩌구/products/불닭볶음면.png")
        .reviewer(user1)
        .likeCount(3L)
        .rating(4)
        .build();

    Tag tag = Tag.builder()
        .name("초코러버")
        .build();

    UserTag userTag = UserTag.builder()
        .user(user1)
        .tag(tag)
        .build();

    ReviewImage reviewImage = ReviewImage.builder()
        .review(Review.builder().id(1L).imageUrls(new ArrayList<>()).build())
        .imageUrl("https://어쩌구저쩌구/review/리뷰이미지.png")
        .build();

    UserFollow userFollow = UserFollow.builder()
        .user(user2)
        .follower(user1)
        .build();

}
