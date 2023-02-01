package com.cvsgo.dto.user;

import com.cvsgo.entity.Role;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class SignUpResponseDto {

    @JsonIgnore
    private final User user;

    @JsonIgnore
    private final List<Tag> tags;

    private SignUpResponseDto(User user, List<Tag> tags) {
        this.user = user;
        this.tags = tags;
    }

    public static SignUpResponseDto of(User user, List<Tag> tags) {
        return new SignUpResponseDto(user, tags);
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
        return tags.stream().map(Tag::getId).toList();
    }

}
