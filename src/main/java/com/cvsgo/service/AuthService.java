package com.cvsgo.service;

import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_USER;
import static com.cvsgo.exception.ExceptionConstants.UNAUTHORIZED_USER;
import static com.cvsgo.util.AuthConstants.ACCESS_TOKEN_TTL_MILLISECOND;
import static com.cvsgo.util.AuthConstants.REFRESH_TOKEN_TTL_MILLISECOND;
import static com.cvsgo.util.AuthConstants.TOKEN_TYPE;

import com.cvsgo.dto.auth.LoginRequestDto;
import com.cvsgo.dto.auth.LoginResponseDto;
import com.cvsgo.dto.auth.ReissueTokenResponseDto;
import com.cvsgo.entity.RefreshToken;
import com.cvsgo.entity.User;
import com.cvsgo.exception.NotFoundException;
import com.cvsgo.exception.UnauthorizedException;
import com.cvsgo.repository.RefreshTokenRepository;
import com.cvsgo.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final Key key;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(@Value("${jwt.secret-key}") final String secretKey,
        UserRepository userRepository, PasswordEncoder passwordEncoder,
        RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 로그인을 진행한다.
     *
     * @param request 로그인 요청 정보
     * @return 토큰 정보
     * @throws NotFoundException     해당하는 아이디를 가진 사용자가 없는 경우
     * @throws UnauthorizedException 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUserId(request.getEmail())
            .orElseThrow(() -> NOT_FOUND_USER);
        user.validatePassword(request.getPassword(), passwordEncoder);

        String accessToken = createAccessToken(user, key, ACCESS_TOKEN_TTL_MILLISECOND);
        RefreshToken refreshToken = RefreshToken.create(user, key, REFRESH_TOKEN_TTL_MILLISECOND);

        refreshTokenRepository.save(refreshToken);

        return LoginResponseDto.builder()
            .userId(user.getId())
            .userNickname(user.getNickname())
            .accessToken(accessToken)
            .refreshToken(refreshToken.getToken())
            .tokenType(TOKEN_TYPE)
            .build();
    }

    /**
     * DB에 저장되어 있는 사용자의 리프레시 토큰을 삭제하여 로그아웃 처리를 한다.
     *
     * @param token 리프레시 토큰
     * @throws UnauthorizedException 토큰이 유효하지 않은 경우
     */
    @Transactional
    public void logout(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> UNAUTHORIZED_USER);
        refreshTokenRepository.delete(refreshToken);
    }

    /**
     * 액세스 토큰과 리프레시 토큰을 재발급한다.
     *
     * @param token 리프레시 토큰
     * @return 토큰 정보
     * @throws UnauthorizedException 토큰이 유효하지 않은 경우
     */
    @Transactional
    public ReissueTokenResponseDto reissueToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> UNAUTHORIZED_USER);
        refreshToken.updateToken(key, REFRESH_TOKEN_TTL_MILLISECOND);
        refreshTokenRepository.save(refreshToken);

        String accessToken = createAccessToken(refreshToken.getUser(), key,
            ACCESS_TOKEN_TTL_MILLISECOND);

        return ReissueTokenResponseDto.builder()
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

    /**
     * 토큰 타입 부분을 제거하고 토큰 문자열만 분리하여 꺼낸다.
     *
     * @param authorizationHeaderValue Authorization 헤더
     * @param tokenType                토큰 타입
     * @return 토큰 문자열
     */
    public String extractToken(String authorizationHeaderValue, String tokenType) {
        String[] strTokens = authorizationHeaderValue.split(" ");
        if (strTokens.length == 2 && tokenType.equals(strTokens[0])) {
            return strTokens[1];
        }
        return null;
    }

    /**
     * 해당 토큰이 유효한지 검사한다.
     *
     * @param token 유효성 검사하려는 토큰
     * @throws ExpiredJwtException 토큰이 만료된 경우
     * @throws SignatureException  토큰의 시그니처가 다른 경우
     */
    public void validateToken(String token) {
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);
    }

    @Transactional(readOnly = true)
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
