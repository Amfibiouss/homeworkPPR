package com.example.laba.json_objects;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class OutputStateRoom {

    public String status;

    public List<OutputStateChannel> channels;

    public List<OutputStatePoll> polls;

    public OffsetDateTime finish_stage;
    public String stage;
    public OutputMessage newspaperMessage;

    public OutputMessage getNewspaperMessage() {
        return newspaperMessage;
    }

    public void setNewspaperMessage(OutputMessage newspaperMessage) {
        this.newspaperMessage = newspaperMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OutputStateChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<OutputStateChannel> channels) {
        this.channels = channels;
    }

    public List<OutputStatePoll> getPolls() {
        return polls;
    }

    public void setPolls(List<OutputStatePoll> polls) {
        this.polls = polls;
    }

    public OffsetDateTime getFinish_stage() {
        return finish_stage;
    }

    public void setFinish_stage(OffsetDateTime finish_stage) {
        this.finish_stage = finish_stage;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
