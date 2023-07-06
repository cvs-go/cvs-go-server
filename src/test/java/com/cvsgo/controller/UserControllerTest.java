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
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
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
import com.cvsgo.dto.product.ConvenienceStoreEventDto;
import com.cvsgo.dto.product.ProductSortBy;
import com.cvsgo.dto.product.ReadUserProductRequestDto;
import com.cvsgo.dto.product.ReadProductQueryDto;
import com.cvsgo.dto.product.ReadProductResponseDto;
import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.dto.user.SignUpResponseDto;
import com.cvsgo.dto.user.UpdateUserRequestDto;
import com.cvsgo.entity.BogoEvent;
import com.cvsgo.entity.BtgoEvent;
import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.DiscountEvent;
import com.cvsgo.entity.GiftEvent;
import com.cvsgo.entity.Manufacturer;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.ProductLike;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.exception.ExceptionConstants;
import com.cvsgo.interceptor.AuthInterceptor;
import com.cvsgo.service.ProductService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    @MockBean
    private ProductService productService;

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
        UpdateUserRequestDto request = new UpdateUserRequestDto("수정닉네임", List.of(1L, 3L),
            "프로필 이미지 URL");

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
                    fieldWithPath("tagIds").type(JsonFieldType.ARRAY).description("태그 ID 목록"),
                    fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL")
                )
            ));
    }

    @Test
    @DisplayName("해당 닉네임을 가진 계정이 존재하면 회원 수정 API 호출시 HTTP 409를 응답한다")
    void respond_409_when_update_user_but_nickname_conflicts() throws Exception {
        UpdateUserRequestDto request = new UpdateUserRequestDto("중복", List.of(1L, 3L),
            "프로필 이미지 URL");
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


    @Test
    @DisplayName("특정 회원의 좋아요 상품 목록을 정상적으로 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_liked_product_list_successfully() throws Exception {
        ReadUserProductRequestDto request = new ReadUserProductRequestDto(ProductSortBy.SCORE);

        Page<ReadProductResponseDto> responseDto = new PageImpl<>(getProductsResponse());
        given(productService.readLikedProductList(any(), any(), any())).willReturn(responseDto);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/users/{userId}/liked-products", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("{class-name}/{method-name}",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("userId").description("좋아요 상품 목록을 조회할 회원 ID")
                ),
                requestFields(
                    fieldWithPath("sortBy").type(JsonFieldType.STRING).description("정렬 기준").optional()
                ),
                relaxedResponseFields(
                    fieldWithPath("data.content[].productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data.content[].productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data.content[].productPrice").type(JsonFieldType.NUMBER).description("상품 가격"),
                    fieldWithPath("data.content[].productImageUrl").type(JsonFieldType.STRING).description("상품 이미지 url"),
                    fieldWithPath("data.content[].categoryId").type(JsonFieldType.NUMBER).description("상품 카테고리 ID"),
                    fieldWithPath("data.content[].manufacturerName").type(JsonFieldType.STRING).description("제조사"),
                    fieldWithPath("data.content[].isLiked").type(JsonFieldType.BOOLEAN).description("사용자의 상품 좋아요 여부"),
                    fieldWithPath("data.content[].isBookmarked").type(JsonFieldType.BOOLEAN).description("사용자의 상품 북마크 여부"),
                    fieldWithPath("data.content[].reviewCount").type(JsonFieldType.NUMBER).description("상품 리뷰 개수"),
                    fieldWithPath("data.content[].reviewRating").type(JsonFieldType.STRING).description("상품 리뷰 평점"),
                    fieldWithPath("data.content[].convenienceStoreEvents[].name").type(JsonFieldType.STRING).description("판매 편의점 이름"),
                    fieldWithPath("data.content[].convenienceStoreEvents[].eventType").type(JsonFieldType.STRING).description("행사 정보").optional(),
                    fieldWithPath("data.content[].convenienceStoreEvents[].discountAmount").type(JsonFieldType.NUMBER).description("할인 가격").optional()
                )
            ));
    }

    @Test
    @DisplayName("특정 회원의 북마크 상품 목록을 정상적으로 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_bookmarked_product_list_successfully() throws Exception {
        ReadUserProductRequestDto request = new ReadUserProductRequestDto(ProductSortBy.SCORE);

        Page<ReadProductResponseDto> responseDto = new PageImpl<>(getProductsResponse());
        given(productService.readBookmarkedProductList(any(), any(), any())).willReturn(responseDto);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/users/{userId}/bookmarked-products", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("{class-name}/{method-name}",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("userId").description("좋아요 상품 목록을 조회할 회원 ID")
                ),
                requestFields(
                    fieldWithPath("sortBy").type(JsonFieldType.STRING).description("정렬 기준").optional()
                ),
                relaxedResponseFields(
                    fieldWithPath("data.content[].productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data.content[].productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data.content[].productPrice").type(JsonFieldType.NUMBER).description("상품 가격"),
                    fieldWithPath("data.content[].productImageUrl").type(JsonFieldType.STRING).description("상품 이미지 url"),
                    fieldWithPath("data.content[].categoryId").type(JsonFieldType.NUMBER).description("상품 카테고리 ID"),
                    fieldWithPath("data.content[].manufacturerName").type(JsonFieldType.STRING).description("제조사"),
                    fieldWithPath("data.content[].isLiked").type(JsonFieldType.BOOLEAN).description("사용자의 상품 좋아요 여부"),
                    fieldWithPath("data.content[].isBookmarked").type(JsonFieldType.BOOLEAN).description("사용자의 상품 북마크 여부"),
                    fieldWithPath("data.content[].reviewCount").type(JsonFieldType.NUMBER).description("상품 리뷰 개수"),
                    fieldWithPath("data.content[].reviewRating").type(JsonFieldType.STRING).description("상품 리뷰 평점"),
                    fieldWithPath("data.content[].convenienceStoreEvents[].name").type(JsonFieldType.STRING).description("판매 편의점 이름"),
                    fieldWithPath("data.content[].convenienceStoreEvents[].eventType").type(JsonFieldType.STRING).description("행사 정보").optional(),
                    fieldWithPath("data.content[].convenienceStoreEvents[].discountAmount").type(JsonFieldType.NUMBER).description("할인 가격").optional()
                )
            ));
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

    Category category1 = Category.builder()
        .id(1L)
        .name("아이스크림")
        .build();

    Category category2 = Category.builder()
        .id(2L)
        .name("과자&빵")
        .build();

    Manufacturer manufacturer1 = Manufacturer.builder()
        .name("롯데")
        .build();

    Manufacturer manufacturer2 = Manufacturer.builder()
        .name("농심")
        .build();

    Product product1 = Product.builder()
        .id(1L)
        .name("아이시스 500ml")
        .price(2000)
        .category(category1)
        .imageUrl("")
        .manufacturer(manufacturer1)
        .build();

    Product product2 = Product.builder()
        .id(2L)
        .name("백산수 500ml")
        .price(1500)
        .category(category2)
        .imageUrl("")
        .manufacturer(manufacturer2)
        .build();

    User user = User.builder().build();

    ProductLike productLike = ProductLike.builder()
        .user(user)
        .product(product1)
        .build();

    ProductBookmark productBookmark = ProductBookmark.builder()
        .user(user)
        .product(product1)
        .build();

    ConvenienceStore cvs1 = ConvenienceStore.builder()
        .id(1L)
        .name("CU")
        .build();

    ConvenienceStore cvs2 = ConvenienceStore.builder()
        .id(2L)
        .name("GS25")
        .build();

    BogoEvent bogoEvent = BogoEvent.builder()
        .product(product1)
        .convenienceStore(cvs1)
        .build();

    BtgoEvent btgoEvent = BtgoEvent.builder()
        .product(product1)
        .convenienceStore(cvs2)
        .build();

    GiftEvent giftEvent = GiftEvent.builder()
        .product(product2)
        .convenienceStore(cvs1)
        .giftProduct(product1)
        .build();

    DiscountEvent discountEvent = DiscountEvent.builder()
        .product(product2)
        .convenienceStore(cvs2)
        .discountAmount(300)
        .build();

    private List<ReadProductResponseDto> getProductsResponse() {
        ReadProductQueryDto productQueryDto1 = new ReadProductQueryDto(product1.getId(),
            product1.getName(), product1.getPrice(), product1.getImageUrl(),
            product1.getCategory().getId(), product1.getManufacturer().getName(), productLike, productBookmark, 5L, 3.5, 4.5);
        List<ConvenienceStoreEventDto> convenienceStoreEvents1 = List.of(
            ConvenienceStoreEventDto.of(cvs1.getName(), bogoEvent),
            ConvenienceStoreEventDto.of(cvs2.getName(), btgoEvent));

        ReadProductQueryDto productQueryDto2 = new ReadProductQueryDto(product2.getId(),
            product2.getName(), product2.getPrice(), product2.getImageUrl(),
            product2.getCategory().getId(), product2.getManufacturer().getName(), null, null, 5L, 4.0, 4.0);
        List<ConvenienceStoreEventDto> convenienceStoreEvents2 = List.of(
            ConvenienceStoreEventDto.of(cvs1.getName(), giftEvent),
            ConvenienceStoreEventDto.of(cvs2.getName(), discountEvent));

        ReadProductResponseDto productResponse1 = ReadProductResponseDto.of(productQueryDto1, convenienceStoreEvents1);
        ReadProductResponseDto productResponse2 = ReadProductResponseDto.of(productQueryDto2, convenienceStoreEvents2);
        return List.of(productResponse1, productResponse2);
    }

}
