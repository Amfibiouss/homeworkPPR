package com.example.laba.json_objects;

public class InputRoom {
    public String name;

    public String description;
    public String help;
    public long max_players;
    public long min_players;

    public String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public long getMax_players() {
        return max_players;
    }

    public void setMax_players(long max_players) {
        this.max_players = max_players;
    }

    public long getMin_players() {
        return min_players;
    }

    public void setMin_players(long min_players) {
        this.min_players = min_players;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
