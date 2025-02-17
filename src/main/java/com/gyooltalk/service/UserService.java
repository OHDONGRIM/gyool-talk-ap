package com.gyooltalk.service;

import com.gyooltalk.entity.User;
import com.gyooltalk.payload.UserDto;
import com.gyooltalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserDto findByUserEmail(String email) {
        UserDto user = userRepository.findByUserEmail(email);
        return user;
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
}
