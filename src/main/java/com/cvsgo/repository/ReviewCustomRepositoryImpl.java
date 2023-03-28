package com.cvsgo.repository;

import static com.cvsgo.entity.QProductBookmark.productBookmark;
import static com.cvsgo.entity.QReview.review;

import com.cvsgo.dto.review.QSearchReviewQueryDto;
import com.cvsgo.dto.review.ReviewSortBy;
import com.cvsgo.dto.review.SearchReviewQueryDto;
import com.cvsgo.dto.review.SearchReviewRequestDto;

import static com.cvsgo.entity.QReviewLike.reviewLike;
import static com.cvsgo.entity.QUserTag.userTag;
import static com.querydsl.jpa.JPAExpressions.selectDistinct;

import com.cvsgo.entity.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<SearchReviewQueryDto> searchByFilter(User loginUser,
        SearchReviewRequestDto searchFilter, Pageable pageable) {
        return queryFactory.select(new QSearchReviewQueryDto(
                review.id,
                review.product.id,
                review.product.name,
                review.product.manufacturer.name,
                review.product.imageUrl,
                review.user,
                review.likeCount,
                review.rating,
                review.content,
                review.createdAt,
                reviewLike,
                productBookmark))
            .from(review)
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

    private static OrderSpecifier<?> sortBy(ReviewSortBy sortBy) {
        return sortBy != null ?
            switch (sortBy) {
                case RATING -> review.rating.desc();
                case LIKE -> review.likeCount.desc();
                case LATEST -> review.createdAt.desc();
            }
            : review.createdAt.desc();
    }

    BooleanExpression categoryIn(List<Long> categoryIds) {
        return categoryIds != null && categoryIds.size() > 0
            ? review.product.category.id.in(categoryIds)
            : null;
    }

    BooleanExpression reviewLikeUserEq(User user) {
        return user != null ? reviewLike.user.eq(user) : null;
    }

    BooleanExpression productBookmarkUserEq(User user) {
        return user != null ? productBookmark.user.eq(user) : null;
    }

    BooleanExpression userIn(List<Long> tagIds) {
        return tagIds != null && tagIds.size() > 0
            ? review.user.in(
                selectDistinct(userTag.user)
                .from(userTag)
                .where(userTag.tag.id.in(tagIds)))
            : null;
    }

    BooleanExpression ratingIn(List<Integer> ratings) {
        return ratings != null && ratings.size() > 0
            ? review.rating.in(ratings)
            : null;
    }

}
