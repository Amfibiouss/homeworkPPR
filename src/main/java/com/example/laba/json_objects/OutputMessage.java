package com.example.laba.json_objects;

public class OutputMessage {
    public String username;

    public String text;

    public double opacity;
    public String alias;
    public String username_color;

    public Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUsername_color() {
        return username_color;
    }

    public void setUsername_color(String username_color) {
        this.username_color = username_color;
    }

    public OutputMessage(String username,
                                     String alias,
                                     String text,
                                     double opacity,
                                     String username_color,
                                     long id) {
        this.username = username;
        this.alias = alias;
        this.text = text;
        this.opacity = opacity;
        this.username_color = username_color;
        this.id = id;
    }

    public OutputMessage(String username, String text, double opacity) {
        this.username = username;
        this.text = text;
        this.opacity = opacity;
    }

    public OutputMessage() {}
}
