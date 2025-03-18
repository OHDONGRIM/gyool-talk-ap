package com.gyooltalk.payload;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private String userId;
    private String userEmail;
    private String userPassword;
    private String userNickName;
    private String userProfileImg;
    private String registerDate;
    private String lastLoginDate;
    private boolean isFriendRequest;
}

