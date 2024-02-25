package com.example.laba.entities;

import jakarta.persistence.*;

import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class FMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch=LAZY)
    private FUser user;
    @ManyToOne(fetch=LAZY)
    private FSection section;
    @Column(length=10000)
    private String text;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FMessage message)) return false;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public FSection getSection() {
        return section;
    }

    public void setSection(FSection section) {
        this.section = section;
        section.getMessages().add(this);
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

    public FMessage() {
    }
}
