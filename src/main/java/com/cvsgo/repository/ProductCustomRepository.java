package com.cvsgo.repository;

import com.cvsgo.dto.product.ConvenienceStoreEventQueryDto;
import com.cvsgo.dto.product.ProductDetailResponseDto;
import com.cvsgo.dto.product.SearchProductQueryDto;
import com.cvsgo.dto.product.SearchProductRequestDto;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {

    List<SearchProductQueryDto> searchByFilter(User user, SearchProductRequestDto filter,
        Pageable pageable);

    Long countByFilter(SearchProductRequestDto filter);

    List<ConvenienceStoreEventQueryDto> findConvenienceStoreEventsByProductIds(
        List<Long> productIds);

    Optional<ProductDetailResponseDto> findByProductId(User user, Long productId);

}
