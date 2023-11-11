package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Promotion;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Import(TestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PromotionRepositoryTest {

    @Autowired
    PromotionRepository promotionRepository;

    @BeforeEach
    void initData() {
        promotion1 = Promotion.builder().id(1L).name("프로모션1").imageUrl("imageUrl1").landingUrl("landindUrl1")
            .priority(3).startAt(LocalDateTime.now().minusDays(3)).endAt(LocalDateTime.now().minusDays(1)).build();
        promotion2 = Promotion.builder().id(2L).name("프로모션2").imageUrl("imageUrl2").landingUrl("landindUrl2")
            .priority(2).startAt(LocalDateTime.now().minusDays(1)).endAt(LocalDateTime.now().plusDays(1)).build();
        promotion3 = Promotion.builder().id(3L).name("프로모션3").imageUrl("imageUrl3").landingUrl("landindUrl3")
            .priority(1).startAt(LocalDateTime.now().minusDays(5)).endAt(LocalDateTime.now().plusDays(6)).build();
        promotion4 = Promotion.builder().id(2L).name("프로모션4").imageUrl("imageUrl2").landingUrl("landindUrl2")
            .priority(1).startAt(LocalDateTime.now().plusDays(1)).endAt(LocalDateTime.now().plusDays(3)).build();
        promotionRepository.saveAll(List.of(promotion1, promotion2, promotion3, promotion4));
    }

    @Test
    @DisplayName("활성된 프로모션을 조회한다")
    void find_active_promotions() {
        // when
        Page<Promotion> foundPromotions = promotionRepository.findActivePromotions(
            LocalDateTime.now(), PageRequest.of(0, 20));

        // then
        assertThat(foundPromotions).isNotEmpty();
        assertThat(foundPromotions.getContent()).hasSize(2);
        assertThat(foundPromotions.getContent().get(0).getPriority()).isEqualTo(1);
        assertThat(foundPromotions.getContent().get(1).getPriority()).isEqualTo(2);
    }

    private Promotion promotion1;
    private Promotion promotion2;
    private Promotion promotion3;
    private Promotion promotion4;
}
