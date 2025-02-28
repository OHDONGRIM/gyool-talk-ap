package com.gyooltalk.repository;

import com.gyooltalk.entity.FriendList;
import com.gyooltalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendListRepository extends JpaRepository<FriendList, String> {
    boolean existsByFriendAndStatus(User friend, int status);
    List<FriendList> findByUser_UserId(String userId);
}

