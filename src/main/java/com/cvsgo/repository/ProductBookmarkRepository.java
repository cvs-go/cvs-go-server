package com.cvsgo.repository;

import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductBookmarkRepository extends JpaRepository<ProductBookmark, Long> {

    Optional<ProductBookmark> findByProductAndUser(Product product, User user);

    boolean existsByProductAndUser(Product product, User user);

}
