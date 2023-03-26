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
import com.cvsgo.dto.product.ProductDetailResponseDto;
import com.cvsgo.dto.product.ProductSearchRequestDto;
import com.cvsgo.dto.product.QConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.QProductDetailResponseDto;
import com.cvsgo.dto.product.QSearchProductQueryDto;
import com.cvsgo.dto.product.SearchProductQueryDto;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<SearchProductQueryDto> searchByFilter(User user,
        ProductSearchRequestDto filter, Pageable pageable) {
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
                review.rating.avg()))
            .from(product)
            .leftJoin(productLike)
            .on(productLike.product.eq(product).and(eqProductLikeUser(user)))
            .leftJoin(productBookmark)
            .on(productBookmark.product.eq(product).and(eqProductBookmarkUser(user)))
            .leftJoin(review).on(review.product.eq(product))
            .leftJoin(manufacturer).on(product.manufacturer.eq(manufacturer))
            .where(
                product.in(
                    selectDistinct(sellAt.product)
                        .from(sellAt)
                        .leftJoin(event).on(sellAt.product.eq(event.product))
                        .where(
                            eqConvenienceStore(filter.getConvenienceStoreIds()),
                            eqEventType(filter.getEventTypes()),
                            eqCategory(filter.getCategoryIds()),
                            priceLessOrEqual(filter.getHighestPrice()),
                            priceGreaterOrEqual(filter.getLowestPrice())
                        ))
            )
            .groupBy(product)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    public List<ConvenienceStoreEventQueryDto> findConvenienceStoreEventsByProductIds(
        List<Long> productIds) {
        return queryFactory
            .select(new QConvenienceStoreEventQueryDto(
                sellAt.product.id,
                sellAt.convenienceStore.name,
                event.eventType))
            .from(sellAt)
            .leftJoin(event).on(sellAt.product.eq(event.product)
                .and(sellAt.convenienceStore.eq(event.convenienceStore)))
            .where(sellAt.product.id.in(productIds))
            .fetch();
    }

    public Optional<ProductDetailResponseDto> findByProductId(User user, Long productId) {
        return Optional.ofNullable(queryFactory
            .select(new QProductDetailResponseDto(
                product.as("product"),
                product.manufacturer.as("manufacturer"),
                productLike.as("productLike"),
                productBookmark.as("productBookmark")
            ))
            .from(product)
            .leftJoin(productLike)
            .on(productLike.product.id.eq(productId).and(productLike.user.eq(user)))
            .leftJoin(productBookmark)
            .on(productBookmark.product.id.eq(productId).and(productBookmark.user.eq(user)))
            .where(product.id.eq(productId))
            .fetchFirst());
    }

    private BooleanExpression eqProductLikeUser(User user) {
        return user != null ? productLike.user.eq(user) : null;
    }

    private BooleanExpression eqProductBookmarkUser(User user) {
        return user != null ? productBookmark.user.eq(user) : null;
    }

    private BooleanExpression eqConvenienceStore(List<Long> convenienceStoreIds) {
        return convenienceStoreIds != null && convenienceStoreIds.size() > 0
            ? sellAt.convenienceStore.id.in(convenienceStoreIds)
            : null;
    }

    private BooleanExpression eqCategory(List<Long> categoryIds) {
        return categoryIds != null && categoryIds.size() > 0
            ? product.category.id.in(categoryIds)
            : null;
    }

    private BooleanExpression eqEventType(List<EventType> eventTypes) {
        return eventTypes != null && eventTypes.size() > 0
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
