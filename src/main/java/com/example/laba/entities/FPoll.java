package com.example.laba.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Array;

import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class FPoll {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    @ManyToOne(fetch=LAZY)
    private FRoom room;
    @Array(length=30)
    private long[] poll_table = new long[30];
    private Long mask_candidates;
    @Column(columnDefinition="TEXT")
    private String candidates;
    private long maxSelection;
    private long minSelection;
    private long lindex;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FPoll fPoll)) return false;
        return Objects.equals(getId(), fPoll.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public String getCandidates() {
        return candidates;
    }

    public void setCandidates(String candidates) {
        this.candidates = candidates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FRoom getRoom() {
        return room;
    }

    public void setRoom(FRoom room) {
        this.room = room;
    }

    public long[] getPoll_table() {
        return poll_table;
    }

    public void setPoll_table(long[] poll_table) {
        this.poll_table = poll_table;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMask_candidates() {
        return mask_candidates;
    }
    public void setMask_candidates(Long mask_candidates) {
        this.mask_candidates = mask_candidates;
    }

    public long getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(long maxSelection) {
        this.maxSelection = maxSelection;
    }

    public long getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(long minSelection) {
        this.minSelection = minSelection;
    }

    public long getLindex() {
        return lindex;
    }

    public void setLindex(long lindex) {
        this.lindex = lindex;
    }
}
