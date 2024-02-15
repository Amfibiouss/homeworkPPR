package com.example.laba.structures;

public class Message {
    public long id;
    public String login;
    public String text;
    public Message(String login, String text) {
        this.login = login;
        this.text = text;
    }

    public Message(long id, String login, String text) {
        this.id = id;
        this.login = login;
        this.text = text;
    }

    public Message(){}
}
