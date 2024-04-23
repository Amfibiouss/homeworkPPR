package com.example.laba.json_objects;

public class OutputRoom {
    public long id;
    public String creator;

    public String name;

    public String description;
    public String status;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OutputRoom(long id, String creator, String name, String description, String status) {
        this.id = id;
        this.creator = creator;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public OutputRoom() {}
}
