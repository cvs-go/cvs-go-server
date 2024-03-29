package com.cvsgo.repository;

import com.cvsgo.dto.product.ConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.ProductSortBy;
import com.cvsgo.dto.product.ReadProductDetailQueryDto;
import com.cvsgo.dto.product.ReadProductQueryDto;
import com.cvsgo.dto.product.ReadProductRequestDto;
import com.cvsgo.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {

    List<ReadProductQueryDto> findAllByFilter(User user, ReadProductRequestDto filter,
        Pageable pageable);

    Long countByFilter(ReadProductRequestDto filter);

    List<ReadProductQueryDto> findAllByUserProductLike(User user, ProductSortBy sortBy, Pageable pageable);

    Long countByUserProductLike(User user);

    List<ReadProductQueryDto> findAllByUserProductBookmark(User user, ProductSortBy sortBy, Pageable pageable);

    Long countByUserProductBookmark(User user);

    List<ConvenienceStoreEventQueryDto> findConvenienceStoreEventsByProductIds(
        List<Long> productIds);

    List<ConvenienceStoreEventQueryDto> findConvenienceStoreEventsByProductId(Long productId);

    Optional<ReadProductDetailQueryDto> findByProductId(User user, Long productId);

}
