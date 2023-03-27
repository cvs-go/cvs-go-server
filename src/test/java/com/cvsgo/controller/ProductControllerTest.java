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
import com.cvsgo.dto.product.CategoryResponseDto;
import com.cvsgo.dto.product.ConvenienceStoreResponseDto;
import com.cvsgo.dto.product.EventTypeResponseDto;
import com.cvsgo.dto.product.ProductDetailResponseDto;
import com.cvsgo.dto.product.ProductFilterResponseDto;
import com.cvsgo.dto.product.ProductResponseDto;
import com.cvsgo.dto.product.ProductSearchRequestDto;
import com.cvsgo.dto.product.SellAtEventResponseDto;
import com.cvsgo.dto.product.SellAtResponseDto;
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
        ProductSearchRequestDto request = ProductSearchRequestDto.builder()
            .convenienceStoreIds(List.of(1L))
            .categoryIds(List.of(1L))
            .eventTypes(List.of(EventType.BOGO))
            .lowestPrice(0)
            .highestPrice(1000)
            .build();

        List<ProductResponseDto> responseDto = createProductsResponse();
        given(productService.getProductList(any(), any(), any())).willReturn(responseDto);

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("{class-name}/{method-name}",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("convenienceStoreIds").type(JsonFieldType.ARRAY).description("편의점 ID 리스트"),
                    fieldWithPath("categoryIds").type(JsonFieldType.ARRAY).description("제품 카테고리 ID 리스트"),
                    fieldWithPath("eventTypes").type(JsonFieldType.ARRAY).description("이벤트타입 리스트"),
                    fieldWithPath("lowestPrice").type(JsonFieldType.NUMBER).description("최저 가격"),
                    fieldWithPath("highestPrice").type(JsonFieldType.NUMBER).description("최고 가격")
                ),
                relaxedResponseFields(
                    fieldWithPath("data[].productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data[].productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data[].productPrice").type(JsonFieldType.NUMBER).description("상품 가격"),
                    fieldWithPath("data[].productImageUrl").type(JsonFieldType.STRING).description("상품 이미지 url"),
                    fieldWithPath("data[].categoryId").type(JsonFieldType.NUMBER).description("상품 카테고리 ID"),
                    fieldWithPath("data[].manufacturerName").type(JsonFieldType.STRING).description("제조사"),
                    fieldWithPath("data[].isLiked").type(JsonFieldType.BOOLEAN).description("사용자의 상품 좋아요 여부"),
                    fieldWithPath("data[].isBookmarked").type(JsonFieldType.BOOLEAN).description("사용자의 상품 북마크 여부"),
                    fieldWithPath("data[].reviewCount").type(JsonFieldType.NUMBER).description("상품 리뷰 개수"),
                    fieldWithPath("data[].reviewRating").type(JsonFieldType.STRING).description("상품 리뷰 평점"),
                    fieldWithPath("data[].sellAt[].name").type(JsonFieldType.STRING).description("판매 편의점"),
                    fieldWithPath("data[].sellAt[].event").type(JsonFieldType.STRING).description("행사 정보").optional()
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
                    fieldWithPath("data.sellAts[].convenienceStoreId").type(JsonFieldType.NUMBER).description("판매 편의점 ID"),
                    fieldWithPath("data.sellAts[].convenienceStoreName").type(JsonFieldType.STRING).description("판매 편의점 이름"),
                    fieldWithPath("data.sellAts[].eventType").type(JsonFieldType.STRING).description("행사 정보").optional(),
                    fieldWithPath("data.sellAts[].discountAmount").type(JsonFieldType.NUMBER).description("할인 가격").optional()
                )
            ));
    }

    @Test
    @DisplayName("해당 ID를 가진 상품이 존재하지 않으면 상품 상세 조회 API 호출시 HTTP 400를 응답한다")
    void respond_400_when_product_does_not_exist() throws Exception {
        given(productService.readProduct(any(), any())).willThrow(ExceptionConstants.NOT_FOUND_PRODUCT);

        mockMvc.perform(get("/api/products/{productId}", 1000L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
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
    @DisplayName("상품 목록을 정상적으로 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_product_filter_successfully() throws Exception {
        given(productService.getProductFilter()).willReturn(getProductFilterResponse());

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
        .name("월드콘")
        .price(2000)
        .category(category1)
        .imageUrl("")
        .manufacturer(manufacturer1)
        .build();

    Product product2 = Product.builder()
        .id(2L)
        .name("바나나킥")
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

    private List<ProductResponseDto> createProductsResponse() {
        ProductResponseDto productResponse1 = ProductResponseDto.of(product1, false, false, 15L, 2.5,
            List.of(SellAtResponseDto.of(cvs1.getName(), bogoEvent.getEventType()),
                SellAtResponseDto.of(cvs2.getName(), btgoEvent.getEventType()),
                SellAtResponseDto.of(cvs3.getName(), null)));
        ProductResponseDto productResponse2 = ProductResponseDto.of(product2, false, true, 1L, 5.0,
            List.of(SellAtResponseDto.of(cvs1.getName(), giftEvent.getEventType()),
                SellAtResponseDto.of(cvs2.getName(), discountEvent.getEventType())));
        return List.of(productResponse1, productResponse2);
    }

    private ProductDetailResponseDto getProductResponse() {
        ProductDetailResponseDto productDetailResponse = ProductDetailResponseDto.of(product1,
            manufacturer1, productLike, productBookmark);
        productDetailResponse.setSellAts(List.of(SellAtEventResponseDto.of(cvs1, bogoEvent),
            SellAtEventResponseDto.of(cvs2, discountEvent),
            SellAtEventResponseDto.of(cvs3, null)));
        return productDetailResponse;
    }

    private ProductFilterResponseDto getProductFilterResponse() {
        List<ConvenienceStoreResponseDto> convenienceStores = List.of(
            ConvenienceStoreResponseDto.from(cvs1), ConvenienceStoreResponseDto.from(cvs2),
            ConvenienceStoreResponseDto.from(cvs3));
        List<CategoryResponseDto> categories = List.of(CategoryResponseDto.from(category1),
            CategoryResponseDto.from(category2));
        List<EventTypeResponseDto> eventTypes = List.of(EventTypeResponseDto.from(EventType.BOGO),
            EventTypeResponseDto.from(EventType.BTGO), EventTypeResponseDto.from(EventType.GIFT));
        Integer highestPrice = 10000;
        return ProductFilterResponseDto.of(convenienceStores, categories, eventTypes, highestPrice);
    }
}
