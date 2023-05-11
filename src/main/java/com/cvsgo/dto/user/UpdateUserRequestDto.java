package com.cvsgo.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserRequestDto {

    @NotNull(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하여야 합니다.")
    private String nickname;

    private List<Long> tagIds;

    public UpdateUserRequestDto(String nickname, List<Long> tagIds) {
        this.nickname = nickname;
        this.tagIds = tagIds;
    }
}
