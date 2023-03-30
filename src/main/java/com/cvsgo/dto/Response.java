package com.cvsgo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Response {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime timestamp = LocalDateTime.now();

}
