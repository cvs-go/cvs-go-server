package com.cvsgo.controller;

import com.cvsgo.argumentresolver.LoginUser;
import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.dto.product.ReadProductResponseDto;
import com.cvsgo.dto.product.ReadUserProductRequestDto;
import com.cvsgo.dto.review.ReadUserReviewResponseDto;
import com.cvsgo.dto.review.ReviewSortBy;
import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.dto.user.SignUpResponseDto;
import com.cvsgo.dto.user.UpdateUserRequestDto;
import com.cvsgo.dto.user.UserResponseDto;
import com.cvsgo.entity.User;
import com.cvsgo.service.ProductService;
import com.cvsgo.service.ReviewService;
import com.cvsgo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private final ProductService productService;

    private final ReviewService reviewService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<SignUpResponseDto> register(
        @RequestBody @Valid SignUpRequestDto request) {
        return SuccessResponse.from(userService.signUp(request));
    }

    @GetMapping("/users/emails/{email}/exists")
    public SuccessResponse<Boolean> checkEmailExists(@PathVariable String email) {
        return SuccessResponse.from(userService.isDuplicatedEmail(email));
    }

    @GetMapping("/users/nicknames/{nickname}/exists")
    public SuccessResponse<Boolean> checkNicknameExists(@PathVariable String nickname) {
        return SuccessResponse.from(userService.isDuplicatedNickname(nickname));
    }

    @GetMapping("/users/{userId}")
    public SuccessResponse<UserResponseDto> readUser(@PathVariable Long userId) {
        return SuccessResponse.from(userService.readUser(userId));
    }

    @PutMapping("/user")
    public SuccessResponse<Void> updateUser(@LoginUser User user,
        @RequestBody @Valid UpdateUserRequestDto request) {
        userService.updateUser(user, request);
        return SuccessResponse.create();
    }

    @PostMapping("/users/{userId}/followers")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> createUserFollow(@LoginUser User user, @PathVariable Long userId) {
        userService.createUserFollow(user, userId);
        return SuccessResponse.create();
    }

    @DeleteMapping("/users/{userId}/followers")
    public SuccessResponse<Void> deleteUserFollow(@LoginUser User user, @PathVariable Long userId) {
        userService.deleteUserFollow(user, userId);
        return SuccessResponse.create();
    }

    @GetMapping("/users/{userId}/tag-match-percentage")
    public SuccessResponse<Integer> readUserTagMatchPercentage(
        @LoginUser User user, @PathVariable Long userId) {
        return SuccessResponse.from(userService.readUserTagMatchPercentage(user, userId));
    }

    @GetMapping("/users/{userId}/liked-products")
    public SuccessResponse<Page<ReadProductResponseDto>> readLikedProductList(
        @PathVariable Long userId, @ModelAttribute ReadUserProductRequestDto request,
        Pageable pageable) {
        return SuccessResponse.from(productService.readLikedProductList(userId, request, pageable));
    }

    @GetMapping("/users/{userId}/bookmarked-products")
    public SuccessResponse<Page<ReadProductResponseDto>> readBookmarkedProductList(
        @PathVariable Long userId, @ModelAttribute ReadUserProductRequestDto request,
        Pageable pageable) {
        return SuccessResponse.from(
            productService.readBookmarkedProductList(userId, request, pageable));
    }

    @GetMapping("/users/{userId}/reviews")
    public SuccessResponse<Page<ReadUserReviewResponseDto>> readUserReviewList(@LoginUser User loginUser,
        @PathVariable Long userId, ReviewSortBy sortBy, Pageable pageable) {
        return SuccessResponse.from(
            reviewService.readUserReviewList(loginUser, userId, sortBy, pageable));
    }

}
