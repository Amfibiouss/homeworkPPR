package com.example.laba.listeners;
import com.example.laba.services.RoomChannelMessageDaoService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class SessionConnectedEventListener implements ApplicationListener<SessionConnectedEvent> {

    @Autowired
    RoomChannelMessageDaoService RCMDAOService;

    @Autowired
    private SimpMessagingTemplate template;

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {

        if (event.getUser() != null) {

            long room_id = RCMDAOService.get_room_id(event.getUser().getName());
            template.convertAndSend("/topic/players/" + room_id, RCMDAOService.get_players(room_id));

            System.out.println("connected " +  event.getUser().getName());
        }
    }
}
