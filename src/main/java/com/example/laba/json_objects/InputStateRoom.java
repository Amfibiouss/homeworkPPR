package com.example.laba.json_objects;

import java.util.List;
import java.util.Map;

public class InputStateRoom {

    public String status;

    public List<InputStateChannel> channels;

    public List<InputStatePoll> polls;

    public List<InputStateCharacter> characters;

    public List<String> messages;

    public long duration;
    public String stage;

    public Map<String, String> names;

    public Map<String, String> getNames() {
        return names;
    }

    public void setNames(Map<String, String> names) {
        this.names = names;
    }

    public List<InputStateChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<InputStateChannel> channels) {
        this.channels = channels;
    }

    public List<InputStatePoll> getPolls() {
        return polls;
    }

    public void setPolls(List<InputStatePoll> polls) {
        this.polls = polls;
    }

    public List<InputStateCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(List<InputStateCharacter> characters) {
        this.characters = characters;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


