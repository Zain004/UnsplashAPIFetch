package com.example.unsplashapi.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder // lettere bygge i api'et
@JsonInclude(JsonInclude.Include.NON_NULL) // fjern nullfelter fra Json Response
public class APIResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final HttpStatus status;
    private final LocalDateTime timeStamp;
    private final String version;
}
