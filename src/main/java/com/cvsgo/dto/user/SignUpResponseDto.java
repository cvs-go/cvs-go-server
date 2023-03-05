package com.cvsgo.dto.user;

import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class SignUpResponseDto {

    @JsonIgnore
    private final User user;

    private SignUpResponseDto(User user) {
        this.user = user;
    }

    public static SignUpResponseDto from(User user) {
        return new SignUpResponseDto(user);
    }

    public String getEmail() {
        return user.getUserId();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public Role getRole() {
        return user.getRole();
    }

    public List<Long> getTagIds() {
        return user.getUserTags().stream()
                .map(userTag -> userTag.getTag().getId())
                .toList();
    }

}
