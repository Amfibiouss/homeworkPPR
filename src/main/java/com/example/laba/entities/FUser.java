package com.example.laba.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
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
    @Basic(fetch=FetchType.LAZY)
    private byte[] photo;
    long photo_eTag;
    private Boolean Admin;
    private String description;
    private String sex;
    private OffsetDateTime date_UwU;
    private String email;

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

    public OffsetDateTime getDate_UwU() {
        return date_UwU;
    }

    public void setDate_UwU(OffsetDateTime date_UwU) {
        this.date_UwU = date_UwU;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhoto_eTag() {
        return photo_eTag;
    }

    public void setPhoto_eTag(long photo_eTag) {
        this.photo_eTag = photo_eTag;
    }

    public FUser() {
    }
}
