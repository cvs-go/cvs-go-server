package com.cvsgo.controller;

import com.cvsgo.argumentresolver.LoginUserArgumentResolver;
import com.cvsgo.config.WebConfig;
import com.cvsgo.dto.auth.LoginRequestDto;
import com.cvsgo.dto.auth.LogoutRequestDto;
import com.cvsgo.dto.auth.TokenDto;
import com.cvsgo.exception.ExceptionConstants;
import com.cvsgo.interceptor.AuthInterceptor;
import com.cvsgo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.cvsgo.ApiDocumentUtils.getDocumentRequest;
import static com.cvsgo.ApiDocumentUtils.getDocumentResponse;
import static com.cvsgo.util.AuthConstants.TOKEN_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @MockBean
    LoginUserArgumentResolver loginUserArgumentResolver;

    @MockBean
    WebConfig webConfig;

    @MockBean
    private AuthInterceptor authInterceptor;

    @MockBean
    private AuthService authService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(sharedHttpSession())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @DisplayName("로그인에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_login_succeed() throws Exception {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("abc@naver.com")
                .password("password1!")
                .build();
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(getSampleAccessToken())
                .refreshToken(getSampleRefreshToken())
                .tokenType(TOKEN_TYPE)
                .build();

        given(authService.login(any())).willReturn(tokenDto);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auth/login",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("요청 시각"),
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                                fieldWithPath("data.tokenType").type(JsonFieldType.STRING).description("토큰 종류")
                        )
                ));
    }

    @Test
    @DisplayName("해당 이메일을 가진 계정이 존재하지 않으면 로그인 API 호출시 HTTP 400를 응답한다")
    void respond_400_when_user_does_not_exist() throws Exception {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("abc@naver.com")
                .password("password1!")
                .build();

        given(authService.login(any())).willThrow(ExceptionConstants.NOT_FOUND_USER);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 로그인 API 호출시 HTTP 401을 응답한다")
    void respond_401_when_password_is_not_correct() throws Exception {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("abc@naver.com")
                .password("password1!")
                .build();

        given(authService.login(any())).willThrow(ExceptionConstants.INVALID_PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_succeed_to_logout() throws Exception {
        LogoutRequestDto logoutRequestDto = LogoutRequestDto.builder().token(getSampleRefreshToken()).build();

        willDoNothing().given(authService).logout(any());
        mockMvc.perform(post("/api/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + " " + getSampleAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auth/logout",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("token").type(JsonFieldType.STRING).description("리프레시 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("토큰 연장에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_succeed_to_reissue_tokens() throws Exception {

        given(authService.reissueToken(any()))
                .willReturn(TokenDto.builder()
                        .accessToken(getSampleAccessToken())
                        .refreshToken(getSampleRefreshToken())
                        .tokenType(TOKEN_TYPE)
                        .build());

        mockMvc.perform(get("/api/auth/tokens")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_TYPE + " " + getSampleRefreshToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auth/tokens",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("요청 시각"),
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                                fieldWithPath("data.tokenType").type(JsonFieldType.STRING).description("토큰 종류")
                        )
                ));
    }

    private String getSampleAccessToken() {
        return "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYmNAbmF2ZXIuY29tIiwiaWF0IjoxNjc2NDU1NTQzLCJleHAiOjE2NzY0NTczNDN9.1GsEkNAENDMCRdgmcpzZB5EBw8HiAhS3HPqVidhVwmA";
    }

    private String getSampleRefreshToken() {
        return "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NzY0NTU1NDMsImp0aSI6IjQzNDIzMWY1LWU3MTktNDRhOC1hODM0LWZmYjAxN2NmM2M1MyIsImV4cCI6MTY3NzY2NTE0M30.3vitvpssbPtWb7NDM8JjfkYY2vOOq5GgC6cQu85dHVA";
    }

}
