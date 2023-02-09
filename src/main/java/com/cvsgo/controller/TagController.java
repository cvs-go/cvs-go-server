package com.cvsgo.controller;

import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.dto.tag.TagResponseDto;
import com.cvsgo.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    public SuccessResponse<List<TagResponseDto>> getTagList() {
        return SuccessResponse.of(tagService.getTagList());
    }

}
