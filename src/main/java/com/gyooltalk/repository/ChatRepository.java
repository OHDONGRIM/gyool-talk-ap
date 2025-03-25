package com.gyooltalk.repository;

import com.gyooltalk.entity.Chat;
import com.gyooltalk.entity.Participant;
import com.gyooltalk.payload.ChatDto;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, Long> {

    @Query("{ 'participants.userId':  { $all:  [?0, ?1] } }")
    Optional<Chat> findByParticipants(String userId, String friendId);

    @Query("{ 'participants.userId':  { $all:  [?0] } }")
    List<ChatDto> findByUserId(String userId);

    @Query("{ 'participants': { $elemMatch: { 'userId': { $all: [?0] }, 'joinTime': { $ne: null} } } }")
    List<ChatDto> findByUserIdWithNonEmptyJoinTime(String userId);

    @Query("{ '_id': ?0 }")
    Optional<Chat> findByChatId(Long chatId);

    @Query("{ '_id': ?0, 'participants.userId': ?1 }")
    Optional<Chat> findByChatIdAndUserId(Long chatId, String userId);

    @Query("{ '_id': ?0, 'participants': { $elemMatch: { 'userId': ?1, 'joinTime': { $ne: null } } } }")
    Boolean existsByChatIdAndUserIdAndJoinTimeNotNull(Long chatId, String userId);

//    @Query("{ '_id': ?0, 'userId': ?1, 'messages.jointime': { $gte: ?2 } }")
//    Optional<Chat> findMessagesByChatIdAndUserIdAndJointimeAfter(Long chatId, Long userId, Date jointime);

    @Modifying
    @Query("{ '_id': ?0, 'participants.userId': ?1 }")
    @Update("{ '$set': { 'participants.$.joinTime': null } }")
    void removeUserFromParticipants(Long chatId, String userId);

    @Modifying
    @Query("{ '_id': ?0, 'participants.userId': ?1 }")
    @Update("{ '$set': { 'participants.$.joinTime': ?2 } }")
    void updateJoinTimeByChatIdAndUserId(Long chatId, String userId, LocalDateTime joinTime);
}
