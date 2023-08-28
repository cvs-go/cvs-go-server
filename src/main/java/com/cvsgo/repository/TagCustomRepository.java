package com.cvsgo.repository;

import com.cvsgo.dto.product.ReadProductLikeTagResponseDto;
import com.cvsgo.entity.Product;
import java.util.List;

public interface TagCustomRepository {

    List<ReadProductLikeTagResponseDto> findTop3ByProduct(Product product);

}
