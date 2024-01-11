package com.cvsgo.repository;

import static com.cvsgo.entity.QPromotion.promotion;

import com.cvsgo.entity.Promotion;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PromotionCustomRepositoryImpl implements PromotionCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<Promotion> findActivePromotions(LocalDateTime now) {
        return queryFactory.selectFrom(promotion)
            .where(promotion.startAt.loe(now).and(promotion.endAt.goe(now)))
            .orderBy(promotion.priority.asc())
            .fetch();
    }

}
