package com.example.laba.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.NaturalId;

import static jakarta.persistence.FetchType.LAZY;
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
    private FUwUPunishment UwUPunishment;
    private String email;
    @ManyToOne(fetch=LAZY)
    FCharacter character;
    private Long desired_room_id;
    private String sessionId;
    @Column(columnDefinition = "TEXT")
    private String recentRoom;

    @OneToMany(mappedBy="user")
    private Set<FPunishment> punishments = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FUser fUser)) return false;
        return Objects.equals(id, fUser.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getDesired_room_id() {
        return desired_room_id;
    }

    public void setDesired_room_id(Long desired_room_id) {
        this.desired_room_id = desired_room_id;
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

    public FUwUPunishment getUwUPunishment() {
        return UwUPunishment;
    }

    public void setUwUPunishment(FUwUPunishment uwUPunishment) {
        UwUPunishment = uwUPunishment;
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

    public FCharacter getCharacter() {
        return character;
    }

    public void setCharacter(FCharacter character) {
        this.character = character;
    }

    public String getRecentRoom() {
        return recentRoom;
    }

    public void setRecentRoom(String recent_room) {
        this.recentRoom = recent_room;
    }

    public FUser() {
    }
}
