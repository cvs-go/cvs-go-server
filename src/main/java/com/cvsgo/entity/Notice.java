package com.cvsgo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<NoticeImage> noticeImages = new ArrayList<>();

    public void addImage(String imageUrl) {
        NoticeImage noticeImage = NoticeImage.builder()
            .notice(this)
            .imageUrl(imageUrl)
            .build();
        noticeImages.add(noticeImage);
    }

    @Builder
    public Notice(Long id, String title, String content, List<String> imageUrls) {
        this.id = id;
        this.title = title;
        this.content = content;
        if (imageUrls != null) {
            for (String imageUrl : imageUrls) {
                addImage(imageUrl);
            }
        }
    }
}
