package com.example.laba.json_objects;

public class InputRoom {
    public String name;

    public String description;
    public String help;
    public long max_players;
    public long min_players;
    public String mode;

    public InputRoom(String roomName, String roomDescription, String roomHelp, long roomMinPlayers, long roomMaxPlayers, String mode) {
        this.name = roomName;
        this.description = roomDescription;
        this.help = roomHelp;
        this.max_players = roomMaxPlayers;
        this.min_players = roomMinPlayers;
        this.mode = mode;
    }

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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
