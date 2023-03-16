package com.cvsgo.repository;

import com.cvsgo.dto.product.ProductResponseDto;
import com.cvsgo.dto.product.ProductSearchFilter;
import com.cvsgo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {

    Page<ProductResponseDto> searchByFilter(User user, ProductSearchFilter filter, Pageable pageable);

}
