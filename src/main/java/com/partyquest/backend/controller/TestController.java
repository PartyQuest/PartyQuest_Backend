package com.partyquest.backend.controller;

import com.partyquest.backend.domain.dto.TestDto;
import com.partyquest.backend.domain.type.FileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/*
* TEST ONLY !!!
* */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    @GetMapping("/t1")
    public ResponseEntity<?> test01() {
        return ResponseEntity.ok().body("tttt");
    }

    @PostMapping("/t2")
    public ResponseEntity<?> test02(@RequestBody String test) {
        log.info(test);
        return ResponseEntity.ok().body(test);
    }
    @PostMapping("/t3")
    public ResponseEntity<?> test03(@RequestBody TestDto type) {
        log.info(type.getType().toString());
        return ResponseEntity.ok().body(type.getType().toString());
    }
}
