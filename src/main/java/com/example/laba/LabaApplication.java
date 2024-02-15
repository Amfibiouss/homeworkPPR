package com.example.laba;

import com.example.laba.entities.Messages;
import com.example.laba.entities.Users;
import com.example.laba.repositories.MessagesRepository;
import com.example.laba.repositories.UsersRepository;
import com.example.laba.structures.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class LabaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabaApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UsersRepository usersRepository,
							 MessagesRepository messagesRepository) {
		return args -> {
			Users user1 = new Users(), user2 = new Users(), user3 = new Users(), user4 = new Users();
			Resource resource1 = new ClassPathResource("static/Холмс.jpg");
			Resource resource2 = new ClassPathResource("static/Уотсон.jpg");
			Resource resource3 = new ClassPathResource("static/Стэмфорд.jpg");
			Resource resource4 = new ClassPathResource("static/Админ.jpg");
			byte[] photo1 = new byte[(int)resource1.contentLength()];
			byte[] photo2 = new byte[(int)resource2.contentLength()];
			byte[] photo3 = new byte[(int)resource3.contentLength()];
			byte[] photo4 = new byte[(int)resource4.contentLength()];

			try(InputStream inputStream1 = new FileInputStream(resource1.getFile());
				InputStream inputStream2 = new FileInputStream(resource2.getFile());
				InputStream inputStream3 = new FileInputStream(resource3.getFile());
				InputStream inputStream4 = new FileInputStream(resource4.getFile())){

				inputStream1.read(photo1);
				inputStream2.read(photo2);
				inputStream3.read(photo3);
				inputStream4.read(photo4);
			} catch(IOException e) {
				System.out.println(e);
			}

			user1.setLogin("Холмс");
			user1.setPhoto(photo1);
			usersRepository.save(user1);

			user2.setLogin("Уотсон");
			user2.setPhoto(photo2);
			usersRepository.save(user2);

			user3.setLogin("Стэмфорд");
			user3.setPhoto(photo3);
			usersRepository.save(user3);

			user4.setLogin("Админ");
			user4.setPhoto(photo4);
			usersRepository.save(user4);

			Resource chat = new ClassPathResource("static/chat.json");
			ObjectMapper objectMapper = new ObjectMapper();
			Message[] messages = objectMapper.readValue(chat.getFile(), Message[].class);

			for (Message message : messages) {
				Messages mes = new Messages();
				mes.setText(message.text);
				mes.setUser(usersRepository.findByLogin(message.login).getFirst());
				messagesRepository.save(mes);
			}

			//System.out.println(messagesRepository.getReferenceById(2L).getText());
		};
	}
}
