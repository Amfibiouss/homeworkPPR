package com.example.laba.listeners;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class SessionConnectedEventListener implements ApplicationListener<SessionConnectedEvent> {

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {

        if (event.getUser() != null) {
            System.out.println("connected " +  event.getUser().getName());
        }

    }
}
