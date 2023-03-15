package com.cvsgo.dto.user;

import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import lombok.Getter;

import java.util.List;

@Getter
public class SignUpResponseDto {

    private final String email;

    private final String nickname;

    private final Role role;

    private final List<Long> tagIds;

    private SignUpResponseDto(User user) {
        this.email = user.getUserId();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.tagIds = user.getUserTags().stream()
                .map(userTag -> userTag.getTag().getId())
                .toList();
    }

    public static SignUpResponseDto from(User user) {
        return new SignUpResponseDto(user);
    }

}
