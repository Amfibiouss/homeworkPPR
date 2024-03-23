package com.example.laba.entities;

import jakarta.persistence.Entity;

@Entity
public class FTargetedMessage extends FMessage{
    Long target;
}
