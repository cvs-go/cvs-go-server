package com.cvsgo.dto.promotion;

import com.cvsgo.entity.Promotion;
import lombok.Getter;

@Getter
public class ReadPromotionResponseDto {

    private final Long id;

    private final String imageUrl;

    private final String landingUrl;

    public ReadPromotionResponseDto(Promotion promotion) {
        this.id = promotion.getId();
        this.imageUrl = promotion.getImageUrl();
        this.landingUrl = promotion.getLandingUrl();
    }

    public static ReadPromotionResponseDto from(Promotion promotion) {
        return new ReadPromotionResponseDto(promotion);
    }
}
