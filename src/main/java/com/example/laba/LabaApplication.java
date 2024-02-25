package com.example.laba;

import com.example.laba.entities.FMessage;
import com.example.laba.entities.FSection;
import com.example.laba.entities.FUser;
import com.example.laba.repositories.MessagesRepository;
import com.example.laba.repositories.SectionsRepository;
import com.example.laba.repositories.UsersRepository;
import com.example.laba.objects_to_fill_templates.TmplMessage;
import com.example.laba.services.InitializeDataBaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;

@EnableTransactionManagement
@SpringBootApplication
public class LabaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabaApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(InitializeDataBaseService initializeDataBaseService) {

		return args -> {
			initializeDataBaseService.initialize();
		};
	}
}
