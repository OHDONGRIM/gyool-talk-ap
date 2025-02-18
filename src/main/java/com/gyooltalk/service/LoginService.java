package com.gyooltalk.service;

import com.gyooltalk.entity.User;
import com.gyooltalk.exception.ResourceNotFoundException;
import com.gyooltalk.payload.LoginResponseDto;
import com.gyooltalk.payload.UserDto;
import com.gyooltalk.repository.UserRepository;
import com.gyooltalk.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<LoginResponseDto> login(UserDto userDto) {

        // 유저 id 검색 후, 존재하지 않는 아이디면 404 코드 반환
        User user = userRepository.findById(userDto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "userId", userDto.getUserId()));

        // userPassword 암호화 이후 로직
        // 유저 password 비교 후, 같지 않으면 401 코드 반환
//        if (!passwordEncoder.matches(userDto.getUserPassword(), user.getUserPassword())) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user password");
//        }

        if (!userDto.getUserPassword().equals(user.getUserPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user password");
        }

        // 토큰 생성
        String token = jwtTokenProvider.generateToken(user);

        // 토큰 기록은 좀 더 생각해봐야할 듯.

        // LoginResponseDto 객체 생성후 200 코드와 함께 반환
        return ResponseEntity.ok(LoginResponseDto.builder()
                        .token(token)
                        .userId(user.getUserId())
                        .userNickname(user.getUserNickName())
                .build());
    }
}
