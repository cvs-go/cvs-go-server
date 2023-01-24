package com.cvsgo.service;

import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.dto.user.SignUpResponseDto;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserTag;
import com.cvsgo.repository.TagRepository;
import com.cvsgo.repository.UserRepository;
import com.cvsgo.repository.UserTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_EMAIL;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_NICKNAME;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    private final UserTagRepository userTagRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자를 등록한다.
     *
     * @param signUpRequestDto 등록할 사용자의 정보
     * @return 등록된 사용자 정보
     * @throws com.cvsgo.exception.user.DuplicateEmailException 이메일이 중복된 경우
     * @throws com.cvsgo.exception.user.DuplicateNicknameException 닉네임이 중복된 경우
     */
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        if (isDuplicatedEmail(signUpRequestDto.getEmail())) {
            throw DUPLICATE_EMAIL;
        }
        if (isDuplicatedNickname(signUpRequestDto.getNickname())) {
            throw DUPLICATE_NICKNAME;
        }
        User user = userRepository.save(signUpRequestDto.toEntity(passwordEncoder));
        List<Tag> tags = tagRepository.findAllById(signUpRequestDto.getTagIds());
        userTagRepository.saveAll(tags.stream().map(
                tag -> UserTag.builder()
                        .user(user)
                        .tag(tag)
                        .build())
                .toList());
        return new SignUpResponseDto(user, signUpRequestDto.getTagIds());
    }

    /**
     * 이메일 중복 체크를 한다.
     * @param email 이메일
     * @return 해당 이메일로 등록된 사용자 존재 여부
     */
    public Boolean isDuplicatedEmail(String email) {
        return userRepository.findByUserId(email).isPresent();
    }

    /**
     * 닉네임 중복 체크를 한다.
     * @param nickname 닉네임
     * @return 해당 닉네임을 가진 사용자 존재 여부
     */
    public Boolean isDuplicatedNickname(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }
}
