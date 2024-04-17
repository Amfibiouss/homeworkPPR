package com.example.laba.json_objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

public class InputStatePoll {
    public String name;
    public long mask_voters;
    public long mask_observers;
    public long mask_candidates;
    public Long maxSelection;
    public Long minSelection;
    public Map<String, Long> candidates;

    public String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(Long maxSelection) {
        this.maxSelection = maxSelection;
    }

    public Long getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(Long minSelection) {
        this.minSelection = minSelection;
    }

    public Map<String, Long> getCandidates() {
        return candidates;
    }

    public void setCandidates(Map<String, Long> candidates) {
        this.candidates = candidates;
    }

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
