package com.example.laba.entities;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;

@Entity
public class FSection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(mappedBy="section")
    private Set<FMessage> messages;
    @ManyToOne(fetch=LAZY)
    private FUser creator;
    private String name;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FSection section)) return false;
        return Objects.equals(id, section.id);
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

    public FUser getCreator() {
        return creator;
    }

    public void setCreator(FUser creator) {
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

    public FSection() {}
}
