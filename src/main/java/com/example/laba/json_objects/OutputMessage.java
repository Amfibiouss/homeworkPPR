package com.example.laba.json_objects;

public class OutputMessage {
    public String username;

    public String text;
    public double opacity;

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

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public OutputMessage(String username, String text, double opacity) {
        this.username = username;
        this.text = text;
        this.opacity = opacity;
    }

    public OutputMessage(String username, String text) {
        this.username = username;
        this.text = text;
    }

    public OutputMessage() {}
}
