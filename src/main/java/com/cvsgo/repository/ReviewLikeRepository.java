package com.cvsgo.repository;

import com.cvsgo.entity.Review;
import com.cvsgo.entity.ReviewLike;
import com.cvsgo.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    Optional<ReviewLike> findByReviewAndUser(Review review, User user);

    boolean existsByReviewAndUser(Review review, User user);

}
