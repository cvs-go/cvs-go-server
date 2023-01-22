package com.cvsgo.dto.user;

import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.List;

public class SignUpResponseDto {

    @JsonIgnore
    private final User user;

    @Getter
    private final List<Long> tagIds;

    public SignUpResponseDto(User user, List<Long> tagIds) {
        this.user = user;
        this.tagIds = tagIds;
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

}
