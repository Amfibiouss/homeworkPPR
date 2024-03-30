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
    private Long target;
    @ManyToOne(fetch=LAZY)
    private FChannel channel;
    @ManyToOne(fetch=LAZY)
    private FStage stage;
    private OffsetDateTime date;
    @Column(columnDefinition="TEXT")
    private String text;
    @Column(columnDefinition="TEXT")
    String jsonString;
    @Column(columnDefinition="TEXT")
    String jsonAnonString;
    @Column(columnDefinition="TEXT")
    String jsonXRayString;

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

    public String getJsonString() {
        return jsonString;
    }

    public FStage getStage() {
        return stage;
    }

    public void setStage(FStage stage) {
        this.stage = stage;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getJsonAnonString() {
        return jsonAnonString;
    }

    public void setJsonAnonString(String jsonAnonString) {
        this.jsonAnonString = jsonAnonString;
    }

    public String getJsonXRayString() {
        return jsonXRayString;
    }

    public void setJsonXRayString(String jsonXRayString) {
        this.jsonXRayString = jsonXRayString;
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
