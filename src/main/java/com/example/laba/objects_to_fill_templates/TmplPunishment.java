package com.example.laba.objects_to_fill_templates;

import java.time.OffsetDateTime;

public class TmplPunishment {
    public long id;

    public String username;
    public String description;
    public long rule;
    public OffsetDateTime date_start;
    public OffsetDateTime date_finish;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRule() {
        return rule;
    }

    public void setRule(Long rule) {
        this.rule = rule;
    }

    public OffsetDateTime getDate_start() {
        return date_start;
    }

    public void setDate_start(OffsetDateTime date_start) {
        this.date_start = date_start;
    }

    public OffsetDateTime getDate_finish() {
        return date_finish;
    }

    public void setDate_finish(OffsetDateTime date_finish) {
        this.date_finish = date_finish;
    }
}
