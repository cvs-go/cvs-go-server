package com.cvsgo.repository;

import static com.cvsgo.entity.QEvent.event;
import static com.cvsgo.entity.QManufacturer.manufacturer;
import static com.cvsgo.entity.QProduct.product;
import static com.cvsgo.entity.QProductBookmark.productBookmark;
import static com.cvsgo.entity.QProductLike.productLike;
import static com.cvsgo.entity.QReview.review;
import static com.cvsgo.entity.QSellAt.sellAt;
import static com.querydsl.jpa.JPAExpressions.selectDistinct;

import com.cvsgo.dto.product.ConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.ProductSortBy;
import com.cvsgo.dto.product.QConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.QReadProductDetailQueryDto;
import com.cvsgo.dto.product.QReadProductQueryDto;
import com.cvsgo.dto.product.ReadProductDetailQueryDto;
import com.cvsgo.dto.product.ReadProductQueryDto;
import com.cvsgo.dto.product.ReadProductRequestDto;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<ReadProductQueryDto> findAllByFilter(User loginUser,
        ReadProductRequestDto searchFilter, Pageable pageable) {
        NumberPath<Long> reviewCount = Expressions.numberPath(Long.class, "reviewCount");
        NumberPath<Double> avgRating = Expressions.numberPath(Double.class, "avgRating");
        NumberPath<Double> score = Expressions.numberPath(Double.class, "score");
        return queryFactory.select(new QReadProductQueryDto(
                product.id,
                product.name,
                product.price,
                product.imageUrl,
                product.category.id,
                manufacturer.name,
                productLike,
                productBookmark,
                review.count().as("reviewCount"),
                review.rating.avg().as("avgRating"),
                (review.rating.coalesce(0).avg().multiply(review.count()).add(product.likeCount)).as(
                    "score")
            ))
            .from(product)
            .leftJoin(productLike)
            .on(productLike.product.eq(product).and(productLikeUserEq(loginUser)))
            .leftJoin(productBookmark)
            .on(productBookmark.product.eq(product).and(productBookmarkUserEq(loginUser)))
            .leftJoin(review).on(review.product.eq(product))
            .leftJoin(manufacturer).on(product.manufacturer.eq(manufacturer))
            .where(
                product.in(
                    selectDistinct(sellAt.product)
                        .from(sellAt)
                        .leftJoin(event).on(sellAt.product.eq(event.product))
                        .where(
                            isEvent(searchFilter.getIsEvent()),
                            convenienceStoreEq(searchFilter.getConvenienceStoreIds()),
                            eventTypeEq(searchFilter.getEventTypes()),
                            categoryEq(searchFilter.getCategoryIds()),
                            priceLessOrEqual(searchFilter.getHighestPrice()),
                            priceGreaterOrEqual(searchFilter.getLowestPrice()),
                            searchKeyword(searchFilter.getKeyword(),
                                product.name.concat(product.manufacturer.name))
                        ))
            )
            .groupBy(product)
            .orderBy(
                sortBy(searchFilter.getSortBy(), score, avgRating, reviewCount).toArray(
                    OrderSpecifier[]::new))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    public Long countByFilter(ReadProductRequestDto searchFilter) {
        return queryFactory.select(product.count())
            .from(product)
            .leftJoin(manufacturer).on(product.manufacturer.eq(manufacturer))
            .where(
                product.in(
                    selectDistinct(sellAt.product)
                        .from(sellAt)
                        .leftJoin(event).on(sellAt.product.eq(event.product))
                        .where(
                            isEvent(searchFilter.getIsEvent()),
                            convenienceStoreEq(searchFilter.getConvenienceStoreIds()),
                            eventTypeEq(searchFilter.getEventTypes()),
                            categoryEq(searchFilter.getCategoryIds()),
                            priceLessOrEqual(searchFilter.getHighestPrice()),
                            priceGreaterOrEqual(searchFilter.getLowestPrice()),
                            searchKeyword(searchFilter.getKeyword(),
                                product.name.concat(product.manufacturer.name))
                        ))
            )
            .fetchOne();
    }

    public List<ReadProductQueryDto> findAllByUserProductLike(User loginUser,
        ProductSortBy sortBy, Pageable pageable) {
        NumberPath<Long> reviewCount = Expressions.numberPath(Long.class, "reviewCount");
        NumberPath<Double> avgRating = Expressions.numberPath(Double.class, "avgRating");
        NumberPath<Double> score = Expressions.numberPath(Double.class, "score");
        return queryFactory.select(new QReadProductQueryDto(
                product.id,
                product.name,
                product.price,
                product.imageUrl,
                product.category.id,
                manufacturer.name,
                productLike,
                productBookmark,
                review.count().as("reviewCount"),
                review.rating.avg().as("avgRating"),
                (review.rating.coalesce(0).avg().multiply(review.count()).add(product.likeCount)).as(
                    "score")
            ))
            .from(product)
            .leftJoin(productLike)
            .on(productLike.product.eq(product).and(productLikeUserEq(loginUser)))
            .leftJoin(productBookmark)
            .on(productBookmark.product.eq(product).and(productBookmarkUserEq(loginUser)))
            .leftJoin(review).on(review.product.eq(product))
            .leftJoin(manufacturer).on(product.manufacturer.eq(manufacturer))
            .where(
                product.in(
                    selectDistinct(sellAt.product)
                        .from(sellAt)
                        .leftJoin(event).on(sellAt.product.eq(event.product))
                        .where(
                            productLikeUserEq(loginUser)
                        ))
            )
            .groupBy(product)
            .orderBy(
                sortBy(sortBy, score, avgRating, reviewCount).toArray(
                    OrderSpecifier[]::new))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    public Long countByUserProductLike(User loginUser) {
        return queryFactory.select(product.count())
            .from(product)
            .leftJoin(productLike)
            .on(productLike.product.eq(product).and(productLikeUserEq(loginUser)))
            .where(
                product.in(
                    selectDistinct(sellAt.product)
                        .from(sellAt)
                        .leftJoin(event).on(sellAt.product.eq(event.product))
                        .where(
                            productLikeUserEq(loginUser)
                        ))
            )
            .fetchOne();
    }

    public List<ReadProductQueryDto> findAllByUserProductBookmark(User loginUser,
        ProductSortBy sortBy, Pageable pageable) {
        NumberPath<Long> reviewCount = Expressions.numberPath(Long.class, "reviewCount");
        NumberPath<Double> avgRating = Expressions.numberPath(Double.class, "avgRating");
        NumberPath<Double> score = Expressions.numberPath(Double.class, "score");
        return queryFactory.select(new QReadProductQueryDto(
                product.id,
                product.name,
                product.price,
                product.imageUrl,
                product.category.id,
                manufacturer.name,
                productLike,
                productBookmark,
                review.count().as("reviewCount"),
                review.rating.avg().as("avgRating"),
                (review.rating.coalesce(0).avg().multiply(review.count()).add(product.likeCount)).as(
                    "score")
            ))
            .from(product)
            .leftJoin(productLike)
            .on(productLike.product.eq(product).and(productLikeUserEq(loginUser)))
            .leftJoin(productBookmark)
            .on(productBookmark.product.eq(product).and(productBookmarkUserEq(loginUser)))
            .leftJoin(review).on(review.product.eq(product))
            .leftJoin(manufacturer).on(product.manufacturer.eq(manufacturer))
            .where(
                product.in(
                    selectDistinct(sellAt.product)
                        .from(sellAt)
                        .leftJoin(event).on(sellAt.product.eq(event.product))
                        .where(
                            productBookmarkUserEq(loginUser)
                        ))
            )
            .groupBy(product)
            .orderBy(
                sortBy(sortBy, score, avgRating, reviewCount).toArray(
                    OrderSpecifier[]::new))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    public Long countByUserProductBookmark(User loginUser) {
        return queryFactory.select(product.count())
            .from(product)
            .leftJoin(productBookmark)
            .on(productBookmark.product.eq(product).and(productBookmarkUserEq(loginUser)))
            .where(
                product.in(
                    selectDistinct(sellAt.product)
                        .from(sellAt)
                        .leftJoin(event).on(sellAt.product.eq(event.product))
                        .where(
                            productBookmarkUserEq(loginUser)
                        ))
            )
            .fetchOne();
    }

    public List<ConvenienceStoreEventQueryDto> findConvenienceStoreEventsByProductIds(
        List<Long> productIds) {
        return queryFactory.select(new QConvenienceStoreEventQueryDto(
                sellAt.product.id,
                sellAt.convenienceStore.name,
                event))
            .from(sellAt)
            .leftJoin(event).on(sellAt.product.eq(event.product)
                .and(sellAt.convenienceStore.eq(event.convenienceStore)))
            .where(sellAt.product.id.in(productIds))
            .fetch();
    }

    public List<ConvenienceStoreEventQueryDto> findConvenienceStoreEventsByProductId(
        Long productId) {
        return queryFactory.select(new QConvenienceStoreEventQueryDto(
                sellAt.convenienceStore.id,
                sellAt.convenienceStore.name,
                event))
            .from(sellAt)
            .leftJoin(event).on(sellAt.product.eq(event.product)
                .and(sellAt.convenienceStore.eq(event.convenienceStore)))
            .where(sellAt.product.id.eq(productId))
            .fetch();
    }

    public Optional<ReadProductDetailQueryDto> findByProductId(User loginUser, Long productId) {
        return Optional.ofNullable(queryFactory
            .select(new QReadProductDetailQueryDto(
                product.id,
                product.name,
                product.price,
                product.imageUrl,
                manufacturer.name,
                productLike,
                productBookmark
            ))
            .from(product)
            .leftJoin(productLike)
            .on(productLike.product.eq(product).and(productLikeUserEq(loginUser)))
            .leftJoin(productBookmark)
            .on(productBookmark.product.eq(product).and(productBookmarkUserEq(loginUser)))
            .leftJoin(manufacturer).on(product.manufacturer.eq(manufacturer))
            .where(product.id.eq(productId))
            .fetchOne());
    }

    private static List<OrderSpecifier<?>> sortBy(ProductSortBy sortBy, NumberPath<Double> score,
        NumberPath<Double> avgRating, NumberPath<Long> reviewCount) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        if (sortBy != null) {
            switch (sortBy) {
                case SCORE -> {
                    orderSpecifiers.add(score.desc());
                    orderSpecifiers.add(avgRating.desc());
                    orderSpecifiers.add(reviewCount.desc());
                    orderSpecifiers.add(product.likeCount.desc());
                    orderSpecifiers.add(product.createdAt.desc());
                }
                case RATING -> {
                    orderSpecifiers.add(avgRating.desc());
                    orderSpecifiers.add(reviewCount.desc());
                    orderSpecifiers.add(product.likeCount.desc());
                    orderSpecifiers.add(product.createdAt.desc());
                }
                case REVIEW_COUNT -> {
                    orderSpecifiers.add(reviewCount.desc());
                    orderSpecifiers.add(avgRating.desc());
                    orderSpecifiers.add(product.likeCount.desc());
                    orderSpecifiers.add(product.createdAt.desc());
                }
            }
        } else {
            orderSpecifiers.add(score.desc());
            orderSpecifiers.add(avgRating.desc());
            orderSpecifiers.add(reviewCount.desc());
            orderSpecifiers.add(product.likeCount.desc());
            orderSpecifiers.add(product.createdAt.desc());
        }
        return orderSpecifiers;
    }

    private BooleanExpression productLikeUserEq(User user) {
        return user != null ? productLike.user.eq(user) : null;
    }

    private BooleanExpression productBookmarkUserEq(User user) {
        return user != null ? productBookmark.user.eq(user) : null;
    }

    private BooleanExpression convenienceStoreEq(List<Long> convenienceStoreIds) {
        return convenienceStoreIds != null && !convenienceStoreIds.isEmpty()
            ? sellAt.convenienceStore.id.in(convenienceStoreIds)
            : null;
    }

    private BooleanExpression categoryEq(List<Long> categoryIds) {
        return categoryIds != null && !categoryIds.isEmpty()
            ? product.category.id.in(categoryIds)
            : null;
    }

    private BooleanExpression eventTypeEq(List<EventType> eventTypes) {
        return eventTypes != null && !eventTypes.isEmpty()
            ? event.eventType.in(eventTypes)
            : null;
    }

    private BooleanExpression priceLessOrEqual(Integer highestPrice) {
        return highestPrice != null ? product.price.loe(highestPrice) : null;
    }

    private BooleanExpression priceGreaterOrEqual(Integer lowestPrice) {
        return lowestPrice != null ? product.price.goe(lowestPrice) : null;
    }

    private BooleanExpression isEvent(Boolean isEvent) {
        return isEvent != null && isEvent ? event.product.isNotNull() : null;
    }

    private BooleanBuilder searchKeyword(String keyword,
        StringExpression productAndManufacturerName) {
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword != null) {
            String[] keywords = keyword.split(" ");
            for (String k : keywords) {
                builder.and(productAndManufacturerName.contains(k));
            }
        }
        return builder;
    }

}
