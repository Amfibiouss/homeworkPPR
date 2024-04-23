package com.example.laba.json_objects;

import java.util.List;

public class OutputRooms {
    List<OutputRoom> rooms;
    OutputRoom current_room;

    public OutputRooms(List<OutputRoom> rooms, OutputRoom current_room) {
        this.rooms = rooms;
        this.current_room = current_room;
    }

    public OutputRooms() {
    }

    public List<OutputRoom> getRooms() {
        return rooms;
    }

    public void setRooms(List<OutputRoom> rooms) {
        this.rooms = rooms;
    }

    public OutputRoom getCurrent_room() {
        return current_room;
    }

    public void setCurrent_room(OutputRoom current_room) {
        this.current_room = current_room;
    }
}
