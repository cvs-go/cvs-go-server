package com.cvsgo.dto.notice;

import com.cvsgo.entity.Notice;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReadNoticeDetailResponseDto {

    private final Long id;

    private final String title;

    private final String content;

    @JsonFormat(pattern = "yy.MM.dd", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    public ReadNoticeDetailResponseDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.createdAt = notice.getCreatedAt();
    }

    public static ReadNoticeDetailResponseDto from(Notice notice) {
        return new ReadNoticeDetailResponseDto(notice);
    }
}
