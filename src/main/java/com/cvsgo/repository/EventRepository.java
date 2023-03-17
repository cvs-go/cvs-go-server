package com.cvsgo.repository;

import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.Event;
import com.cvsgo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

    Event findByProductAndConvenienceStore(Product product, ConvenienceStore convenienceStore);

}
