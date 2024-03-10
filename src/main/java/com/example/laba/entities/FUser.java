package com.example.laba.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.NaturalId;

import static org.hibernate.Length.LONG32;

@Entity
public class FUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NaturalId
    private String login;
    private String password;
    @Column(length=LONG32, columnDefinition="BLOB")
    private byte[] photo;
    private Boolean Admin;
    private String description;
    private String sex;
    @OneToMany(mappedBy="user")
    private Set<FPunishment> punishments = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FUser fUser)) return false;
        return Objects.equals(id, fUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Boolean  getAdmin() {
        return Admin;
    }
    public void setAdmin(Boolean admin) {
        Admin = admin;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Set<FPunishment> getPunishments() {
        return punishments;
    }

    public void setPunishments(Set<FPunishment> punishments) {
        this.punishments = punishments;
    }

    public FUser() {
    }
}
