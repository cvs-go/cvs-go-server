package com.cvsgo.repository;

import com.cvsgo.entity.Review;
import com.cvsgo.entity.ReviewLike;
import com.cvsgo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    boolean existsByReviewAndUser(Review review, User user);

}
