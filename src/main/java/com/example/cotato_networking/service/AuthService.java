package com.example.cotato_networking.service;

import com.example.cotato_networking.domain.User;
import com.example.cotato_networking.dto.request.LoginRequest;
import com.example.cotato_networking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public User loginOrSignup(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByLoginId(request.getLoginId());
        User user;

        // 없는 경우 회원가입
        if (userOptional.isEmpty()) {
            User newUser = User.builder()
                    .loginId(request.getLoginId())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
            user = userRepository.save(newUser);
        } else { // 있는 경우
            user = userOptional.get();
        }

        // 시큐리티에게 로그인 검사 요청
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.getLoginId(),
                request.getPassword()
        );

        // 비밀번호 확인
        Authentication authentication = authenticationManager.authenticate(token);

        // 세션 처리
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return user;
    }

}
