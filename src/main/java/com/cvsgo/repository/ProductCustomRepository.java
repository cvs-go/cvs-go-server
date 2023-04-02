package com.cvsgo.repository;

import com.cvsgo.dto.product.ConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.ProductDetailResponseDto;
import com.cvsgo.dto.product.SearchProductRequestDto;
import com.cvsgo.dto.product.SearchProductQueryDto;
import com.cvsgo.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {

    List<SearchProductQueryDto> searchByFilter(User user, SearchProductRequestDto filter,
        Pageable pageable);

    List<ConvenienceStoreEventQueryDto> findConvenienceStoreEventsByProductIds(
        List<Long> productIds);

    Optional<ProductDetailResponseDto> findByProductId(User user, Long productId);

}
