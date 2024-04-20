package com.example.laba.entities;

import jakarta.persistence.Embeddable;

import java.time.OffsetDateTime;

@Embeddable
public class FUwUPunishment {
    private OffsetDateTime dateUwU;
    private boolean active;
    private String UwUDescription;

    public FUwUPunishment(OffsetDateTime dateUwU, boolean active, String UwUDescription) {
        this.dateUwU = dateUwU;
        this.active = active;
        this.UwUDescription = UwUDescription;
    }

    public FUwUPunishment() {}

    public OffsetDateTime getDateUwU() {
        return dateUwU;
    }

    public void setDateUwU(OffsetDateTime dateUwU) {
        this.dateUwU = dateUwU;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUwUDescription() {
        return UwUDescription;
    }

    public void setUwUDescription(String description) {
        this.UwUDescription = description;
    }
}
