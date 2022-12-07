package com.cvsgo.repository;

import com.cvsgo.entity.UserTag;
import com.cvsgo.entity.UserTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTagRepository extends JpaRepository<UserTag, UserTagId> {

}
