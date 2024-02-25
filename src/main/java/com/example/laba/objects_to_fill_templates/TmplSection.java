package com.example.laba.objects_to_fill_templates;

public class TmplSection {
    public long id;
    public String creator;
    public String name;
    public String description;

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

    public TmplSection(long id, String creator, String name, String description) {
        this.id = id;
        this.creator = creator;
        this.name = name;
        this.description = description;
    }
}
