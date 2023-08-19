package com.cvsgo.dto.user;

import com.cvsgo.dto.tag.TagResponseDto;
import com.cvsgo.entity.User;
import java.util.List;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final List<TagResponseDto> tags;
    private final long reviewLikeCount;

    private UserResponseDto(User user, long reviewLikeCount) {
        this.id = user.getId();
        this.email = user.getUserId();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.tags = user.getUserTags().stream()
                .map(userTag -> TagResponseDto.from(userTag.getTag()))
                .toList();
        this.reviewLikeCount = reviewLikeCount;
    }

    public static UserResponseDto of(User user, long likeCount) {
        return new UserResponseDto(user, likeCount);
    }
}
