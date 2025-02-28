package com.gyooltalk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(name = "user")
public class FriendList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_user_id") // 외래 키 컬럼 이름 지정
    private User user;

    @ManyToOne
    private User friend;

    private String friendNickname;
    private int status;
    private LocalDateTime requestDate;
}
