package com.example.laba.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
public class FPunishment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FUser user;
    private String description;
    private Long rule;
    private OffsetDateTime date_start;
    private OffsetDateTime date_finish;
    private Boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FPunishment that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FUser getUser() {
        return user;
    }

    public void setUser(FUser user) {
        this.user = user;
        user.getPunishments().add(this);
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
