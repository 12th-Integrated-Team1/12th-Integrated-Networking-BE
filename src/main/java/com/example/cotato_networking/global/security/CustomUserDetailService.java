package com.example.cotato_networking.global.security;

import com.example.cotato_networking.domain.User;
import com.example.cotato_networking.global.exception.AppException;
import com.example.cotato_networking.global.exception.auth.AuthErrorCode;
import com.example.cotato_networking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new AppException(AuthErrorCode.NOT_FOUND));

        return new CustomUserDetails(user);
    }

}
