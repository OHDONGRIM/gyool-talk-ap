package com.gyooltalk.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@RedisHash("emailAuth")
public class EmailAuth {

    @Id
    private String email;  // 이메일을 고유 식별자로 사용

    private String authNum;

    @TimeToLive
    private Long expiredTime;
}