package com.example.laba.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private Long read_mask;
    private Long write_mask;

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

    public FChannel() {}
}
