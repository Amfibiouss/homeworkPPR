package com.example.laba.objects_to_fill_templates;

public class TmplMessage {
    public long id;
    public String login;
    public String text;
    public TmplMessage(String login, String text) {
        this.login = login;
        this.text = text;
    }

    public TmplMessage(long id, String login, String text) {
        this.id = id;
        this.login = login;
        this.text = text;
    }

    public TmplMessage(){}
}
