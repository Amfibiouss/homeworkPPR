package com.example.laba.entities;

import jakarta.persistence.*;

import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@IdClass(FCharacterId.class)
public class FCharacter {
    @Id
    @ManyToOne(fetch=LAZY)
    private FRoom room;
    @Id
    private Long pindex;
    private String name;
    private long channelReadMask;
    private long channelAnonReadMask;
    private long channelXRayReadMask;
    private long channelWriteMask;
    private long channelAnonWriteMask;
    private long pollVoteMask;
    private long pollObserveMask;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FCharacter that)) return false;
        return Objects.equals(getRoom(), that.getRoom()) && Objects.equals(getPindex(), that.getPindex());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoom(), getPindex());
    }

    public FRoom getRoom() {
        return room;
    }

    public void setRoom(FRoom room) {
        this.room = room;
    }

    public Long getPindex() {
        return pindex;
    }

    public void setPindex(Long pindex) {
        this.pindex = pindex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getChannelReadMask() {
        return channelReadMask;
    }

    public void setChannelReadMask(long channelReadMask) {
        this.channelReadMask = channelReadMask;
    }

    public long getChannelAnonReadMask() {
        return channelAnonReadMask;
    }

    public void setChannelAnonReadMask(long channelAnonReadMask) {
        this.channelAnonReadMask = channelAnonReadMask;
    }

    public long getChannelXRayReadMask() {
        return channelXRayReadMask;
    }

    public void setChannelXRayReadMask(long channelXRayReadMask) {
        this.channelXRayReadMask = channelXRayReadMask;
    }

    public long getChannelWriteMask() {
        return channelWriteMask;
    }

    public void setChannelWriteMask(long channelWriteMask) {
        this.channelWriteMask = channelWriteMask;
    }

    public long getChannelAnonWriteMask() {
        return channelAnonWriteMask;
    }

    public void setChannelAnonWriteMask(long channelAnonWriteMask) {
        this.channelAnonWriteMask = channelAnonWriteMask;
    }

    public long getPollVoteMask() {
        return pollVoteMask;
    }

    public void setPollVoteMask(long pollVoteMask) {
        this.pollVoteMask = pollVoteMask;
    }

    public long getPollObserveMask() {
        return pollObserveMask;
    }

    public void setPollObserveMask(long pollObserveMask) {
        this.pollObserveMask = pollObserveMask;
    }
}
