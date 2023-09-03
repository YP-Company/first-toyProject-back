package com.youngpotato.firsttoyprojectback.controller;

import com.youngpotato.firsttoyprojectback.common.Constants;
import com.youngpotato.firsttoyprojectback.common.jwt.TokenProvider;
import com.youngpotato.firsttoyprojectback.dto.LoginDTO;
import com.youngpotato.firsttoyprojectback.dto.MemberDTO;
import com.youngpotato.firsttoyprojectback.dto.SignUpDTO;
import com.youngpotato.firsttoyprojectback.dto.TokenDTO;
import com.youngpotato.firsttoyprojectback.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    /**
     * 일반 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<MemberDTO> signup(@Valid @RequestBody SignUpDTO signUpDto) {
        return ResponseEntity.ok(memberService.signup(signUpDto));
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> authorize(@Valid @RequestBody LoginDTO loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        // authenticate() 메소드가 실행 될 때 PrincipalDetailsService의 loadByUser() 메소드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(Constants.JWT_HEADER_STRING, Constants.JWT_TOKEN_PREFIX + jwt);

        return new ResponseEntity<>(new TokenDTO(jwt), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/success-oauth")
    public ResponseEntity<?> createToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return null;
    }
}
