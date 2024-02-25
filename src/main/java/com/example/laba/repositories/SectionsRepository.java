package com.example.laba.repositories;

import com.example.laba.entities.FSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionsRepository extends JpaRepository<FSection, Long> {
}
