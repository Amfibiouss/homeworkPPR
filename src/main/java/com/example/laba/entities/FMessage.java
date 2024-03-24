package com.example.laba.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

@Entity
public class FMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch=LAZY)
    private FUser user;
    private String alias;
    @ManyToOne(fetch=LAZY)
    private FChannel channel;
    @Column(length=10000)
    private String text;
    private OffsetDateTime date;
    private Long target;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FMessage message)) return false;
        return Objects.equals(id, message.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public FChannel getChannel() {
        return channel;
    }

    public void setChannel(FChannel channel) {
        this.channel = channel;
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
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public FMessage() {
    }
}
