    package com.gyooltalk.service;

    import com.gyooltalk.entity.FriendList;
    import com.gyooltalk.entity.User;
    import com.gyooltalk.payload.FriendAddDto;
    import com.gyooltalk.payload.LoginResponseDto;
    import com.gyooltalk.payload.UserDto;
    import com.gyooltalk.repository.FriendListRepository;
    import com.gyooltalk.repository.UserRepository;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.apache.catalina.connector.Request;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;

    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.text.SimpleDateFormat;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.Base64;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Map;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class UserService {



        private final UserRepository userRepository;
        private final FriendListRepository friendListRepository;

        public ResponseEntity<?> findByUserEmail(UserDto userDto,String userId) {
            /**
             * 1. 로그인 전 아이디 찾기  ==> userDto.getUserEmail != null
             * 2. 로그인 후 친구 추가 아이디 찾기  ==> userDto.getUserEmail == null
             * */
            User user = userDto.getUserEmail() !=null ? userRepository.findByUserEmail(userDto.getUserEmail()) :userRepository.findByUserId(userDto.getUserId());
            Map<String, Object> body = new HashMap<>();

            // user null 체크
            if (user == null) {

                body.put("message", "사용자를 찾을 수 없습니다.");
                body.put("isEmailUser", "N"); // 새 key-value 추가
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(body);
            }

            UserDto.UserDtoBuilder builder = UserDto.builder()
                    .userId(user.getUserId());

            // userId로 검색한 경우에만 프로필 URL 추가 ==> 친구 추가 아이디 검색의 경우
            if (userDto.getUserId() != null) {
                builder.userProfileImg(user.getUserProfileImg());
                builder.userNickName(user.getUserNickName());

                // 친구 요청 상태 확인 (로그인한 사용자와의 관계)
                long friendCnt = friendListRepository.existsByUserAndFriend(userId, user.getUserId());
                boolean isFriend = friendCnt > 0 ?  true:false;// 요청 중 상태 확인
                builder.isFriendRequest(isFriend); // 친구 요청 상태를 추가
            }

            return ResponseEntity.ok(builder.build());
        }


        public boolean existsByUserId(String id) {
            boolean exists = userRepository.existsByUserId(id);
            return exists;
        }

        public boolean insertUser(UserDto userDto) {
            User user = User.builder()
                            .userId(userDto.getUserId())
                            .userPassword(userDto.getUserPassword())
                            .userEmail(userDto.getUserEmail())
                            .userNickName(userDto.getUserNickName())
                            .build();
            userRepository.save(user);
            boolean exists = userRepository.existsByUserId(user.getUserId());

            return exists;
        }

        public ResponseEntity<Boolean> resetPassword(UserDto userDto) {
            int updatedCount = userRepository.resetPassword(userDto.getUserEmail(), userDto.getUserPassword());
            // updatedCount가 1 이상이면 true, 아니면 false를 ResponseEntity로 반환
            return ResponseEntity.ok(updatedCount > 0);
        }




    }
