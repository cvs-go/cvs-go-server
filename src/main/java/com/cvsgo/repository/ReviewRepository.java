package com.cvsgo.repository;

import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

    boolean existsByProductAndUser(Product product, User user);
}
