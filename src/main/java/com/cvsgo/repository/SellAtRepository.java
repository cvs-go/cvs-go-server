package com.cvsgo.repository;

import com.cvsgo.entity.SellAt;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellAtRepository extends JpaRepository<SellAt, Long> {

    @EntityGraph(attributePaths = {"convenienceStore", "product"})
    List<SellAt> findByProductId(Long productId);

}
