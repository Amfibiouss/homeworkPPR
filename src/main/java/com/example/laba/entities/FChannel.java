package com.example.laba.entities;

import jakarta.persistence.*;

import java.util.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class FChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    Long cindex;
    private String name;
    @OneToMany(mappedBy="channel")
    private Set<FMessage> messages = new HashSet<>();
    @ManyToOne(fetch=LAZY)
    FRoom room;

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

    public Long getCindex() {return cindex;}

    public void setCindex(Long сindex) {this.cindex = сindex;}

    public FChannel() {}
}
