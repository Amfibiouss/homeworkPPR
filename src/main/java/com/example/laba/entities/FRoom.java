package com.example.laba.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class FRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch=LAZY)
    private FUser creator;
    private String name;
    private String description;
    private String status;
    private OffsetDateTime start_time;
    private OffsetDateTime finish_time;
    private Long min_players;
    private Long max_players;
    private String init_code;
    private String handle_code;

    @OneToMany(mappedBy="room")
    private List<FChannel> channels = new ArrayList<>();

    @OneToMany(mappedBy="room")
    private List<FUser> players = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FRoom fRoom)) return false;
        return Objects.equals(id, fRoom.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FUser getCreator() {
        return creator;
    }

    public void setCreator(FUser creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<FChannel> channels) {
        this.channels = channels;
    }

    public void addChannel(FChannel channel) {
        channel.setRoom(this);
        channels.add(channel);
    }
    public List<FUser> getPlayers() {
        return players;
    }

    public void setPlayers(List<FUser> players) {
        this.players = players;
    }

    public void addPlayer(FUser player) {
        this.players.add(player);
        player.setRoom(this);
    }
    public void removePlayer(FUser player) {
        this.players.remove(player);
        player.setRoom(null);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(OffsetDateTime start_time) {
        this.start_time = start_time;
    }

    public OffsetDateTime getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(OffsetDateTime finish_time) {
        this.finish_time = finish_time;
    }

    public Long getMin_players() {
        return min_players;
    }

    public void setMin_players(Long min_players) {
        this.min_players = min_players;
    }

    public Long getMax_players() {
        return max_players;
    }

    public void setMax_players(Long max_players) {
        this.max_players = max_players;
    }

    public String getInit_code() {
        return init_code;
    }

    public void setInit_code(String init_code) {
        this.init_code = init_code;
    }

    public String getHandle_code() {
        return handle_code;
    }

    public void setHandle_code(String handle_code) {
        this.handle_code = handle_code;
    }
}
