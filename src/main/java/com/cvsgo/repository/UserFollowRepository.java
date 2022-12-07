package com.cvsgo.repository;

import com.cvsgo.entity.UserFollow;
import com.cvsgo.entity.UserFollowId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<UserFollow, UserFollowId> {

}
