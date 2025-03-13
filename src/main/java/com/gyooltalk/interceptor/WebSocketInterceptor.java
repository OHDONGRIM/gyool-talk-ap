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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && accessor.getCommand() == StompCommand.CONNECT) {
            String token = accessor.getFirstNativeHeader(HEADER_STRING);

            if (token != null && token.startsWith(TOKEN_PREFIX)) {
                token = token.substring(TOKEN_PREFIX.length()); // "Bearer " 제거

                if (jwtTokenProvider.validateToken(token)) {
                    // UserId 추출
                    String userId = jwtTokenProvider.getUserId(token);

                    // UserId만 포함된 Authentication 객체 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, null);

                    // SecurityContext 및 WebSocket 세션에 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    accessor.setUser(authentication);
                } else {
                    log.warn("토큰 검증 실패");
                }
            } else {
                log.warn("token x");
            }
        }
        return message;
    }
}