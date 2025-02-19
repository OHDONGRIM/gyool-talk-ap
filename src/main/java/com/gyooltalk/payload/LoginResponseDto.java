package com.gyooltalk.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {

    private String userId;
    private String userNickname;
    private String token;

}
