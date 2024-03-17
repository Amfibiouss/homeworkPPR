package com.example.laba.objects_to_fill_templates;

public class TmplChannel {
    public long id;

    public String name;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TmplChannel(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
