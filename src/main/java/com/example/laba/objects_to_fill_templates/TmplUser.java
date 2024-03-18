package com.example.laba.objects_to_fill_templates;

import com.example.laba.entities.FUser;

import java.time.OffsetDateTime;

public class TmplUser {
    public long id;
    public String login;
    public boolean Admin;

    public String description;
    public String sex;
    public double degreeUwU;
    public  String email;
    public long player_index;

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

    public Boolean getAdmin() {
        return Admin;
    }

    public void setAdmin(Boolean admin) {
        Admin = admin;
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

    public double getDegreeUwU() {
        return degreeUwU;
    }

    public void setDegreeUwU(double degreeUwU) {
        this.degreeUwU = degreeUwU;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPlayer_index() {
        return player_index;
    }

    public void setPlayer_index(long player_index) {
        this.player_index = player_index;
    }

    public TmplUser(FUser user, double degree) {
        this.Admin = user.getAdmin();
        this.login = user.getLogin();
        this.sex = user.getSex();
        this.description = user.getDescription();
        this.id = user.getId();
        this.degreeUwU = degree;
        this.email = user.getEmail();
        this.player_index = user.getPlayer_index();
    }
    public TmplUser(){}
}
