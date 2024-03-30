package com.example.laba.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

@Embeddable
public class FStageFChannelId implements Serializable {
    @ManyToOne(fetch=LAZY)
    FStage stage;
    @ManyToOne(fetch=LAZY)
    FChannel channel;

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
    public FStageFChannelId(FStage stage, FChannel channel) {
        this.stage = stage;
        this.channel = channel;
    }

    public FStage getStage() {
        return stage;
    }

    public void setStage(FStage stage) {
        this.stage = stage;
    }

    public FChannel getChannel() {
        return channel;
    }

    public void setChannel(FChannel channel) {
        this.channel = channel;
    }
}
