package com.cvsgo.controller;

import com.cvsgo.argumentresolver.LoginUser;
import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.dto.product.ReadProductLikeTagResponseDto;
import com.cvsgo.dto.product.ReadProductDetailResponseDto;
import com.cvsgo.dto.product.ReadProductFilterResponseDto;
import com.cvsgo.dto.product.ReadProductRequestDto;
import com.cvsgo.dto.product.ReadProductResponseDto;
import com.cvsgo.entity.User;
import com.cvsgo.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public SuccessResponse<Page<ReadProductResponseDto>> readProductList(@LoginUser User user,
        @ModelAttribute ReadProductRequestDto request, Pageable pageable) {
        return SuccessResponse.from(productService.readProductList(user, request, pageable));
    }

    @GetMapping("/{productId}")
    public SuccessResponse<ReadProductDetailResponseDto> readProduct(@LoginUser User user,
        @PathVariable Long productId) {
        return SuccessResponse.from(productService.readProduct(user, productId));
    }

    @GetMapping("/{productId}/tags")
    public SuccessResponse<List<ReadProductLikeTagResponseDto>> readProductLikeTags(
        @PathVariable Long productId) {
        return SuccessResponse.from(productService.readProductLikeTags(productId));
    }

    @PostMapping("/{productId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> createProductLike(@LoginUser User user,
        @PathVariable Long productId) {
        productService.createProductLike(user, productId);
        return SuccessResponse.create();
    }

    @DeleteMapping("/{productId}/likes")
    public SuccessResponse<Void> deleteProductLike(@LoginUser User user,
        @PathVariable Long productId) {
        productService.deleteProductLike(user, productId);
        return SuccessResponse.create();
    }

    @PostMapping("/{productId}/bookmarks")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> createProductBookmark(@LoginUser User user,
        @PathVariable Long productId) {
        productService.createProductBookmark(user, productId);
        return SuccessResponse.create();
    }

    @DeleteMapping("/{productId}/bookmarks")
    public SuccessResponse<Void> deleteProductBookmark(@LoginUser User user,
        @PathVariable Long productId) {
        productService.deleteProductBookmark(user, productId);
        return SuccessResponse.create();
    }

    @GetMapping("/filter")
    public SuccessResponse<ReadProductFilterResponseDto> readProductFilter() {
        return SuccessResponse.from(productService.readProductFilter());
    }

}
