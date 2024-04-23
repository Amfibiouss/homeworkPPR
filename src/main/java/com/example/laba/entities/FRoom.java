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
    @Column(columnDefinition = "TEXT")
    private String description;
    private String mode;
    private OffsetDateTime start_time;
    private OffsetDateTime finish_time;
    private Long min_players;
    private Long max_players;
    @Column(columnDefinition = "TEXT")
    private String code;
    private String status;
    private OffsetDateTime finish_stage;
    @OneToMany(mappedBy="room")
    private List<FChannel> channels = new ArrayList<>();

    @OneToMany(mappedBy="room")
    private List<FPoll> polls = new ArrayList<>();

    @OneToMany(mappedBy="room")
    private Set<FStage> stages = new HashSet<>();

    @OneToMany(mappedBy="room")
    private Set<FCharacter> characters = new HashSet<>();

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

    public Set<FStage> getStages() {
        return stages;
    }

    public void setStages(Set<FStage> stages) {
        this.stages = stages;
    }

    public void addStage(FStage stage) {
        stage.setRoom(this);
        stages.add(stage);
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OffsetDateTime getFinish_stage() {
        return finish_stage;
    }

    public void setFinish_stage(OffsetDateTime finish_stage) {
        this.finish_stage = finish_stage;
    }

    public List<FPoll> getPolls() {
        return polls;
    }

    public void setPolls(List<FPoll> polls) {
        this.polls = polls;
    }

    public void addPoll(FPoll poll) {
        poll.setRoom(this);
        polls.add(poll);
    }

    public Set<FCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(Set<FCharacter> characters) {
        this.characters = characters;
    }

    public void addCharacter(FCharacter character) {
        character.setRoom(this);
        characters.add(character);
    }
}
