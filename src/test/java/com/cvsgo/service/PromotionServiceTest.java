package com.cvsgo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.cvsgo.dto.promotion.ReadPromotionResponseDto;
import com.cvsgo.entity.Promotion;
import com.cvsgo.repository.PromotionRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    PromotionService promotionService;

    @Test
    @DisplayName("프로모션 목록을 정상적으로 조회한다")
    void succeed_to_read_promotion_list() {
        Pageable pageable = PageRequest.of(0, 20);
        given(promotionRepository.findActivePromotions(any(), any())).willReturn(getPromotionList());

        Page<ReadPromotionResponseDto> result = promotionService.readPromotionList(pageable);

        assertEquals(getPromotionList().getTotalElements(), result.getTotalElements());
        then(promotionRepository).should(times(1)).findActivePromotions(any(), any());
    }

    Promotion promotion1 = Promotion.builder()
        .id(1L)
        .imageUrl("imageUrl1")
        .landingUrl("landindUrl1")
        .build();

    Promotion promotion2 = Promotion.builder()
        .id(2L)
        .imageUrl("imageUrl2")
        .landingUrl("landindUrl2")
        .build();

    private Page<Promotion> getPromotionList() {
        return new PageImpl<>(List.of(promotion1, promotion2));
    }
}
