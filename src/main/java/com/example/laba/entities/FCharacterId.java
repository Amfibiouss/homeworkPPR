package com.example.laba.entities;

import java.io.Serializable;
import java.util.Objects;

public class FCharacterId implements Serializable {
    private Long room;
    private Long pindex;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FCharacterId that)) return false;
        return Objects.equals(getRoom(), that.getRoom()) && Objects.equals(getPindex(), that.getPindex());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoom(), getPindex());
    }

    public FCharacterId(Long room, Long pindex) {
        this.room = room;
        this.pindex = pindex;
    }

    public FCharacterId() {
    }

    public Long getRoom() {
        return room;
    }

    public void setRoom(Long room) {
        this.room = room;
    }

    public Long getPindex() {
        return pindex;
    }

    public void setPindex(Long pindex) {
        this.pindex = pindex;
    }
}
