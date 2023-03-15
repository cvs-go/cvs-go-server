package com.cvsgo.controller;

import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.dto.user.SignUpResponseDto;
import com.cvsgo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<SignUpResponseDto> register(@RequestBody @Valid SignUpRequestDto request) {
        return SuccessResponse.from(userService.signUp(request));
    }

    @GetMapping("/emails/{email}/exists")
    public SuccessResponse<Boolean> checkEmailExists(@PathVariable String email) {
        return SuccessResponse.from(userService.isDuplicatedEmail(email));
    }

    @GetMapping("/nicknames/{nickname}/exists")
    public SuccessResponse<Boolean> checkNicknameExists(@PathVariable String nickname) {
        return SuccessResponse.from(userService.isDuplicatedNickname(nickname));
    }

}
