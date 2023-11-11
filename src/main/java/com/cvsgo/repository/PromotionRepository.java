package com.cvsgo.repository;

import com.cvsgo.entity.Promotion;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query(value = "select p from Promotion p where p.startAt <= :now and p.endAt > :now ORDER BY p.priority")
    Page<Promotion> findActivePromotions(@Param("now") LocalDateTime now, Pageable pageable);

}
