package com.example.laba.json_objects;

import java.util.List;

public class InputStateRoom {

    public List<InputStateChannel> channels;

    public List<InputStatePoll> polls;

    public List<String> messages;

    public long duration;
    public String stage;

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
}


