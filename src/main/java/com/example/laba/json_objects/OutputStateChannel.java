package com.example.laba.json_objects;

public class OutputStateChannel {
    public long channel_id;

    public String name;
    boolean can_write;
    boolean can_read;

    public long getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(long channel_id) {
        this.channel_id = channel_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getCan_write() {
        return can_write;
    }

    public void setCan_write(boolean can_write) {
        this.can_write = can_write;
    }

    public boolean getCan_read() {
        return can_read;
    }

    public void setCan_read(boolean can_read) {
        this.can_read = can_read;
    }
}
