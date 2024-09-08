package com.example.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/chat-websocket/**") // Ignore CSRF for WebSocket endPoints
                .disable()
            )
            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers( "products/update/*", "/products/delete/*", "/products/new", "/products/list", "/products/detail"
//                            ,"members/register", "/members/login", "/").permitAll()
                    .requestMatchers("/**").permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }


    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
