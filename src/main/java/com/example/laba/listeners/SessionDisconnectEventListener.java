package com.example.laba.listeners;

import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import com.example.laba.services.RoomChannelMessageDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionDisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {
    @Autowired
    RoomChannelMessageDaoService RCMDAOService;

    @Autowired
    private SimpMessagingTemplate template;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {

        if (event.getUser() != null) {
            System.out.println("disconnect " +  event.getUser().getName());

            long room_id = RCMDAOService.remove_player(event.getUser().getName(), false);

            if (room_id > 0) {
                template.convertAndSend("/topic/players/" + room_id,  RCMDAOService.get_players(room_id));
            }
        }
    }
}
