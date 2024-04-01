package com.example.laba.json_objects;

import java.util.List;
import java.util.Map;

public class OutputStatePoll {
    public long poll_id;

    public String name;
    public boolean can_vote;
    public Map<String, Number> candidates;

    public Map<String, Number> getCandidates() {
        return candidates;
    }

    public void setCandidates(Map<String, Number> candidates) {
        this.candidates = candidates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCan_vote() {
        return can_vote;
    }

    public void setCan_vote(boolean can_vote) {
        this.can_vote = can_vote;
    }

    public long getPoll_id() {
        return poll_id;
    }

    public void setPoll_id(long poll_id) {
        this.poll_id = poll_id;
    }
}
