package com.example.cotato_networking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .title("12th-Integrated-Team1")
                .description("코테이토 프론트-백 네트워킹 API 목록입니다.");


        return new OpenAPI()
                .info(info);
    }
}
