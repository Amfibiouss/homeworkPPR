package com.example.laba.entities;

import jakarta.persistence.*;

import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@IdClass(FStageFChannelId.class)
public class FStageFChannel {
    @Id
    @ManyToOne(fetch=LAZY)
    private FStage stage;
    @Id
    @ManyToOne(fetch=LAZY)
    private FChannel channel;
    @Column(columnDefinition="TEXT")
    private String jsonStrings;
    @Column(columnDefinition="TEXT")
    private String jsonXRayStrings;
    @Column(columnDefinition="TEXT")
    private String jsonAnonStrings;
    private Long count;
    private Long XRayReadMask;
    private Long ReadMask;
    private Long AnonReadMask;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FStageFChannel that)) return false;
        return Objects.equals(getStage(), that.getStage()) && Objects.equals(getChannel(), that.getChannel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStage(), getChannel());
    }

    public Long getXRayReadMask() {
        return XRayReadMask;
    }

    public void setXRayReadMask(Long XRayReadMask) {
        this.XRayReadMask = XRayReadMask;
    }

    public Long getReadMask() {
        return ReadMask;
    }

    public void setReadMask(Long readMask) {
        ReadMask = readMask;
    }

    public Long getAnonReadMask() {
        return AnonReadMask;
    }

    public void setAnonReadMask(Long anonReadMask) {
        AnonReadMask = anonReadMask;
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getJsonStrings() {
        return jsonStrings;
    }

    public void setJsonStrings(String jsonStrings) {
        this.jsonStrings = jsonStrings;
    }

    public String getJsonXRayStrings() {
        return jsonXRayStrings;
    }

    public void setJsonXRayStrings(String jsonXRayStrings) {
        this.jsonXRayStrings = jsonXRayStrings;
    }

    public String getJsonAnonStrings() {
        return jsonAnonStrings;
    }

    public void setJsonAnonStrings(String jsonAnonStrings) {
        this.jsonAnonStrings = jsonAnonStrings;
    }
}
