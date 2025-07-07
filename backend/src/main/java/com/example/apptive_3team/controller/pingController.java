package com.example.apptive_3team.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class pingController {

    @GetMapping("/")
    public void index(HttpServletResponse response) throws IOException {
        response.sendRedirect("/index.html"); // 직접 리디렉션 처리
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }


}
