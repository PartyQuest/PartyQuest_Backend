package com.partyquest.backend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
//@WebMvcTest(TestController.class)
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class TestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("test01")
    void test01() throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/test01/t1")
        ).andDo(document("t1")).andExpect(status().isOk());
//        mockMvc.perform(
//                get("/test01/t1"))
//                .andDo(
//                        document("/t1", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()))
//        ).andExpect(status().isOk());
    }
}
