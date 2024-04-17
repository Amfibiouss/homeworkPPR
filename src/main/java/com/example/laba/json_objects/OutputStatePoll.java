package com.example.laba.json_objects;

import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.List;
import java.util.Map;

public class OutputStatePoll {
    public long poll_id;

    public String name;
    public String description;
    public long lindex;
    public long maxSelection;
    public long minSelection;
    @JsonRawValue
    public String candidates;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(long maxSelection) {
        this.maxSelection = maxSelection;
    }

    public long getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(long minSelection) {
        this.minSelection = minSelection;
    }

    public String getCandidates() {
        return candidates;
    }

    public void setCandidates(String candidates) {
        this.candidates = candidates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPoll_id() {
        return poll_id;
    }

    public void setPoll_id(long poll_id) {
        this.poll_id = poll_id;
    }

    public long getLindex() {
        return lindex;
    }

    public void setLindex(long lindex) {
        this.lindex = lindex;
    }
}
