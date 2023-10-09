package com.partyquest.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
* TEST ONLY !!!
* */
@RestController
@RequestMapping("/test01")
public class TestController {
    @GetMapping("/t1")
    public ResponseEntity<?> test01() {
        return ResponseEntity.ok().body("tttt");
    }

}
