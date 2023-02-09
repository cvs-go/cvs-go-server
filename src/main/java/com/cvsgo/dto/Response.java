package com.cvsgo.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Response {

    private final LocalDateTime timestamp = LocalDateTime.now();

}
