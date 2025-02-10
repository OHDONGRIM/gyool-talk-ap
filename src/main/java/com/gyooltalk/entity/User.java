package com.gyooltalk.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

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

}
