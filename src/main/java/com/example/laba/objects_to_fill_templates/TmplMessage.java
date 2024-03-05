package com.example.laba.objects_to_fill_templates;

import com.example.laba.entities.FMessage;

public class TmplMessage {
    public long user_id;
    public String login;

    public String text;

    public TmplMessage(long id, String login, String text) {
        this.user_id = id;
        this.login = login;
        this.text = text;
    }

    public TmplMessage(){}
}
