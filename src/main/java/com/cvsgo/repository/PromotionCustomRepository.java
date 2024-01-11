package com.cvsgo.repository;

import com.cvsgo.entity.Promotion;
import java.time.LocalDateTime;
import java.util.List;

public interface PromotionCustomRepository {

    List<Promotion> findActivePromotions(LocalDateTime now);

}
