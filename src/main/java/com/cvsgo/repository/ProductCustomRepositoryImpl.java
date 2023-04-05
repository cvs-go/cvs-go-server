package com.cvsgo.repository;

import static com.cvsgo.entity.QConvenienceStore.convenienceStore;
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
import com.cvsgo.dto.product.QSearchProductDetailQueryDto;
import com.cvsgo.dto.product.QSearchProductQueryDto;
import com.cvsgo.dto.product.SearchProductDetailQueryDto;
import com.cvsgo.dto.product.SearchProductQueryDto;
import com.cvsgo.dto.product.SearchProductRequestDto;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
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

    public List<SearchProductQueryDto> searchByFilter(User loginUser,
        SearchProductRequestDto searchFilter, Pageable pageable) {
        NumberPath<Double> avgRating = Expressions.numberPath(Double.class, "avgRating");
        NumberPath<Double> score = Expressions.numberPath(Double.class, "score");
        return queryFactory.select(new QSearchProductQueryDto(
                product.id,
                product.name,
                product.price,
                product.imageUrl,
                product.category.id,
                manufacturer.name,
                productLike,
                productBookmark,
                review.count(),
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
                            convenienceStoreEq(searchFilter.getConvenienceStoreIds()),
                            eventTypeEq(searchFilter.getEventTypes()),
                            categoryEq(searchFilter.getCategoryIds()),
                            priceLessOrEqual(searchFilter.getHighestPrice()),
                            priceGreaterOrEqual(searchFilter.getLowestPrice())
                        ))
            )
            .groupBy(product)
            .orderBy(
                sortBy(searchFilter.getSortBy(), score, avgRating).toArray(OrderSpecifier[]::new))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    public Long countByFilter(SearchProductRequestDto searchFilter) {
        return queryFactory.select(product.count())
            .from(product)
            .where(
                product.in(
                    selectDistinct(sellAt.product)
                        .from(sellAt)
                        .leftJoin(event).on(sellAt.product.eq(event.product))
                        .where(
                            convenienceStoreEq(searchFilter.getConvenienceStoreIds()),
                            eventTypeEq(searchFilter.getEventTypes()),
                            categoryEq(searchFilter.getCategoryIds()),
                            priceLessOrEqual(searchFilter.getHighestPrice()),
                            priceGreaterOrEqual(searchFilter.getLowestPrice())
                        ))
            )
            .fetchFirst();
    }

    public List<ConvenienceStoreEventQueryDto> findConvenienceStoreEventsByProductIds(
        List<Long> productIds) {
        return queryFactory
            .select(new QConvenienceStoreEventQueryDto(
                sellAt.product.id,
                sellAt.convenienceStore.name,
                event))
            .from(sellAt)
            .leftJoin(event).on(sellAt.product.eq(event.product)
                .and(sellAt.convenienceStore.eq(event.convenienceStore)))
            .where(sellAt.product.id.in(productIds))
            .fetch();
    }

    public Optional<SearchProductDetailQueryDto> findByProductId(User loginUser, Long productId) {
        return Optional.ofNullable(queryFactory
            .select(new QSearchProductDetailQueryDto(
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
            .fetchFirst());
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
            .leftJoin(convenienceStore).on(sellAt.convenienceStore.eq(convenienceStore))
            .where(sellAt.product.id.eq(productId))
            .fetch();
    }

    private static List<OrderSpecifier> sortBy(ProductSortBy sortBy, NumberPath<Double> score,
        NumberPath<Double> avgRating) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        if (sortBy != null) {
            switch (sortBy) {
                case SCORE -> {
                    orderSpecifiers.add(score.desc());
                    orderSpecifiers.add(avgRating.desc());
                    orderSpecifiers.add(product.likeCount.desc());
                    orderSpecifiers.add(product.createdAt.desc());
                }
                case RATING -> {
                    orderSpecifiers.add(avgRating.desc());
                    orderSpecifiers.add(product.likeCount.desc());
                    orderSpecifiers.add(product.createdAt.desc());
                }
                case LIKE -> {
                    orderSpecifiers.add(product.likeCount.desc());
                    orderSpecifiers.add(avgRating.desc());
                    orderSpecifiers.add(product.createdAt.desc());
                }
            }
        } else {
            orderSpecifiers.add(score.desc());
            orderSpecifiers.add(avgRating.desc());
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

}
