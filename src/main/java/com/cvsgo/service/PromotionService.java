package com.cvsgo.service;

import com.cvsgo.dto.promotion.ReadPromotionResponseDto;
import com.cvsgo.repository.PromotionRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    /**
     * 현재 로컬 날짜 및 시간 기준 활성된 프로모션 목록을 우선순위 순으로 조회한다.
     *
     * @return 프로모션 목록
     */
    @Transactional(readOnly = true)
    public Page<ReadPromotionResponseDto> readPromotionList(Pageable pageable) {
        return promotionRepository.findActivePromotions(LocalDateTime.now(), pageable)
            .map(ReadPromotionResponseDto::from);
    }

}
