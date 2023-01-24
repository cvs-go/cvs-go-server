package com.cvsgo.controller;

import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.exception.ExceptionConstants;
import com.cvsgo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(sharedHttpSession())
                .build();
    }

    @ParameterizedTest
    @DisplayName("이메일 형식에 맞지 않으면 회원가입 API 호출시 HTTP 400을 응답한다")
    @ValueSource(strings = {"abc", "11111", "email1234", "@gmail.com", "abc1234@"})
    void respond_400_when_email_does_not_match_email_format(String email) throws Exception {
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
                .email(email)
                .password("11111111aa!!")
                .nickname("닉네임")
                .tagIds(Arrays.asList(2L, 3L, 7L))
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @ParameterizedTest
    @DisplayName("비밀번호가 10자 이상 20자 이하가 아니라면 회원가입 API 호출시 HTTP 400을 응답한다")
    @ValueSource(strings = {"1a!", "123a!", "1234a!", "1234567a!", "123456789abcdefgh!!!!"})
    @NullAndEmptySource
    void respond_400_when_length_of_password_does_not_meet_password_policy(String password) throws Exception {
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
                .email("abc@naver.com")
                .password(password)
                .nickname("닉네임")
                .tagIds(Arrays.asList(2L, 3L, 7L))
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @ParameterizedTest
    @DisplayName("비밀번호가 영문, 숫자, 특수문자를 모두 포함하지 않으면 회원가입 API 호출시 HTTP 400을 응답한다")
    @ValueSource(strings = {"123456789a", "a123456789", "123456789!", "!123456789", "abcdefghijk", "12345678910", "1~!@#$%^&*()", "~!~!~!~!~!"})
    void respond_400_when_password_does_not_meet_password_policy(String password) throws Exception {
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
                .email("abc@naver.com")
                .password(password)
                .nickname("닉네임")
                .tagIds(Arrays.asList(2L, 3L, 7L))
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @ParameterizedTest
    @DisplayName("닉네임이 2자 이상 8자 이하가 아니면 회원가입 API 호출시 HTTP 400을 응답한다")
    @ValueSource(strings = {"김", "닉", "열글자닉네임을만들자", "123456789"})
    void respond_400_when_length_of_nickname_does_not_meet_nickname_policy(String nickname) throws Exception {
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
                .email("abc@naver.com")
                .password("111111111a!")
                .nickname(nickname)
                .tagIds(Arrays.asList(2L, 3L, 7L))
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입에 성공하면 201을 응답한다")
    void respond_201_when_sign_up_succeed() throws Exception {
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
                .email("abc@naver.com")
                .password("111111111a!")
                .nickname("닉네임")
                .tagIds(Arrays.asList(2L, 3L, 7L))
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("해당 닉네임을 가진 계정이 존재하면 회원가입 API 호출시 HTTP 409를 응답한다")
    void respond_409_when_nickname_conflicts() throws Exception {
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
                .email("abc@naver.com")
                .password("111111111a!")
                .nickname("닉네임")
                .tagIds(Arrays.asList(2L, 3L, 7L))
                .build();

        given(userService.signUp(any())).willThrow(ExceptionConstants.DUPLICATE_NICKNAME);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    @DisplayName("해당 이메일을 가진 계정이 존재하면 회원가입 API 호출시 HTTP 409를 응답한다")
    void respond_409_when_email_conflicts() throws Exception {
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
                .email("abc@naver.com")
                .password("111111111a!")
                .nickname("닉네임")
                .tagIds(Arrays.asList(2L, 3L, 7L))
                .build();

        given(userService.signUp(any())).willThrow(ExceptionConstants.DUPLICATE_EMAIL);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    @DisplayName("해당 이메일로 가입된 계정이 존재하는 경우 이메일 중복 검사에서 true를 응답한다")
    void respond_true_when_email_exists() throws Exception {
        final String email = "aaa@naver.com";
        given(userService.isDuplicatedEmail(email)).willReturn(true);

        mockMvc.perform(get("/api/users/emails/{email}/exists", email).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("true")))
                .andDo(print());
    }

    @Test
    @DisplayName("해당 이메일로 가입된 계정이 없으면 이메일 중복 검사에서 false를 응답한다")
    void respond_false_when_email_exists() throws Exception {
        final String email = "aaa@naver.com";
        given(userService.isDuplicatedEmail(email)).willReturn(false);

        mockMvc.perform(get("/api/users/emails/{email}/exists", email).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("false")))
                .andDo(print());
    }

}
