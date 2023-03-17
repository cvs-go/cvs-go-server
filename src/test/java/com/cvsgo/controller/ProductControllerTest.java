package com.cvsgo.controller;

import static com.cvsgo.ApiDocumentUtils.getDocumentRequest;
import static com.cvsgo.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.cvsgo.argumentresolver.LoginUserArgumentResolver;
import com.cvsgo.config.WebConfig;
import com.cvsgo.dto.product.ProductResponseDto;
import com.cvsgo.dto.product.ProductSearchRequestDto;
import com.cvsgo.dto.product.SellAtResponseDto;
import com.cvsgo.entity.BogoEvent;
import com.cvsgo.entity.BtgoEvent;
import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.DiscountEvent;
import com.cvsgo.entity.EventType.Values;
import com.cvsgo.entity.GiftEvent;
import com.cvsgo.entity.Manufacturer;
import com.cvsgo.entity.Product;
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
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
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
            .eventTypes(List.of("BOGO"))
            .prices(List.of(0, 1000))
            .build();

        Page<ProductResponseDto> responseDto = new PageImpl<>(createProductsResponse());
        given(productService.getProductList(any(), any(), any())).willReturn(responseDto);

        mockMvc.perform(post("/api/products")
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
                    fieldWithPath("prices").type(JsonFieldType.ARRAY).description("가격 리스트")
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
                    fieldWithPath("data.content[].sellAt[].name").type(JsonFieldType.STRING).description("판매 편의점"),
                    fieldWithPath("data.content[].sellAt[].event").type(JsonFieldType.STRING).description("행사 정보").optional()
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

    ConvenienceStore cvs1 = ConvenienceStore.builder()
        .name("CU")
        .build();

    ConvenienceStore cvs2 = ConvenienceStore.builder()
        .name("GS25")
        .build();

    ConvenienceStore cvs3 = ConvenienceStore.builder()
        .name("세븐일레븐")
        .build();

    BogoEvent bogoEvent = BogoEvent.builder()
        .product(product1)
        .convenienceStore(cvs1)
        .eventType(Values.BOGO)
        .build();

    BtgoEvent btgoEvent = BtgoEvent.builder()
        .product(product1)
        .convenienceStore(cvs2)
        .eventType(Values.BTGO)
        .build();

    GiftEvent giftEvent = GiftEvent.builder()
        .product(product2)
        .convenienceStore(cvs1)
        .eventType(Values.GIFT)
        .giftProduct(product1)
        .build();

    DiscountEvent discountEvent = DiscountEvent.builder()
        .product(product2)
        .convenienceStore(cvs2)
        .eventType(Values.DISCOUNT)
        .discountAmount(300)
        .build();

    private List<ProductResponseDto> createProductsResponse() {
        ProductResponseDto productResponse1 = ProductResponseDto.of(product1, 1, null, 15L, 2.5);
        productResponse1.setSellAt(List.of(SellAtResponseDto.of(cvs1, bogoEvent), SellAtResponseDto.of(cvs2, btgoEvent), SellAtResponseDto.of(cvs3, null)));
        ProductResponseDto productResponse2 = ProductResponseDto.of(product2, null, 1, 1L, 5.0);
        productResponse2.setSellAt(List.of(SellAtResponseDto.of(cvs1, giftEvent), SellAtResponseDto.of(cvs2, discountEvent)));
        return List.of(productResponse1, productResponse2);
    }

}
