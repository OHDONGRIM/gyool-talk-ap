package com.gyooltalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {

    @Id
    private String userId;
    private String userEmail;
    private String userPassword;
    private String userNickName;
    private String userProfileImg;
    private LocalDateTime registerDate;
    private LocalDateTime lastLoginDate;

    // fetch lazy, 필드 호출 시에만 db호출
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserToken> userTokens;

}
