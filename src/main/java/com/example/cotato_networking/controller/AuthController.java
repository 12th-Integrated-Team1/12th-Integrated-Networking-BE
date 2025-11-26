package com.example.cotato_networking.controller;

import com.example.cotato_networking.domain.User;
import com.example.cotato_networking.dto.request.LoginRequest;
import com.example.cotato_networking.dto.response.LoginResponse;
import com.example.cotato_networking.global.dto.DataResponse;
import com.example.cotato_networking.global.dto.ErrorResponse;
import com.example.cotato_networking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AuthController", description = "로그인, 로그아웃 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "통합 로그인", description = "새로운 사용자를 등록하거나 기존 아이디로 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치 (AUTH-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/login")
    public ResponseEntity<DataResponse<LoginResponse>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        User user = authService.loginOrSignup(request);

        // 세션 생성
        HttpSession session = httpRequest.getSession(true);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext
        );

        LoginResponse response = new LoginResponse(user.getId(), user.getLoginId());

        return ResponseEntity.ok(DataResponse.from(response));
    }

    @Operation(summary = "로그아웃", description = "현재 사용자의 세션을 만료시키고 쿠키를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (COMMON-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/logout")
    public ResponseEntity<DataResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보 있으면 로그아웃 처리
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok(DataResponse.ok());
    }

}
