package com.example.laba.json_objects;

import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.List;

public class OutputStageChannel {
    String stage_name;
    long stage_id;
    long count;
    @JsonRawValue
    List<String> messages;
    boolean loaded;

    public OutputStageChannel(String stage_name, long stage_id, long count, List<String> messages, boolean loaded) {
        this.stage_name = stage_name;
        this.stage_id = stage_id;
        this.count = count;
        this.messages = messages;
        this.loaded = loaded;
    }

    public String getStage_name() {
        return stage_name;
    }

    public void setStage_name(String stage_name) {
        this.stage_name = stage_name;
    }

    public long getStage_id() {
        return stage_id;
    }

    public void setStage_id(long stage_id) {
        this.stage_id = stage_id;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
