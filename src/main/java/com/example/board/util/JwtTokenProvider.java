package com.example.board.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // HS256에 적합한 비밀키 생성

    // 토큰 유효 기간 설정
    private final long validityInMilliseconds = 3600000; // 1시간

    //토큰 생성
    public String createToken(String member_id) {
        Claims claims = Jwts.claims().setSubject(member_id); // 클레임에 memberId 설정

        // 유효 기간 설정
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        // 토큰 생성
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey) // 비밀키로 서명
                .compact();
    }

    // JWT에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey) // 비밀키로 서명 검증
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
    	  try {
              Jws<Claims> claimsJws = Jwts.parserBuilder()
                      .setSigningKey(secretKey) // 비밀키로 서명 검증
                      .build()
                      .parseClaimsJws(token);
              return true;
          } catch (Exception e) {
              return false;
          }
    }
}
