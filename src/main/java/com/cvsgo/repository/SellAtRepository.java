package com.cvsgo.repository;

import com.cvsgo.entity.SellAt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SellAtRepository extends JpaRepository<SellAt, Long> {

    @Query("SELECT s FROM SellAt s JOIN FETCH s.convenienceStore JOIN FETCH s.product WHERE s.product.id = :productId")
    List<SellAt> findByProductId(Long productId);

}
