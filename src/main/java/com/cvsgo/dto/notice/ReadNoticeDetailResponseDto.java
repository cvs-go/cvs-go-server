package com.cvsgo.dto.notice;

import com.cvsgo.entity.Notice;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class ReadNoticeDetailResponseDto {

    private final Long id;

    private final String title;

    private final String content;

    private final List<String> noticeImageUrls;

    @JsonFormat(pattern = "yy.MM.dd", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    public ReadNoticeDetailResponseDto(Notice notice, List<String> noticeImageUrls) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.noticeImageUrls = noticeImageUrls;
        this.createdAt = notice.getCreatedAt();
    }

    public static ReadNoticeDetailResponseDto of(Notice notice, List<String> noticeImageUrls) {
        return new ReadNoticeDetailResponseDto(notice, noticeImageUrls);
    }
}
