package com.partyquest.backend.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Builder
@Slf4j
@Data
public class ResponseWrapper<T> {
    private String time;
    private HttpStatus httpStatus;
    private List<T> data;

    public static <T> ResponseWrapper addObject(T obj, HttpStatus status) {
        List<T> result = new ArrayList<T>();
        result.add(obj);
        return ResponseWrapper.<T>builder()
                    .data(result)
                    .httpStatus(status)
                    .time(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
    }
}
