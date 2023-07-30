package com.youngpotato.firsttoyprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MainController {

    // 모두 접근 허용
    @GetMapping("/home")
    public String home() {
        return "<h1>home</h1>";
    }

    // 유저 혹은 매니저 혹은 어드민이 접근 가능
    @GetMapping("/user")
    public String user(Authentication authentication) {
        return "<h1>user</h1>";
    }

    // 매니저 혹은 어드민이 접근 가능
    @GetMapping("/manager")
    public String reports(Authentication authentication) {
        return "<h1>manager</h1>";
    }

    // Admin만 접근 가능
    @GetMapping("/admin")
    public String users(Authentication authentication) {
        return "<h1>Admin</h1>";
    }
}
