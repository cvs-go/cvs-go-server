package com.cvsgo.repository;

import com.cvsgo.dto.product.ConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.ReadProductDetailQueryDto;
import com.cvsgo.dto.product.ReadProductQueryDto;
import com.cvsgo.dto.product.ReadProductRequestDto;
import com.cvsgo.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {

    List<ReadProductQueryDto> searchByFilter(User user, ReadProductRequestDto filter,
        Pageable pageable);

    Long countByFilter(ReadProductRequestDto filter);

    List<ConvenienceStoreEventQueryDto> findConvenienceStoreEventsByProductIds(
        List<Long> productIds);

    Optional<ReadProductDetailQueryDto> findByProductId(User user, Long productId);

    List<ConvenienceStoreEventQueryDto> findConvenienceStoreEventsByProductId(Long productId);

}
