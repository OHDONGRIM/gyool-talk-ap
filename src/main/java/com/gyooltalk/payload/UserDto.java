package com.gyooltalk.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String userId;
}
