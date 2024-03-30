package com.example.laba.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.util.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class FChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @OneToMany(mappedBy="channel")
    private Set<FMessage> messages = new HashSet<>();
    @ManyToOne(fetch=LAZY)
    FRoom room;
    private Long read_real_username_mask;
    private Long read_mask;
    private Long anon_read_mask;
    private Long write_mask;
    private Long anon_write_mask;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FChannel section)) return false;
        return Objects.equals(id, section.getId());
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

    public Set<FMessage> getMessages() {
        return messages;
    }

    public void setMessages(Set<FMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(FMessage message) {
        this.messages.add(message);
        message.setChannel(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FRoom getRoom() {
        return room;
    }

    public void setRoom(FRoom room) {
        this.room = room;
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

    public Long getRead_real_username_mask() {
        return read_real_username_mask;
    }

    public void setRead_real_username_mask(Long read_real_username_mask) {
        this.read_real_username_mask = read_real_username_mask;
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

    public FChannel() {}
}
