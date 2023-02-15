package com.cvsgo.service;

import com.cvsgo.dto.auth.LoginRequestDto;
import com.cvsgo.dto.auth.TokenDto;
import com.cvsgo.entity.RefreshToken;
import com.cvsgo.entity.User;
import com.cvsgo.repository.RefreshTokenRepository;
import com.cvsgo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.cvsgo.exception.auth.UserNotFoundException;
import com.cvsgo.exception.auth.InvalidPasswordException;

import java.security.Key;
import java.util.Date;

import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;

    private static final String TOKEN_TYPE = "Bearer";

    /**
     * 로그인을 진행한다.
     * @param loginRequestDto 로그인 요청 정보
     * @throws UserNotFoundException 해당하는 아이디를 가진 사용자가 없는 경우
     * @throws InvalidPasswordException 비밀번호가 일치하지 않는 경우
     * @return 토큰 정보
     */
    public TokenDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByUserId(loginRequestDto.getEmail()).orElseThrow(() -> NOT_FOUND_USER);
        user.validatePassword(loginRequestDto.getPassword(), passwordEncoder);

        final long ACCESS_TOKEN_TTL_MILLISECOND = 1000 * 60 * 30;
        final long REFRESH_TOKEN_TTL_MILLISECOND = 1000 * 60 * 60 * 24 * 14;
        Key key = createKey();
        String accessToken = createAccessToken(user, key, ACCESS_TOKEN_TTL_MILLISECOND);
        RefreshToken refreshToken = RefreshToken.create(user, key, REFRESH_TOKEN_TTL_MILLISECOND);

        refreshTokenRepository.save(refreshToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType(TOKEN_TYPE)
                .build();
    }

    private String createAccessToken(User user, Key key, long ttlMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(user.getUserId())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMillis))
                .signWith(key)
                .compact();
    }

    private Key createKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
