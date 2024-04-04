package com.example.laba.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

public class FStageFChannelId implements Serializable {
    private Long stage;
    private Long channel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FStageFChannelId that)) return false;
        return Objects.equals(getStage(), that.getStage()) && Objects.equals(getChannel(), that.getChannel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStage(), getChannel());
    }

    public FStageFChannelId() {}

    public FStageFChannelId(Long stage, Long channel) {
        this.stage = stage;
        this.channel = channel;
    }

    public Long getStage() {return stage;}

    public void setStage(Long stage) {
        this.stage = stage;
    }

    public Long getChannel() {
        return channel;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }
}
