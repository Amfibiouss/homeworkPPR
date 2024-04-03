package com.example.laba.entities;

import jakarta.persistence.*;

import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@IdClass(FCharacterId.class)
public class FCharacter {
    @Id
    @ManyToOne(fetch=LAZY)
    FRoom room;
    @Id
    Long pindex;
    String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FCharacter that)) return false;
        return Objects.equals(getRoom(), that.getRoom()) && Objects.equals(getPindex(), that.getPindex());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoom(), getPindex());
    }

    public FRoom getRoom() {
        return room;
    }

    public void setRoom(FRoom room) {
        this.room = room;
    }

    public Long getPindex() {
        return pindex;
    }

    public void setPindex(Long pindex) {
        this.pindex = pindex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
