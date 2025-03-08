package com.gyooltalk.interceptor;

import com.gyooltalk.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    // static final String으로 선언
    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("WebSocketInterceptor ==> ", message.getPayload());
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && accessor.getCommand() == StompCommand.CONNECT) {
            String token = accessor.getFirstNativeHeader(HEADER_STRING);

            if (token != null && token.startsWith(TOKEN_PREFIX)) {
                token = token.substring(TOKEN_PREFIX.length()); // "Bearer " 제거

                // 토큰 검증
                if (jwtTokenProvider.validateToken(token)) {
                    // 인증 정보 주입
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    accessor.setUser(auth);
                }
            }
        }
        return message;
    }
}