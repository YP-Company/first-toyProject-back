package com.youngpotato.firsttoyprojectback.web.controller;

import com.youngpotato.firsttoyprojectback.common.exception.ApiException;
import com.youngpotato.firsttoyprojectback.common.exception.ErrorMessage;
import com.youngpotato.firsttoyprojectback.domain.member.MemberRepository;
import com.youngpotato.firsttoyprojectback.domain.post.Post;
import com.youngpotato.firsttoyprojectback.domain.post.PostRepository;
import com.youngpotato.firsttoyprojectback.service.MainService;
import com.youngpotato.firsttoyprojectback.web.dto.MemberDTO;
import com.youngpotato.firsttoyprojectback.web.dto.SignUpDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;
    private final MemberRepository memberRepository; //테스트용 선언
    private final PostRepository postRepository; //테스트용 선언

    @GetMapping("/home")
    public String home() {
        return "<h1>home</h1>";
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<MemberDTO> signup(@RequestBody SignUpDTO signUpDto) {
        return ResponseEntity.ok(mainService.signup(signUpDto));
    }

    /**
     * JWT를 사용하면 UserDetailsService를 호출하지 않기 때문에 @AuthenticationPrincipal 사용이 불가능.
     * 왜냐하면 @AuthenticationPrincipal은 UserDetailsService에서 리턴될 때 만들어지기 때문
     */
    // 유저 혹은 매니저 혹은 어드민이 접근 가능
    @GetMapping("/user")
    public String user(Authentication authentication) {
        return "<h1>user</h1>";
    }

    // 매니저 혹은 어드민이 접근 가능
    @GetMapping("/manager/reports")
    public String reports(Authentication authentication) {
        return "<h1>reports</h1>";
    }

    // Admin만 접근 가능
    @GetMapping("/admin")
    public String users(Authentication authentication) {
        return "<h1>Admin</h1>";
    }

    // auditing test api
    @PostMapping("/user/post")
    public void savePost() {
        Post post = new Post();
        post.setMember(memberRepository.findByEmail("woojin97318@gmail.com"));
        post.setTitle("testTitle2");
        post.setContent("testContent2");

        postRepository.save(post);
    }
    @PatchMapping("/user/post")
    public void updatePost() {
        Post post = postRepository.findById(1l)
                .orElseThrow(() -> new ApiException("MemberId Not Found", ErrorMessage.USER_NOT_FOUND));

        post.setContent("내용 수정2");

        postRepository.save(post);
    }
}
