package com.example.laba.json_objects;

public class InputStateCharacter {
    public long pindex;
    public String name;
    public long channelReadMask;
    public long channelAnonReadMask;
    public long channelXRayReadMask;
    public long channelWriteMask;
    public long channelAnonWriteMask;
    public long pollVoteMask;
    public long pollObserveMask;

    public long getPindex() {
        return pindex;
    }

    public void setPindex(long pindex) {
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
