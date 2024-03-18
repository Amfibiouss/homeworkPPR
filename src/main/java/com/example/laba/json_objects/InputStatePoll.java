package com.example.laba.json_objects;

public class InputStatePoll {
    public String name;

    public long mask_vote;

    public long mask_view;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMask_vote() {
        return mask_vote;
    }

    public void setMask_vote(long mask_vote) {
        this.mask_vote = mask_vote;
    }

    public long getMask_view() {
        return mask_view;
    }

    public void setMask_view(long mask_view) {
        this.mask_view = mask_view;
    }
}
