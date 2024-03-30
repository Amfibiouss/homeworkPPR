package com.example.laba.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class FStageFChannel {
    @EmbeddedId
    FStageFChannelId id;
    @Column(columnDefinition="TEXT")
    String jsonStrings;
    @Column(columnDefinition="TEXT")
    String jsonXRayStrings;
    @Column(columnDefinition="TEXT")
    String jsonAnonStrings;
    Long count;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FStageFChannel that)) return false;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public FStageFChannelId getId() {
        return id;
    }

    public void setId(FStageFChannelId id) {
        this.id = id;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getJsonStrings() {
        return jsonStrings;
    }

    public void setJsonStrings(String jsonStrings) {
        this.jsonStrings = jsonStrings;
    }

    public String getJsonXRayStrings() {
        return jsonXRayStrings;
    }

    public void setJsonXRayStrings(String jsonXRayStrings) {
        this.jsonXRayStrings = jsonXRayStrings;
    }

    public String getJsonAnonStrings() {
        return jsonAnonStrings;
    }

    public void setJsonAnonStrings(String jsonAnonStrings) {
        this.jsonAnonStrings = jsonAnonStrings;
    }
}
