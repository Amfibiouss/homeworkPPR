package com.example.laba.json_objects;

public class InputMessage {
    public String text;

    public String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public InputMessage(String text) {
        this.text = text;
    }

    public InputMessage() {}
}
