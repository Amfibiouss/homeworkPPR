package com.example.laba.json_objects;

public class InputStateChannel {
    public String name;
    public long read_mask;
    public long write_mask;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRead_mask() {
        return read_mask;
    }

    public void setRead_mask(long read_mask) {
        this.read_mask = read_mask;
    }

    public long getWrite_mask() {
        return write_mask;
    }

    public void setWrite_mask(long write_mask) {
        this.write_mask = write_mask;
    }
}
