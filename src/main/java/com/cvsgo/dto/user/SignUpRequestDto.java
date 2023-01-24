package com.cvsgo.dto.user;

import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {

    @NotNull(message = "이메일은 필수입니다.")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;

    @NotNull(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{10,20}", message = "비밀번호는 영문, 숫자, 특수문자 포함 10자 이상 20자 이하여야 합니다.")
    private String password;

    @NotNull(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하여야 합니다.")
    private String nickname;

    private List<Long> tagIds;

    @Builder
    public SignUpRequestDto(String email, String password, String nickname, List<Long> tagIds) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.tagIds = tagIds;
    }

    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .userId(email)
                .role(Role.ASSOCIATE)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();
    }

}
