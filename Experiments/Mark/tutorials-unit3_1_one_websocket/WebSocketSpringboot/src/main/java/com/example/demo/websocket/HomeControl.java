package com.example.demo.websocket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

    @RestController
    public class HomeControl {

        @GetMapping("/")
        public String home() {
            return "Hello, this is the homepage!";
    }
}
