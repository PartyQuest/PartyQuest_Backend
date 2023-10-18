package com.partyquest.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/*
* TEST ONLY !!!
* */
@RestController
@RequestMapping("/test01")
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
}
