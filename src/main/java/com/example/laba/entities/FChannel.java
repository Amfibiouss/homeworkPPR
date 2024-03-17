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

    public FChannel() {}
}
