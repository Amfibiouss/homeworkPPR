package com.example.laba.structures;

public class MessageForm {
    public long id;
    public String login;
    public String text;
    public MessageForm(String login, String text) {
        this.login = login;
        this.text = text;
    }

    public MessageForm(long id, String login, String text) {
        this.id = id;
        this.login = login;
        this.text = text;
    }

    public MessageForm(){}
}
