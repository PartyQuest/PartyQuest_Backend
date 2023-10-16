package com.partyquest.backend.config;

import com.partyquest.backend.domain.dto.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@Component
@Slf4j
public class ResponseEntityFactory {
    public static <T> ResponseEntity<?> createResponse(String uriPath, Long uriId, T data) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path(uriPath)
                .buildAndExpand(uriId)
                .toUri();
        return ResponseEntity.created(location).body(ResponseWrapper.addObject(data, HttpStatus.CREATED));
    }
    public static <T> ResponseEntity<?> okResponse(T data) {
        return ResponseEntity.ok().body(
                ResponseWrapper.addObject(Objects.requireNonNullElse(data, "NOT DATA"), HttpStatus.OK)
        );
    }
    public static ResponseEntity<?> noResponse() {
        return ResponseEntity.noContent().build();
    }
}
