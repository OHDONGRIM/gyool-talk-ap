package com.gyooltalk.repository;

import com.gyooltalk.entity.Chat;
import com.gyooltalk.entity.Participant;
import com.gyooltalk.payload.ChatDto;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, Long> {

    @Query("{ 'participants.userId':  { $all:  [?0, ?1] } }")
    Optional<Chat> findByParticipants(String userId, String friendId);

    @Query("{ 'participants.userId':  { $all:  [?0] } }")
    List<ChatDto> findByUserId(String userId);

    @Query("{ '_id': ?0 }")
    Optional<Chat> findByChatId(Long chatId);

    @Modifying
    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'entry_time.?1': '' } }")
    void removeUserFromParticipants(Long chatId,int index);
}
