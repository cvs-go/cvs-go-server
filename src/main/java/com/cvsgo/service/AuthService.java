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
import com.cvsgo.exception.auth.NotFoundUserException;
import com.cvsgo.exception.auth.InvalidPasswordException;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_USER;
import static com.cvsgo.util.AuthConstants.ACCESS_TOKEN_TTL_MILLISECOND;
import static com.cvsgo.util.AuthConstants.REFRESH_TOKEN_TTL_MILLISECOND;
import static com.cvsgo.util.AuthConstants.TOKEN_TYPE;

@Service
public class AuthService {

    private final Key key;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(@Value("${jwt.secret-key}") final String secretKey, UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 로그인을 진행한다.
     * @param loginRequestDto 로그인 요청 정보
     * @throws NotFoundUserException 해당하는 아이디를 가진 사용자가 없는 경우
     * @throws InvalidPasswordException 비밀번호가 일치하지 않는 경우
     * @return 토큰 정보
     */
    public TokenDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByUserId(loginRequestDto.getEmail()).orElseThrow(() -> NOT_FOUND_USER);
        user.validatePassword(loginRequestDto.getPassword(), passwordEncoder);

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

    public String extractAccessToken(String authorizationHeaderValue, String tokenType) {
        String[] strTokens = authorizationHeaderValue.split(" ");
        if (strTokens.length == 2 && tokenType.equals(strTokens[0])) {
            return strTokens[1];
        }
        return null;
    }

    public Optional<User> getLoginUser(String accessToken) {
        if (accessToken == null) {
            return Optional.empty();
        }
        String email = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
        return userRepository.findByUserId(email);
    }

}
