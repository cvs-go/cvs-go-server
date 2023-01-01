package com.cvsgo.repository;

import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.ProductBookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductBookmarkRepository extends JpaRepository<ProductBookmark, ProductBookmarkId> {

}
