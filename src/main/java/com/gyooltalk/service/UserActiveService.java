    package com.gyooltalk.service;

    import com.gyooltalk.entity.FriendList;
    import com.gyooltalk.entity.User;
    import com.gyooltalk.payload.FriendAddDto;
    import com.gyooltalk.payload.UserDto;
    import com.gyooltalk.repository.FriendListRepository;
    import com.gyooltalk.repository.UserRepository;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class UserActiveService {

        private final UserRepository userRepository;
        private final FriendListRepository friendListRepository;

        // 친구 목록을 조회하는 메서드
        public ResponseEntity<?> getFriends(String userId) {
            //친구 목록 조회
            List<FriendList> friendList = friendListRepository.findByUser_UserId(userId);
            log.debug("friendList: {}", friendList);
            List<UserDto> friends = friendList.stream()
                    .map(friend -> {
                        User friendUser = friend.getFriend();  // FriendList에서 User 객체 가져오기

                        // UserDto로 변환
                        return UserDto.builder()
                                .userId(friendUser.getUserId())
                                .userNickName(friendUser.getUserNickName())
                                .userProfileImg(friendUser.getUserProfileImg())
                                .build();
                    })
                    .collect(Collectors.toList());
            return new ResponseEntity<>(friends, HttpStatus.OK);
        }
    }
