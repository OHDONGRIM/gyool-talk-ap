package com.gyooltalk.repository;

import com.gyooltalk.entity.Chat;
import com.gyooltalk.payload.ChatDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, Long> {

    @Query("{ 'participants':  { $all:  [?0, ?1] } }")
    Optional<Chat> findByParticipants(String userId, String friendId);

    @Query("{ 'participants':  { $all:  [?0] } }")
    List<ChatDto> findByUserId(String userId);
}
