    package com.gyooltalk.service;

    import com.gyooltalk.entity.FriendList;
    import com.gyooltalk.entity.User;
    import com.gyooltalk.payload.FriendAddDto;
    import com.gyooltalk.payload.UserDto;
    import com.gyooltalk.repository.FriendListRepository;
    import com.gyooltalk.repository.UserRepository;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;

    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.text.SimpleDateFormat;
    import java.time.LocalDateTime;
    import java.util.Base64;
    import java.util.Date;
    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class UserActiveService {

        private final UserRepository userRepository;
        private final FriendListRepository friendListRepository;

        @Value("${file.upload-dir}")
        private String uploadDir;
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

        public ResponseEntity<Boolean> addFriend(FriendAddDto friendAddDto) {
            User user = userRepository.findByUserId(friendAddDto.getUserId());
            User friend = userRepository.findByUserId(friendAddDto.getFriendId());

            if (user == null || friend == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
            }

            FriendList friendList = FriendList.builder()
                    .user(user)
                    .friend(friend)
                    .friendNickname(friend.getUserNickName())
                    .status(friendAddDto.getStatus())
                    .requestDate(LocalDateTime.now())
                    .build();

            log.debug("친구추가 확인 => ", friendList);

            friendListRepository.save(friendList);

            return ResponseEntity.ok(true);
        }
        @Transactional
        public ResponseEntity<?>  UpdateNickname(UserDto userDto) {
            int updatedCount = userRepository.updateNickname(userDto.getUserId(), userDto.getUserNickName());
            if (updatedCount >0){

                return ResponseEntity.ok(userDto);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }


        public ResponseEntity<String>  uploadImage(String uri, String  userId) {
            // Base64 데이터를 분리하여 디코딩
            String base64Data = uri.split(",")[1];

            // Base64 디코딩
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            log.info(uri + " file");
            log.info(userId + " user");

            // 저장할 경로 (uploadDir 변수가 선언되어야 함)
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String fileName =  sdf.format(now)+".jpg";
            String savePath = userId + "/" + fileName;

            try {
                // 디렉토리가 없으면 생성
                java.nio.file.Files.createDirectories(java.nio.file.Paths.get(uploadDir + userId));

                // 파일 저장
                try (FileOutputStream fos = new FileOutputStream(uploadDir + savePath)) {
                    fos.write(imageBytes);
                }

                userRepository.uploadImage(userId,savePath);


                return ResponseEntity.ok(savePath); // 성공적으로 저장되었을 경우

            } catch (IOException e) {
                log.error("파일 저장 실패: ", e);
                return ResponseEntity.internalServerError().body("upload image fail"); // 오류 발생 시
            }
        }
    }
