package com.youngpotato.firsttoyprojectback.web.controller;

import com.youngpotato.firsttoyprojectback.domain.member.MemberRepository;
import com.youngpotato.firsttoyprojectback.web.dto.SignUpDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TestController {

    private final MemberRepository memberRepository;

    @GetMapping("/home")
    public String home() {
        return "<h1>home</h1>";
    }

    /**
     * 회원가입
     */
//    @PostMapping("/signup")
//    public String join(@RequestBody SignUpDTO signUpDto) {
//        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//        user.setRoles("ROLE_USER");
//        userRepository.save(user);
//
//        return "회원가입 완료";
//    }
}
