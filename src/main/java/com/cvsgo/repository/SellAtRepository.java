package com.cvsgo.repository;

import com.cvsgo.entity.SellAt;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface SellAtRepository extends JpaRepository<SellAt, Long> {

    @EntityGraph(attributePaths = {"convenienceStore", "product"})
    List<SellAt> findByProductId(@Param("productId") Long productId);

}
