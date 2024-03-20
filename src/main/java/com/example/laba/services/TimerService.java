package com.example.laba.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TimerService {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private RoomChannelMessageDaoService RCMDAOService;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);


    public void notify_host_after_delay(final long room_id, final long delay) {
        scheduler.schedule(new Runnable(){
            public void run() {
                RCMDAOService.set_room_status(room_id, "processing");
                template.convertAndSend("/topic/room_status/" + room_id, "processing");
            }
        }, delay, TimeUnit.SECONDS);
    }
}
