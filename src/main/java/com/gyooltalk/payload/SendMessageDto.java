package com.gyooltalk.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageDto implements MessageDto {
    private Long id;
    private String senderId;
    private String content;
    private int messageType;
    private List<AttachmentDto> attachments;
    private LocalDateTime timestamp;
}