package com.gyooltalk.repository;

import com.gyooltalk.entity.FriendList;
import com.gyooltalk.entity.User;
import com.gyooltalk.payload.FriendListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendListRepository extends JpaRepository<FriendList, String> {
    boolean existsByFriendAndStatus(User friend, int status);
    List<FriendList> findByUser_UserId(String userId);


    @Query(value = "SELECT " +
            "    fl.id as id, " +
            "    fl.friend_nickname as friendNickName, " +
            "    u.user_nick_name as friendUserNickName, " +
            "    u.user_profile_img as userProfileImg " +
            "FROM friend_list fl " +
            "LEFT JOIN user u ON u.user_id = fl.friend_user_id " +
            "WHERE fl.user_user_id = :userId " +
            "  AND fl.status = 0", nativeQuery = true)
    List<FriendListDto> findByUserFriendList(String userId);
}

