package com.example.laba.repositories;

import com.example.laba.entities.FUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersRepository extends JpaRepository<FUser, Long> {
    List<FUser> findByLogin(String login);
}
