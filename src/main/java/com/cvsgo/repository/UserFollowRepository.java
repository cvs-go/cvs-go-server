package com.cvsgo.repository;

import com.cvsgo.entity.User;
import com.cvsgo.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    Boolean existsByUserAndFollower(User user, User follower);

}
