package com.cvsgo.repository;

import static com.cvsgo.entity.QProduct.product;
import static com.cvsgo.entity.QProductBookmark.productBookmark;
import static com.cvsgo.entity.QReview.review;
import static com.cvsgo.entity.QReviewLike.reviewLike;
import static com.cvsgo.entity.QUser.user;
import static com.cvsgo.entity.QUserFollow.userFollow;
import static com.cvsgo.entity.QUserTag.userTag;
import static com.querydsl.jpa.JPAExpressions.selectDistinct;

import com.cvsgo.dto.review.QReadProductReviewQueryDto;
import com.cvsgo.dto.review.QReadReviewQueryDto;
import com.cvsgo.dto.review.ReadProductReviewQueryDto;
import com.cvsgo.dto.review.ReadProductReviewRequestDto;
import com.cvsgo.dto.review.ReadReviewQueryDto;
import com.cvsgo.dto.review.ReviewSortBy;
import com.cvsgo.dto.review.ReadReviewRequestDto;
import com.cvsgo.entity.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<ReadReviewQueryDto> findAllByFilter(User loginUser,
        ReadReviewRequestDto searchFilter, Pageable pageable) {
        return queryFactory.select(new QReadReviewQueryDto(
                review.id,
                review.product.id,
                review.product.name,
                review.product.manufacturer.name,
                review.product.imageUrl,
                review.user.id,
                review.user.nickname,
                review.user.profileImageUrl,
                userFollow,
                review.likeCount,
                review.rating,
                review.content,
                review.createdAt,
                reviewLike,
                productBookmark))
            .from(review)
            .leftJoin(userFollow)
            .on(review.user.eq(userFollow.user).and(userFollowingEq(loginUser)))
            .leftJoin(reviewLike)
            .on(reviewLike.review.eq(review).and(reviewLikeUserEq(loginUser)))
            .leftJoin(productBookmark)
            .on(review.product.eq(productBookmark.product).and(productBookmarkUserEq(loginUser)))
            .where(
                categoryIn(searchFilter.getCategoryIds()),
                ratingIn(searchFilter.getRatings()),
                userIn(searchFilter.getTagIds())
            )
            .orderBy(sortBy(searchFilter.getSortBy()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Long countLatestReviews() {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        return queryFactory.select(review.count())
            .from(review)
            .where(review.createdAt.after(sevenDaysAgo.atStartOfDay()))
            .fetchOne();
    }

    public List<ReadProductReviewQueryDto> findAllByProductIdAndFilter(User loginUser,
        Long productId, ReadProductReviewRequestDto filter, Pageable pageable) {
        return queryFactory.select(new QReadProductReviewQueryDto(
                user.id,
                review.id,
                user.nickname,
                user.profileImageUrl,
                userFollow,
                review.content,
                review.rating,
                reviewLike,
                review.likeCount,
                review.createdAt))
            .from(review)
            .join(user).on(user.eq(review.user))
            .join(product).on(review.product.eq(product))
            .leftJoin(userFollow)
            .on(review.user.eq(userFollow.user).and(userFollowingEq(loginUser)))
            .leftJoin(reviewLike).on(reviewLike.review.eq(review).and(reviewLikeUserEq(loginUser)))
            .where(
                review.product.id.eq(productId),
                ratingIn(filter.getRatings()),
                userIn(filter.getTagIds())
            )
            .orderBy(sortBy(filter.getSortBy()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    public Long countByProductIdAndFilter(Long productId, ReadProductReviewRequestDto filter) {
        return queryFactory.select(review.count())
            .from(review)
            .join(user).on(user.eq(review.user))
            .join(product).on(review.product.eq(product))
            .where(
                review.product.id.eq(productId),
                ratingIn(filter.getRatings()),
                userIn(filter.getTagIds())
            )
            .fetchOne();
    }

    private OrderSpecifier<?> sortBy(ReviewSortBy sortBy) {
        return sortBy != null ?
            switch (sortBy) {
                case RATING -> review.rating.desc();
                case LIKE -> review.likeCount.desc();
                case LATEST -> review.createdAt.desc();
            }
            : review.createdAt.desc();
    }

    BooleanExpression categoryIn(List<Long> categoryIds) {
        return categoryIds != null && !categoryIds.isEmpty()
            ? review.product.category.id.in(categoryIds)
            : null;
    }

    BooleanExpression reviewLikeUserEq(User user) {
        return reviewLike.user.id.eq(user == null ? -1L : user.getId());
    }

    private BooleanExpression userFollowingEq(User user) {
        return userFollow.follower.id.eq(user == null ? -1L : user.getId());
    }

    BooleanExpression productBookmarkUserEq(User user) {
        return user != null ? productBookmark.user.eq(user) : null;
    }

    BooleanExpression userIn(List<Long> tagIds) {
        return tagIds != null && !tagIds.isEmpty()
            ? review.user.in(
            selectDistinct(userTag.user)
                .from(userTag)
                .where(userTag.tag.id.in(tagIds)))
            : null;
    }

    BooleanExpression ratingIn(List<Integer> ratings) {
        return ratings != null && !ratings.isEmpty()
            ? review.rating.in(ratings)
            : null;
    }

}
