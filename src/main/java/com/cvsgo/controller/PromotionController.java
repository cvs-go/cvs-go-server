package com.cvsgo.controller;

import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.dto.promotion.ReadPromotionResponseDto;
import com.cvsgo.service.PromotionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    private SuccessResponse<List<ReadPromotionResponseDto>> readPromotionList() {
        return SuccessResponse.from(promotionService.readPromotionList());
    }

}
