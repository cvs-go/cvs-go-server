package com.cvsgo.dto.tag;

import com.cvsgo.entity.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class TagResponseDto {

    @JsonIgnore
    private final Tag tag;

    public TagResponseDto(Tag tag) {
        this.tag = tag;
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
