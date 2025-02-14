package com.gyooltalk.service;

import com.gyooltalk.entity.User;
import com.gyooltalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public String findByUserEmail(String email) {
        Optional<User> user = userRepository.findByUserEmail(email);
        return user.map(User::getUserId).orElse(null);  // 아이디 없으면 null 반환
    }
}
