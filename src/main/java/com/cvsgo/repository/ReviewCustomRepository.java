package com.cvsgo.repository;

import com.cvsgo.dto.review.ReadReviewQueryDto;
import com.cvsgo.dto.review.ReadReviewRequestDto;
import com.cvsgo.dto.review.SearchReviewQueryDto;
import com.cvsgo.dto.review.SearchReviewRequestDto;
import com.cvsgo.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {
    List<SearchReviewQueryDto> searchByFilter(User user, SearchReviewRequestDto request, Pageable pageable);

    List<ReadReviewQueryDto> findAllByFilter(User loginUser, Long productId,
        ReadReviewRequestDto filter, Pageable pageable);

    Long countByProductIdAndFilter(Long productId, ReadReviewRequestDto filter);

}
