package com.example.apptive_3team.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String providerId;

    @Enumerated(EnumType.STRING) // DB에 ENUM 값으로 저장될 때, String 형태로 저장됨
    private ProviderType providerType;

    private String name;
    private Double latitude;
    private Double longitude;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean allow_notification;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public boolean isAllow_notification() {
        return allow_notification != null && allow_notification;
    }

    // Enum 타입을 정의
    public enum ProviderType {
        GOOGLE,
        KAKAO
    }
}