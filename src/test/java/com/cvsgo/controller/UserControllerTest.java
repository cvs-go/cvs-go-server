package com.cvsgo.controller;

import static com.cvsgo.ApiDocumentUtils.documentIdentifier;
import static com.cvsgo.ApiDocumentUtils.getDocumentRequest;
import static com.cvsgo.ApiDocumentUtils.getDocumentResponse;
import static com.cvsgo.exception.ExceptionConstants.BAD_REQUEST_USER_FOLLOW;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_NICKNAME;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_USER_FOLLOW;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_USER;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_USER_FOLLOW;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.cvsgo.argumentresolver.LoginUserArgumentResolver;
import com.cvsgo.config.WebConfig;
import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.dto.user.SignUpResponseDto;
import com.cvsgo.dto.user.UpdateUserRequestDto;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.exception.ExceptionConstants;
import com.cvsgo.interceptor.AuthInterceptor;
import com.cvsgo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private AuthInterceptor authInterceptor;

    @MockBean
    LoginUserArgumentResolver loginUserArgumentResolver;

    @MockBean
    WebConfig webConfig;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .apply(sharedHttpSession())
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
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
            .tagIds(getTags().stream().map(Tag::getId).toList())
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
            .tagIds(getTags().stream().map(Tag::getId).toList())
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
            .tagIds(getTags().stream().map(Tag::getId).toList())
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
            .tagIds(getTags().stream().map(Tag::getId).toList())
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
        SignUpRequestDto signUpRequest = createRequest();

        given(userService.signUp(any())).willReturn(createResponse());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                    fieldWithPath("tagIds").type(JsonFieldType.ARRAY).description("태그 ID 목록")
                ),
                responseFields(
                    fieldWithPath("timestamp").type(JsonFieldType.STRING).description("요청 시각"),
                    fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                    fieldWithPath("data.role").type(JsonFieldType.STRING).description("회원 등급"),
                    fieldWithPath("data.tagIds").type(JsonFieldType.ARRAY).description("태그 ID 목록")
                )
            ));
    }

    @Test
    @DisplayName("해당 닉네임을 가진 계정이 존재하면 회원가입 API 호출시 HTTP 409를 응답한다")
    void respond_409_when_nickname_conflicts() throws Exception {
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
            .email("abc@naver.com")
            .password("111111111a!")
            .nickname("닉네임")
            .tagIds(getTags().stream().map(Tag::getId).toList())
            .build();

        given(userService.signUp(any())).willThrow(DUPLICATE_NICKNAME);

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
            .tagIds(getTags().stream().map(Tag::getId).toList())
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
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("email").description("이메일")
                ),
                responseFields(
                    fieldWithPath("timestamp").type(JsonFieldType.STRING).description("요청 시각"),
                    fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("해당 이메일로 가입된 계정 존재 여부")
                )
            ));
    }

    @Test
    @DisplayName("해당 닉네임으로 가입된 계정이 없으면 닉네임 중복 검사에서 false를 응답한다")
    void respond_false_when_nickname_exists() throws Exception {
        final String nickname = "닉네임";
        given(userService.isDuplicatedNickname(nickname)).willReturn(false);

        mockMvc.perform(get("/api/users/nicknames/{nickname}/exists", nickname).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("false")))
            .andDo(print());
    }

    @Test
    @DisplayName("해당 닉네임으로 가입된 계정이 존재하는 경우 닉네임 중복 검사에서 true를 응답한다")
    void respond_true_when_nickname_exists() throws Exception {
        final String nickname = "닉네임";
        given(userService.isDuplicatedNickname(nickname)).willReturn(true);

        mockMvc.perform(get("/api/users/nicknames/{nickname}/exists", nickname).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("true")))
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("nickname").description("닉네임")
                ),
                responseFields(
                    fieldWithPath("timestamp").type(JsonFieldType.STRING).description("요청 시각"),
                    fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("해당 닉네임으로 가입된 계정 존재 여부")
                )
            ));
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

    @Test
    @DisplayName("회원 정보 수정에 성공하면 200을 응답한다")
    void respond_200_when_update_user_succeed() throws Exception {
        UpdateUserRequestDto request = new UpdateUserRequestDto("수정닉네임", List.of(1L, 3L));

        mockMvc.perform(put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                    fieldWithPath("tagIds").type(JsonFieldType.ARRAY).description("태그 ID 목록")
                )
            ));
    }

    @Test
    @DisplayName("해당 닉네임을 가진 계정이 존재하면 회원 수정 API 호출시 HTTP 409를 응답한다")
    void respond_409_when_update_user_but_nickname_conflicts() throws Exception {
        UpdateUserRequestDto request = new UpdateUserRequestDto("중복", List.of(1L, 3L));
        willThrow(DUPLICATE_NICKNAME).given(userService).updateUser(any(), any());

        mockMvc.perform(put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 팔로우 생성에 성공하면 HTTP 201을 응답한다")
    void respond_201_when_create_user_follow_succeed() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/users/{userId}/followers", 2L)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("userId").description("팔로우할 회원 ID")
                )
            ));
    }

    @Test
    @DisplayName("회원 팔로우 생성 시 본인을 팔로우하는 경우 HTTP 400을 응답한다")
    void respond_400_when_create_user_follow_but_invalid_user_follow() throws Exception {
        willThrow(BAD_REQUEST_USER_FOLLOW).given(userService).createUserFollow(any(), anyLong());

        mockMvc.perform(post("/api/users/{userId}/followers", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 팔로우 생성 시 해당하는 아이디를 가진 사용자가 없는 경우 HTTP 404을 응답한다")
    void respond_404_when_create_user_follow_but_user_does_not_exist() throws Exception {
        willThrow(NOT_FOUND_USER).given(userService).createUserFollow(any(), anyLong());

        mockMvc.perform(post("/api/users/{userId}/followers", 10000L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 팔로우 생성 시 이미 해당하는 회원 팔로우가 존재하는 경우 HTTP 409을 응답한다")
    void respond_409_when_create_user_follow_but_user_follow_duplicated() throws Exception {
        willThrow(DUPLICATE_USER_FOLLOW).given(userService).createUserFollow(any(), anyLong());

        mockMvc.perform(post("/api/users/{userId}/followers", 2L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 팔로우 삭제에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_delete_user_follow_succeed() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}/followers", 2L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("userId").description("언팔로우할 회원 ID")
                )
            ));
    }

    @Test
    @DisplayName("회원 팔로우 삭제 시 해당하는 아이디를 가진 사용자가 없는 경우 HTTP 404을 응답한다")
    void respond_404_when_delete_user_follow_but_user_does_not_exist() throws Exception {
        willThrow(NOT_FOUND_USER).given(userService).deleteUserFollow(any(), anyLong());

        mockMvc.perform(delete("/api/users/{userId}/followers", 10000L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 팔로우 삭제 시 해당하는 회원 팔로우가 없는 경우 HTTP 404을 응답한다")
    void respond_404_when_delete_user_follow_but_user_follow_does_not_exist() throws Exception {
        willThrow(NOT_FOUND_USER_FOLLOW).given(userService).deleteUserFollow(any(), anyLong());

        mockMvc.perform(delete("/api/users/{userId}/followers", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    private List<Tag> getTags() {
        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.builder().id(2L).name("맵부심").group(1).build());
        tags.add(Tag.builder().id(3L).name("초코러버").group(2).build());
        tags.add(Tag.builder().id(7L).name("소식가").group(5).build());
        return tags;
    }

    private SignUpRequestDto createRequest() {
        return SignUpRequestDto.builder()
            .email("abc@naver.com")
            .password("111111111a!")
            .nickname("닉네임")
            .tagIds(getTags().stream().map(Tag::getId).toList())
            .build();
    }

    private User createUser() {
        return User.create("abc@naver.com", "password1!", "닉네임", getTags());
    }

    private SignUpResponseDto createResponse() {
        return SignUpResponseDto.from(createUser());
    }

}
