package com.cvsgo.service;

import com.cvsgo.dto.tag.TagResponseDto;
import com.cvsgo.entity.Tag;
import com.cvsgo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    /**
     * 사용자 태그를 조회한다.
     * @return 사용자 태그 목록
     */
    public List<TagResponseDto> getTagList() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream().map(TagResponseDto::new).toList();
    }

}
