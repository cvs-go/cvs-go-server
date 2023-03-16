package com.cvsgo.dto.tag;

import com.cvsgo.entity.Tag;
import lombok.Getter;

@Getter
public class TagResponseDto {

    private final Long id;

    private final String name;

    private final Integer group;

    private TagResponseDto(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.group = tag.getGroup();
    }

    public static TagResponseDto from(Tag tag) {
        return new TagResponseDto(tag);
    }

}
