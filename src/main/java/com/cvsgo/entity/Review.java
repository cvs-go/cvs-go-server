package com.cvsgo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "product_id"})})
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    private Integer rating;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ReviewImage> reviewImages = new ArrayList<>();

    @NotNull
    @ColumnDefault("0")
    private Long likeCount = 0L;

    @Version
    private Long version;

    public void addImage(String imageUrl) {
        ReviewImage reviewImage = ReviewImage.builder()
            .review(this)
            .imageUrl(imageUrl)
            .build();
        reviewImages.add(reviewImage);
    }

    @Builder
    public Review(Long id, String content, Integer rating, User user, Product product,
        List<String> imageUrls) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.user = user;
        this.product = product;
        if (imageUrls != null) {
            for (String imageUrl : imageUrls) {
                addImage(imageUrl);
            }
        }
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateRating(Integer rating) {
        this.rating = rating;
    }

    public void updateReviewImages(List<String> imageUrls) {
        this.reviewImages.removeIf(reviewImage -> !imageUrls.contains(reviewImage.getImageUrl()));
        List<String> currentImageUrls = reviewImages.stream().map(ReviewImage::getImageUrl)
            .toList();
        for (String imageUrl : imageUrls) {
            if (!currentImageUrls.contains(imageUrl)) {
                addImage(imageUrl);
            }
        }
    }

    public void plusLikeCount() {
        this.likeCount++;
    }

    public void minusLikeCount() {
        this.likeCount--;
    }

}
