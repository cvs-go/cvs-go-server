package com.cvsgo.repository;

import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductLike;
import com.cvsgo.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    Optional<ProductLike> findByProductAndUser(Product product, User user);

    boolean existsByProductAndUser(Product product, User user);

}
