package com.gyooltalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {

    @Id
    private String userId;

    private String userEmail;
    private String userPassword;
    private String userNickName;
    private String userProfileImg;
    @Column(nullable = false, updatable = false)
    private LocalDateTime registerDate;
    private LocalDateTime lastLoginDate;

    // fetch lazy, 필드 호출 시에만 db호출
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserToken> userTokens;

    @PrePersist
    public void prePersist() {
        this.registerDate = LocalDateTime.now(); // 현재 시간으로 등록시간 설정
    }
}
