package com.example.laba.json_objects;

public class InputStatePoll {
    public String name;

    public long mask_voters;

    public long mask_observers;

    public long mask_candidates;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMask_voters() {
        return mask_voters;
    }

    public void setMask_voters(long mask_voters) {
        this.mask_voters = mask_voters;
    }

    public long getMask_observers() {
        return mask_observers;
    }

    public void setMask_observers(long mask_observers) {
        this.mask_observers = mask_observers;
    }

    public long getMask_candidates() {
        return mask_candidates;
    }

    public void setMask_candidates(long mask_candidates) {
        this.mask_candidates = mask_candidates;
    }
}
