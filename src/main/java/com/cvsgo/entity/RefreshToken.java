package com.cvsgo.entity;

import io.jsonwebtoken.Jwts;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private String token;

    private RefreshToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public static RefreshToken create(User user, Key key, long ttlMillis) {
        long now = System.currentTimeMillis();
        String refreshToken = Jwts.builder()
                .setIssuedAt(new Date(now))
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date(now + ttlMillis))
                .signWith(key)
                .compact();
        return new RefreshToken(user, refreshToken);
    }

    public RefreshToken updateToken(Key key, long ttlMillis) {
        long now = System.currentTimeMillis();
        this.token = Jwts.builder()
                .setIssuedAt(new Date(now))
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date(now + ttlMillis))
                .signWith(key)
                .compact();
        return this;
    }

}
