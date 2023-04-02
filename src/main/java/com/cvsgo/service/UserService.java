package com.cvsgo.service;

import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.dto.user.SignUpResponseDto;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.repository.TagRepository;
import com.cvsgo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import com.cvsgo.exception.user.DuplicateEmailException;
import com.cvsgo.exception.user.DuplicateNicknameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_EMAIL;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_NICKNAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    private final PasswordEncoder passwordEncoder;

    private final EntityManager entityManager;

    /**
     * 사용자를 등록한다.
     *
     * @param request 등록할 사용자의 정보
     * @return 등록된 사용자 정보
     * @throws DuplicateEmailException    이메일이 중복된 경우
     * @throws DuplicateNicknameException 닉네임이 중복된 경우
     */
    @Transactional
    public SignUpResponseDto signUp(SignUpRequestDto request) {
        List<Tag> tags = tagRepository.findAllById(request.getTagIds());
        try {
            User user = userRepository.save(request.toEntity(passwordEncoder, tags));
            return SignUpResponseDto.from(user);
        } catch (DataIntegrityViolationException e) {
            entityManager.clear();
            if (isDuplicatedEmail(request.getEmail())) {
                log.info("중복된 이메일: '{}'", request.getEmail());
                throw DUPLICATE_EMAIL;
            }
            if (isDuplicatedNickname(request.getNickname())) {
                log.info("중복된 닉네임: '{}'", request.getNickname());
                throw DUPLICATE_NICKNAME;
            }
            throw e;
        }

    }

    /**
     * 이메일 중복 체크를 한다.
     *
     * @param email 이메일
     * @return 해당 이메일로 등록된 사용자 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean isDuplicatedEmail(String email) {
        return userRepository.findByUserId(email).isPresent();
    }

    /**
     * 닉네임 중복 체크를 한다.
     *
     * @param nickname 닉네임
     * @return 해당 닉네임을 가진 사용자 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean isDuplicatedNickname(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }
}
