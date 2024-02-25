package com.example.laba.repositories;

import com.example.laba.entities.FMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagesRepository extends JpaRepository<FMessage, Long> {
}
