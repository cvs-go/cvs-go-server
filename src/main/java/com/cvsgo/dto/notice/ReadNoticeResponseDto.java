package com.cvsgo.dto.notice;

import com.cvsgo.entity.Notice;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Getter;

@Getter
public class ReadNoticeResponseDto {

    private final Long id;

    private final String title;

    @JsonFormat(pattern = "yy.MM.dd", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    private final boolean isNew;

    public ReadNoticeResponseDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.createdAt = notice.getCreatedAt();
        this.isNew = notice.getCreatedAt() != null
            && ChronoUnit.DAYS.between(notice.getCreatedAt().toLocalDate(), LocalDate.now()) <= 7;
    }

    public static ReadNoticeResponseDto from(Notice notice) {
        return new ReadNoticeResponseDto(notice);
    }

}
