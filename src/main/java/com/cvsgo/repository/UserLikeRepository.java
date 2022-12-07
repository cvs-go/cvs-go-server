package com.cvsgo.repository;

import com.cvsgo.entity.UserLike;
import com.cvsgo.entity.UserLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLikeRepository extends JpaRepository<UserLike, UserLikeId> {

}
