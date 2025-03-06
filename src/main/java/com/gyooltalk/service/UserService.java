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
    import java.util.Map;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class UserService {

        @Value("${file.upload-dir}")
        private String uploadDir;

        private final UserRepository userRepository;
        private final FriendListRepository friendListRepository;

        public ResponseEntity<?> findByUserEmail(UserDto userDto) {

            User user = userDto.getUserEmail() !=null ? userRepository.findByUserEmail(userDto.getUserEmail()) :userRepository.findByUserId(userDto.getUserId());

            // user null 체크
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "사용자를 찾을 수 없습니다."));
            }

            UserDto.UserDtoBuilder builder = UserDto.builder()
                    .userId(user.getUserId());

            // userId로 검색한 경우에만 프로필 URL 추가
            if (userDto.getUserId() != null) {
                builder.userProfileImg(user.getUserProfileImg());
                builder.userNickName(user.getUserNickName());

                // 친구 요청 상태 확인 (로그인한 사용자와의 관계)
                boolean isRequestPending = friendListRepository.existsByFriendAndStatus(user, 0); // 요청 중 상태 확인
                builder.isFriendRequest(isRequestPending); // 친구 요청 상태를 추가
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


//        public ResponseEntity<Boolean>  uploadImage(String uri, String  userId) {
//            // Base64 데이터를 분리하여 디코딩
//            String base64Data = uri.split(",")[1];
//
//            // Base64 디코딩
//            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
//
//            log.info(uri + " file");
//            log.info(userId + " user");
//
//            // 저장할 경로 (uploadDir 변수가 선언되어야 함)
//            Date now = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//            String savePath = uploadDir + userId + "/" + sdf.format(now)+".jpg";
//
//            try {
//                // 디렉토리가 없으면 생성
//                java.nio.file.Files.createDirectories(java.nio.file.Paths.get(uploadDir + userId));
//
//                // 파일 저장
//                try (FileOutputStream fos = new FileOutputStream(savePath)) {
//                    fos.write(imageBytes);
//                }
//
//                userRepository.uploadImage(userId,savePath);
//                return ResponseEntity.ok(true); // 성공적으로 저장되었을 경우
//
//            } catch (IOException e) {
//                log.error("파일 저장 실패: ", e);
//                return ResponseEntity.internalServerError().body(false); // 오류 발생 시
//            }
//        }
    }
