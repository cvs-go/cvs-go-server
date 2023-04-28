package com.cvsgo.controller;

import static com.cvsgo.ApiDocumentUtils.documentIdentifier;
import static com.cvsgo.ApiDocumentUtils.getDocumentRequest;
import static com.cvsgo.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.cvsgo.argumentresolver.LoginUserArgumentResolver;
import com.cvsgo.config.WebConfig;
import com.cvsgo.dto.product.CategoryDto;
import com.cvsgo.dto.product.ConvenienceStoreDto;
import com.cvsgo.dto.product.ConvenienceStoreEventDto;
import com.cvsgo.dto.product.EventTypeDto;
import com.cvsgo.dto.product.ProductSortBy;
import com.cvsgo.dto.product.ReadProductDetailQueryDto;
import com.cvsgo.dto.product.ReadProductDetailResponseDto;
import com.cvsgo.dto.product.ReadProductFilterResponseDto;
import com.cvsgo.dto.product.ReadProductQueryDto;
import com.cvsgo.dto.product.ReadProductRequestDto;
import com.cvsgo.dto.product.ReadProductResponseDto;
import com.cvsgo.entity.BogoEvent;
import com.cvsgo.entity.BtgoEvent;
import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.DiscountEvent;
import com.cvsgo.entity.EventType;
import com.cvsgo.entity.GiftEvent;
import com.cvsgo.entity.Manufacturer;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.ProductLike;
import com.cvsgo.entity.User;
import com.cvsgo.exception.ExceptionConstants;
import com.cvsgo.interceptor.AuthInterceptor;
import com.cvsgo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @MockBean
    LoginUserArgumentResolver loginUserArgumentResolver;

    @MockBean
    WebConfig webConfig;

    @MockBean
    AuthInterceptor authInterceptor;

    @MockBean
    private ProductService productService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .apply(sharedHttpSession())
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .build();
    }

    @Test
    @DisplayName("상품 목록을 정상적으로 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_product_list_successfully() throws Exception {
        ReadProductRequestDto request = new ReadProductRequestDto(ProductSortBy.SCORE,
            List.of(1L), List.of(1L), List.of(EventType.BOGO), 0, 10000, "500");

        Page<ReadProductResponseDto> responseDto = new PageImpl<>(getProductsResponse());
        given(productService.readProductList(any(), any(), any())).willReturn(responseDto);

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("{class-name}/{method-name}",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("sortBy").type(JsonFieldType.STRING).description("정렬 기준").optional(),
                    fieldWithPath("convenienceStoreIds").type(JsonFieldType.ARRAY).description("편의점 ID 리스트"),
                    fieldWithPath("categoryIds").type(JsonFieldType.ARRAY).description("제품 카테고리 ID 리스트"),
                    fieldWithPath("eventTypes").type(JsonFieldType.ARRAY).description("이벤트타입 리스트"),
                    fieldWithPath("lowestPrice").type(JsonFieldType.NUMBER).description("최저 가격"),
                    fieldWithPath("highestPrice").type(JsonFieldType.NUMBER).description("최고 가격"),
                    fieldWithPath("keyword").type(JsonFieldType.STRING).description("검색어")
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
    @DisplayName("상품을 정상적으로 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_product_successfully() throws Exception {
        given(productService.readProduct(any(), any())).willReturn(getProductResponse());

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/products/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data.productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data.productPrice").type(JsonFieldType.NUMBER).description("상품 가격"),
                    fieldWithPath("data.productImageUrl").type(JsonFieldType.STRING).description("상품 이미지 url"),
                    fieldWithPath("data.manufacturerName").type(JsonFieldType.STRING).description("제조사"),
                    fieldWithPath("data.isLiked").type(JsonFieldType.BOOLEAN).description("사용자의 상품 좋아요 여부"),
                    fieldWithPath("data.isBookmarked").type(JsonFieldType.BOOLEAN).description("사용자의 상품 북마크 여부"),
                    fieldWithPath("data.convenienceStoreEvents[].name").type(JsonFieldType.STRING).description("판매 편의점 이름"),
                    fieldWithPath("data.convenienceStoreEvents[].eventType").type(JsonFieldType.STRING).description("행사 정보").optional(),
                    fieldWithPath("data.convenienceStoreEvents[].discountAmount").type(JsonFieldType.NUMBER).description("할인 가격").optional()
                )
            ));
    }

    @Test
    @DisplayName("상품 조회 시 해당 ID를 가진 상품이 존재하지 않으면 상품 상세 조회 API 호출시 HTTP 404를 응답한다")
    void respond_404_when_read_product_but_product_does_not_exist() throws Exception {
        given(productService.readProduct(any(), any())).willThrow(ExceptionConstants.NOT_FOUND_PRODUCT);

        mockMvc.perform(get("/api/products/{productId}", 1000L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
    @DisplayName("상품 좋아요 생성에 성공하면 HTTP 201을 응답한다")
    void respond_201_when_create_product_like_succeed() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/products/{productId}/likes", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                )
            ));
    }

    @Test
    @DisplayName("상품 좋아요 삭제에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_delete_product_like_succeed() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/products/{productId}/likes", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                )
            ));
    }

    @Test
    @DisplayName("상품 북마크 생성에 성공하면 HTTP 201을 응답한다")
    void respond_201_when_create_product_bookmark_succeed() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/products/{productId}/bookmarks", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                )
            ));
    }

    @Test
    @DisplayName("상품 북마크 삭제에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_delete_product_bookmark_succeed() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/products/{productId}/bookmarks", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                )
            ));
    }

    @Test
    @DisplayName("상품 필터를 정상적으로 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_product_filter_successfully() throws Exception {
        given(productService.readProductFilter()).willReturn(getProductFilterResponse());

        mockMvc.perform(get("/api/products/filter").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                relaxedResponseFields(
                    fieldWithPath("data.convenienceStores[].id").type(JsonFieldType.NUMBER).description("편의점 ID"),
                    fieldWithPath("data.convenienceStores[].name").type(JsonFieldType.STRING).description("편의점 이름"),
                    fieldWithPath("data.categories[].id").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                    fieldWithPath("data.categories[].name").type(JsonFieldType.STRING).description("카테고리 이름"),
                    fieldWithPath("data.eventTypes[].value").type(JsonFieldType.STRING).description("이벤트 타입"),
                    fieldWithPath("data.eventTypes[].name").type(JsonFieldType.STRING).description("이벤트 타입 이름"),
                    fieldWithPath("data.highestPrice").type(JsonFieldType.NUMBER).description("최고 가격")
                )
            ));
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

    ConvenienceStore cvs3 = ConvenienceStore.builder()
        .id(3L)
        .name("세븐일레븐")
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

    private ReadProductDetailResponseDto getProductResponse() {
        ReadProductDetailQueryDto productDetailQueryDto = new ReadProductDetailQueryDto(
            product2.getId(), product2.getName(), product2.getPrice(), product2.getImageUrl(),
            product2.getManufacturer().getName(), null, null);
        List<ConvenienceStoreEventDto> convenienceStoreEvents = List.of(
            ConvenienceStoreEventDto.of(cvs1.getName(), giftEvent),
            ConvenienceStoreEventDto.of(cvs2.getName(), discountEvent));

        return ReadProductDetailResponseDto.of(productDetailQueryDto, convenienceStoreEvents);
    }

    private ReadProductFilterResponseDto getProductFilterResponse() {
        List<ConvenienceStoreDto> convenienceStores = List.of(
            ConvenienceStoreDto.from(cvs1), ConvenienceStoreDto.from(cvs2),
            ConvenienceStoreDto.from(cvs3));
        List<CategoryDto> categories = List.of(CategoryDto.from(category1),
            CategoryDto.from(category2));
        List<EventTypeDto> eventTypes = List.of(com.cvsgo.dto.product.EventTypeDto.from(EventType.BOGO),
            EventTypeDto.from(EventType.BTGO), com.cvsgo.dto.product.EventTypeDto.from(EventType.GIFT));
        Integer highestPrice = 10000;
        return ReadProductFilterResponseDto.of(convenienceStores, categories, eventTypes, highestPrice);
    }
}
