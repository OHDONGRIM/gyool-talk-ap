package com.gyooltalk.repository;

import com.gyooltalk.entity.FriendList;
import com.gyooltalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendListRepository extends JpaRepository<FriendList, String> {
    boolean existsByFriendAndStatus(User friend, int status);
}

