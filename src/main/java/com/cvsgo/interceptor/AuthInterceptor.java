package com.cvsgo.interceptor;

import com.cvsgo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.cvsgo.exception.ExceptionConstants.UNAUTHORIZED_USER;
import static com.cvsgo.util.AuthConstants.TOKEN_TYPE;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (pathMatcher.match("/api/products/*/reviews", request.getRequestURI())
            && HttpMethod.GET.matches(request.getMethod())) {
            return true;
        }
        if (authorizationHeader == null) {
            throw UNAUTHORIZED_USER;
        }
        String token = authService.extractToken(authorizationHeader, TOKEN_TYPE);
        authService.validateToken(token);
        return true;
    }

}
