package com.gyooltalk.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChattingRequestDto {

    private String friendId;
    private String friendNickname;
    private String userNickname;

}
