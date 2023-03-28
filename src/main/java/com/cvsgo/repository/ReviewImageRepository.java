package com.cvsgo.repository;

import com.cvsgo.entity.ReviewImage;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    @EntityGraph(attributePaths = "review")
    List<ReviewImage> findByReviewIdIn(List<Long> reviewIds);
}
