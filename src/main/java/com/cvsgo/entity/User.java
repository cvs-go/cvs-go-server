package com.cvsgo.entity;

import static com.cvsgo.exception.ExceptionConstants.INVALID_PASSWORD;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String userId;

    @NotNull
    private String password;

    @NotNull
    @Column(unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String profileImageUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTag> userTags = new ArrayList<>();

    @Builder
    public User(Long id, String userId, String password, String nickname, Role role) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public static User create(String userId, String password, String nickname, List<Tag> tags) {
        User user = User.builder()
                .userId(userId)
                .password(password)
                .nickname(nickname)
                .role(Role.ASSOCIATE)
                .build();
        for (Tag tag : tags) {
            user.addTag(tag);
        }
        return user;
    }

    public void validatePassword(String password, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(password, this.password)) {
            throw INVALID_PASSWORD;
        }
    }

    public void addTag(Tag tag) {
        UserTag userTag = UserTag.builder()
                .user(this)
                .tag(tag)
                .build();
        userTags.add(userTag);
    }

    public void updateRoleToRegular() {
        this.role = Role.REGULAR;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateTag(List<Tag> tags) {
        for (Tag tag : tags) {
            if (userTags.stream().noneMatch(userTag -> userTag.getTag().equals(tag))) {
                addTag(tag);
            }
        }
        userTags.removeIf(userTag -> !tags.contains(userTag.getTag()));
    }

}
