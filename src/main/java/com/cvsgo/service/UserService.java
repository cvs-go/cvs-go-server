package com.cvsgo.service;

import static com.cvsgo.exception.ExceptionConstants.BAD_REQUEST_USER_FOLLOW;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_EMAIL;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_NICKNAME;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_USER_FOLLOW;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_USER;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_USER_FOLLOW;

import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.dto.user.SignUpResponseDto;
import com.cvsgo.dto.user.UpdateUserRequestDto;
import com.cvsgo.dto.user.UserResponseDto;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserFollow;
import com.cvsgo.entity.UserTag;
import com.cvsgo.exception.BadRequestException;
import com.cvsgo.exception.DuplicateException;
import com.cvsgo.exception.NotFoundException;
import com.cvsgo.repository.ReviewRepository;
import com.cvsgo.repository.TagRepository;
import com.cvsgo.repository.UserFollowRepository;
import com.cvsgo.repository.UserRepository;
import com.cvsgo.repository.UserTagRepository;
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

    private final UserTagRepository userTagRepository;

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    private final UserFollowRepository userFollowRepository;

    private final ReviewRepository reviewRepository;

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
     * 사용자 정보를 조회한다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 상세 정보
     */
    @Transactional(readOnly = true)
    public UserResponseDto readUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> NOT_FOUND_USER);

        long reviewLikeCount = reviewRepository.findAllByUser(user)
            .stream().mapToLong(Review::getLikeCount).sum();

        return UserResponseDto.of(user, reviewLikeCount);
    }

    /**
     * 사용자 정보를 수정한다.
     *
     * @param user    로그인한 사용자
     * @param request 수정할 사용자 정보
     * @throws DuplicateException 닉네임이 중복된 경우
     */
    @Transactional
    public void updateUser(User user, UpdateUserRequestDto request) {
        if (!user.getNickname().equals(request.getNickname()) && isDuplicatedNickname(
            request.getNickname())) {
            throw DUPLICATE_NICKNAME;
        }
        List<Tag> tags = tagRepository.findAllById(request.getTagIds());

        user.updateNickname(request.getNickname());
        user.updateTags(tags);
        user.updateProfileImageUrl(request.getProfileImageUrl());
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

        if (userFollowRepository.existsByUserAndFollower(user, followee)) {
            log.info("중복된 회원 팔로우: {} -> {}", user.getId(), followee.getId());
            throw DUPLICATE_USER_FOLLOW;
        }

        UserFollow userFollow = UserFollow.create(followee, user);
        userFollowRepository.save(userFollow);
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

    /**
     * 태그 매칭률을 조회한다.
     *
     * @param user   로그인한 사용자
     * @param userId 태그 매칭률을 조회할 사용자 ID
     * @return 태그 매칭률
     * @throws NotFoundException 해당하는 아이디를 가진 사용자가 없는 경우
     */
    @Transactional(readOnly = true)
    public Integer readUserTagMatchPercentage(User user, Long userId) {
        User targetUser = userRepository.findById(userId).orElseThrow(() -> NOT_FOUND_USER);

        List<Tag> loginUserTag = userTagRepository.findAllByUser(user).stream().map(UserTag::getTag)
            .toList();
        List<Tag> targetUserTag = userTagRepository.findAllByUser(targetUser).stream().map(
            UserTag::getTag).toList();

        int matchingCount = 0;
        for (Tag tag : loginUserTag) {
            if (targetUserTag.contains(tag)) {
                matchingCount++;
            }
        }

        return (int) (((double) matchingCount / loginUserTag.size()) * 100);
    }

}
