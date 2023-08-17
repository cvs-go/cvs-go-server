package com.cvsgo.repository;

import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.User;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

    boolean existsByProductAndUser(Product product, User user);

    long countByUser(User user);

    @Lock(LockModeType.OPTIMISTIC)
    @Query(value = "select p from Review p where p.id = :id")
    Optional<Review> findByIdWithOptimisticLock(@Param("id") Long reviewId);

    List<Review> findAllByUser(User user);
}
