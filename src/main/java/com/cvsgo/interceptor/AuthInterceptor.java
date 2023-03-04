package com.cvsgo.interceptor;

import com.cvsgo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.cvsgo.exception.ExceptionConstants.UNAUTHORIZED_USER;
import static com.cvsgo.util.AuthConstants.TOKEN_TYPE;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null) {
            throw UNAUTHORIZED_USER;
        }
        String accessToken = authService.extractToken(authorizationHeader, TOKEN_TYPE);
        authService.getLoginUser(accessToken).orElseThrow(() -> UNAUTHORIZED_USER);
        return true;
    }

}
