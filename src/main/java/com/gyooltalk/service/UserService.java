package com.gyooltalk.service;

import com.gyooltalk.entity.User;
import com.gyooltalk.payload.LoginResponseDto;
import com.gyooltalk.payload.UserDto;
import com.gyooltalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

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
        }

        return ResponseEntity.ok(builder.build());
    }


    public boolean existsByUserId(String id) {
        boolean exists = userRepository.existsByUserId(id);
        return exists;
    }

    public boolean insertUser(User user) {
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
