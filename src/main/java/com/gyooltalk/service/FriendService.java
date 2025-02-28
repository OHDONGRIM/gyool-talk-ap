package com.gyooltalk.service;

import com.gyooltalk.payload.FriendListDto;
import com.gyooltalk.repository.FriendListRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FriendService {

    private final FriendListRepository friendListRepository;

    public ResponseEntity<List<FriendListDto>> fetchFriendList() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        List<FriendListDto> friendListDtos = friendListRepository.findByUserFriendList(userId);

        return ResponseEntity.ok(friendListDtos);
    }
}
