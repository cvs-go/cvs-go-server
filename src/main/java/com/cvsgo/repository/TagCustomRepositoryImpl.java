package com.cvsgo.repository;

import static com.cvsgo.entity.QProductLike.productLike;
import static com.cvsgo.entity.QTag.tag;
import static com.cvsgo.entity.QUserTag.userTag;

import com.cvsgo.dto.product.QReadProductLikeTagResponseDto;
import com.cvsgo.dto.product.ReadProductLikeTagResponseDto;
import com.cvsgo.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TagCustomRepositoryImpl implements TagCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<ReadProductLikeTagResponseDto> findTop3ByProduct(Product product) {
        return queryFactory.select(new QReadProductLikeTagResponseDto(
                userTag.tag.id,
                userTag.tag.name,
                userTag.tag.count()
            ))
            .from(userTag)
            .join(userTag.tag, tag)
            .join(productLike).on(productLike.user.eq(userTag.user))
            .where(productLike.product.eq(product))
            .groupBy(userTag.tag.id, userTag.tag.name)
            .orderBy(userTag.tag.count().desc())
            .limit(3)
            .fetch();
    }

}
