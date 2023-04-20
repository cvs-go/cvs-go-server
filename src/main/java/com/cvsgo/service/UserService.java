package com.cvsgo.service;

import static com.cvsgo.exception.ExceptionConstants.BAD_REQUEST_USER_FOLLOW;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_EMAIL;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_NICKNAME;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_USER_FOLLOW;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_USER;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_USER_FOLLOW;

import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.dto.user.SignUpResponseDto;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserFollow;
import com.cvsgo.exception.BadRequestException;
import com.cvsgo.exception.DuplicateException;
import com.cvsgo.exception.NotFoundException;
import com.cvsgo.repository.TagRepository;
import com.cvsgo.repository.UserFollowRepository;
import com.cvsgo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    private final UserFollowRepository userFollowRepository;

    private final PasswordEncoder passwordEncoder;

    private final EntityManager entityManager;

    /**
     * 사용자를 등록한다.
     *
     * @param request 등록할 사용자의 정보
     * @return 등록된 사용자 정보
     * @throws DuplicateException 이메일이 중복된 경우
     * @throws DuplicateException 닉네임이 중복된 경우
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

    /**
     * 회원 팔로우를 생성한다.
     *
     * @param user   로그인한 사용자
     * @param userId 팔로잉할 사용자 ID
     * @throws NotFoundException   해당하는 아이디를 가진 사용자가 없는 경우
     * @throws BadRequestException 본인을 팔로우하는 경우
     * @throws DuplicateException  이미 해당하는 회원 팔로우가 존재하는 경우
     */
    @Transactional
    public void createUserFollow(User user, Long userId) {
        User followee = userRepository.findById(userId).orElseThrow(() -> NOT_FOUND_USER);
        if (user.equals(followee)) {
            throw BAD_REQUEST_USER_FOLLOW;
        }

        UserFollow userFollow = UserFollow.create(followee, user);
        try {
            userFollowRepository.save(userFollow);
        } catch (DataIntegrityViolationException e) {
            entityManager.clear();
            if (userFollowRepository.existsByUserAndFollower(user, followee)) {
                log.info("중복된 회원 팔로우: {} -> {}", user.getId(), followee.getId());
                throw DUPLICATE_USER_FOLLOW;
            }
            throw e;
        }
    }

    /**
     * 회원 팔로우를 삭제한다.
     *
     * @param user   로그인한 사용자
     * @param userId 언팔로우할 사용자 ID
     * @throws NotFoundException 해당하는 아이디를 가진 사용자가 없는 경우
     * @throws NotFoundException 해당하는 팔로우가 없는 경우
     */
    @Transactional
    public void deleteUserFollow(User user, Long userId) {
        User followee = userRepository.findById(userId).orElseThrow(() -> NOT_FOUND_USER);

        UserFollow userFollow = userFollowRepository.findByUserAndFollower(followee, user)
            .orElseThrow(() -> NOT_FOUND_USER_FOLLOW);
        userFollowRepository.delete(userFollow);
    }

}
