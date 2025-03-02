package com.gyooltalk.payload;

public interface FriendListDto {

    // FriendList 식별자
    Long getId();
    // 친구 아이디
    String getUserId();
    // 친구 별명 (내가 설정한 별명)
    String getFriendNickName();
    // 친구 썸네일 Url
    String getUserProfileImg();
    // 친구가 설정한 별명
    String getFriendUserNickName();

}
