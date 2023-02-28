package com.cvsgo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@IdClass(UserFollowId.class)
public class UserFollow {

    @Id
    @ManyToOne
    @JoinColumn(name = "following_id")
    private User following;

    @Id
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    public UserFollow(User following, User follower) {
        this.following = following;
        this.follower = follower;
    }
}
