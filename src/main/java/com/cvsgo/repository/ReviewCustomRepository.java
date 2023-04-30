package com.cvsgo.repository;

import com.cvsgo.dto.review.ReadProductReviewQueryDto;
import com.cvsgo.dto.review.ReadProductReviewRequestDto;
import com.cvsgo.dto.review.ReadReviewQueryDto;
import com.cvsgo.dto.review.ReadReviewRequestDto;
import com.cvsgo.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {

    List<ReadReviewQueryDto> findAllByFilter(User user, ReadReviewRequestDto request,
        Pageable pageable);

    List<ReadProductReviewQueryDto> findAllByProductIdAndFilter(User loginUser, Long productId,
        ReadProductReviewRequestDto filter, Pageable pageable);

    Long countByProductIdAndFilter(Long productId, ReadProductReviewRequestDto filter);

}
