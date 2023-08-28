package com.cvsgo.repository;

import com.cvsgo.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long>, TagCustomRepository {

}
