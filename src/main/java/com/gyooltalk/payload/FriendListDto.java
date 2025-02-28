package com.gyooltalk.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendListDto {

    // FriendList 식별자
    private Long id;
    // 친구 별명
    private String friendNickName;
    // 친구 썸네일 Url
    private String userProfileImg;
    private String friendUserNickName;

}
