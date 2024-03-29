package com.cvsgo.repository;

import com.cvsgo.entity.User;
import com.cvsgo.entity.UserFollow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    boolean existsByUserAndFollower(User user, User follower);

    Optional<UserFollow> findByUserAndFollower(User user, User follower);

    List<UserFollow> findAllByFollower(User follower);

}
