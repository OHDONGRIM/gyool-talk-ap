package com.gyooltalk.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendAddDto {
    private String userId;
    private String friendId;
    private int status;
}
