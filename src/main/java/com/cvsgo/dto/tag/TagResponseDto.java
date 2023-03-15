package com.cvsgo.dto.tag;

import com.cvsgo.entity.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class TagResponseDto {

    @JsonIgnore
    private final Tag tag;

    private TagResponseDto(Tag tag) {
        this.tag = tag;
    }

    public static TagResponseDto of(Tag tag) {
    public static TagResponseDto from(Tag tag) {
        return new TagResponseDto(tag);
    }

    public Long getId() {
        return tag.getId();
    }

    public String getName() {
        return tag.getName();
    }

    public Integer getGroup() {
        return tag.getGroup();
    }
}
