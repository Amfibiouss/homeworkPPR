package com.example.laba.objects_to_fill_templates;

import com.example.laba.entities.FUser;

public class TmplUser {
    public long id;
    public String login;
    public boolean Admin;
    public String description;
    public String sex;

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

    public TmplUser(FUser user) {
        this.Admin = user.getAdmin();
        this.login = user.getLogin();
        this.sex = user.getSex();
        this.description = user.getDescription();
        this.id = user.getId();
    }
    public TmplUser(){}
}
