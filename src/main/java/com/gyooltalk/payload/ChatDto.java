package com.gyooltalk.payload;

import com.gyooltalk.entity.Message;
import com.gyooltalk.entity.Participant;

import java.util.List;

public interface ChatDto {
    List<Participant> getParticipants();
    List<MessageDto> getMessages();
    String getChatroomName();
    Long getId();
}
