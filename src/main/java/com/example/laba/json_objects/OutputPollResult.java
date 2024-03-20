package com.example.laba.json_objects;

public class OutputPollResult {
    public String name;

    public long[] poll_table;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long[] getPoll_table() {
        return poll_table;
    }

    public void setPoll_table(long[] poll_table) {
        this.poll_table = poll_table;
    }
}
