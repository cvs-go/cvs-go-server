package com.cvsgo.repository;

import com.cvsgo.entity.User;
import com.cvsgo.entity.UserTag;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {

    @EntityGraph(attributePaths = "tag")
    List<UserTag> findByUserIn(List<User> users);

    @EntityGraph(attributePaths = {"user", "tag"})
    List<UserTag> findByUserIdIn(List<Long> userIds);

    List<UserTag> deleteByUser(User user);

}
