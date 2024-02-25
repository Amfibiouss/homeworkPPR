package com.example.laba.services;

import com.example.laba.entities.FMessage;
import com.example.laba.entities.FSection;
import com.example.laba.entities.FUser;
import com.example.laba.objects_to_fill_templates.TmplMessage;
import com.example.laba.repositories.MessagesRepository;
import com.example.laba.repositories.SectionsRepository;
import com.example.laba.repositories.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

@Component
public class InitializeDataBaseService {
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    MessagesRepository messagesRepository;
    @Autowired
    SectionsRepository sectionsRepository;

    @Transactional
    public void initialize() throws IOException {
        FUser user1 = new FUser(), user2 = new FUser(), user3 = new FUser(), user4 = new FUser();
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
        user1.setPassword("1");
        user1.setAdmin(false);
        usersRepository.save(user1);

        user2.setLogin("Уотсон");
        user2.setPhoto(photo2);
        user2.setPassword("2");
        user2.setAdmin(false);
        usersRepository.save(user2);

        user3.setLogin("Стэмфорд");
        user3.setPhoto(photo3);
        user3.setPassword("3");
        user3.setAdmin(false);
        usersRepository.save(user3);

        user4.setLogin("Админ");
        user4.setPhoto(photo4);
        user4.setPassword("4");
        user4.setAdmin(true);
        usersRepository.save(user4);

        for (int i = 0; i < 15; i++) {
            FSection section = new FSection();
            section.setName("Этюд в багровых тонах");
            section.setDescription("Повесть Артура Конан Дойла 'Этюд в багровых тонах'");
            section.setCreator(user4);
            section.setMessages(new HashSet<>());
            sectionsRepository.save(section);
        }

        Resource chat = new ClassPathResource("static/chat.json");
        ObjectMapper objectMapper = new ObjectMapper();
        TmplMessage[] messageForms = objectMapper.readValue(chat.getFile(), TmplMessage[].class);

        for (TmplMessage messageForm : messageForms) {
            FMessage mes = new FMessage();
            mes.setSection(sectionsRepository.getReferenceById(1L));
            mes.setText(messageForm.text);
            mes.setUser(usersRepository.findByLogin(messageForm.login).getFirst());
            messagesRepository.save(mes);
        }
    }
}
