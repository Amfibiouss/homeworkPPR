package com.example.laba.json_objects;

import java.time.OffsetDateTime;
import java.util.List;

public class OutputStateRoom {

    public String status;

    public List<OutputStateChannel> channels;

    public List<OutputStatePoll> polls;

    public long channelReadMask;
    public long channelAnonReadMask;
    public long channelXRayReadMask;
    public long channelWriteMask;
    public long channelAnonWriteMask;
    public long pollVoteMask;
    public OffsetDateTime finish_stage;
    public String stage;
    public Long stage_id;
    public OutputMessage newspaperMessage;

    public OutputMessage getNewspaperMessage() {
        return newspaperMessage;
    }

    public void setNewspaperMessage(OutputMessage newspaperMessage) {
        this.newspaperMessage = newspaperMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OutputStateChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<OutputStateChannel> channels) {
        this.channels = channels;
    }

    public List<OutputStatePoll> getPolls() {
        return polls;
    }

    public void setPolls(List<OutputStatePoll> polls) {
        this.polls = polls;
    }

    public OffsetDateTime getFinish_stage() {
        return finish_stage;
    }

    public void setFinish_stage(OffsetDateTime finish_stage) {
        this.finish_stage = finish_stage;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Long getStage_id() {
        return stage_id;
    }

    public void setStage_id(Long stage_id) {
        this.stage_id = stage_id;
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
}
