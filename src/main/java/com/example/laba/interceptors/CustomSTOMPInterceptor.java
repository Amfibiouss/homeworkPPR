package com.example.laba.interceptors;

import com.example.laba.services.RoomChannelMessageDaoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class CustomSTOMPInterceptor implements ChannelInterceptor {
    @Autowired
    RoomChannelMessageDaoService RCMDAOService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getUser() == null)
            return null;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            RCMDAOService.add_player(accessor.getUser().getName(), accessor.getSessionId());
        }

        return message;
    }
}
