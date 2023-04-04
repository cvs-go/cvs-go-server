package com.cvsgo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ReviewImage> reviewImages = new ArrayList<>();

    @NotNull
    @ColumnDefault("0")
    private Long likeCount = 0L;

    @Builder
    public Review(Long id, String content, Integer rating, User user, Product product,
        List<String> imageUrls) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.user = user;
        this.product = product;
        for (String imageUrl : imageUrls) {
            this.reviewImages.add(ReviewImage.builder()
                .review(this)
                .imageUrl(imageUrl)
                .build());
        }
    }

}
