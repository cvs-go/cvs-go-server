package com.cvsgo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Promotion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    private String imageUrl;

    private String landingUrl;

    private Integer priority;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Builder
    public Promotion(Long id, String name, String imageUrl, String landingUrl, Integer priority,
        LocalDateTime startAt, LocalDateTime endAt) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.landingUrl = landingUrl;
        this.priority = priority;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
