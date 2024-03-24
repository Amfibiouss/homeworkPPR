package com.example.laba.json_objects;

public class InputStateChannel {
    public String name;
    public Long read_real_username_mask;
    public Long read_mask;
    public Long anon_read_mask;
    public Long write_mask;
    public Long anon_write_mask;

    public Long getRead_real_username_mask() {
        return read_real_username_mask;
    }

    public void setRead_real_username_mask(Long read_real_username_mask) {
        this.read_real_username_mask = read_real_username_mask;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRead_mask() {
        return read_mask;
    }

    public void setRead_mask(Long read_mask) {
        this.read_mask = read_mask;
    }

    public Long getWrite_mask() {
        return write_mask;
    }

    public void setWrite_mask(Long write_mask) {
        this.write_mask = write_mask;
    }

    public Long getAnon_write_mask() {
        return anon_write_mask;
    }

    public void setAnon_write_mask(Long anon_write_mask) {
        this.anon_write_mask = anon_write_mask;
    }

    public Long getAnon_read_mask() {
        return anon_read_mask;
    }

    public void setAnon_read_mask(Long anon_read_mask) {
        this.anon_read_mask = anon_read_mask;
    }
}
