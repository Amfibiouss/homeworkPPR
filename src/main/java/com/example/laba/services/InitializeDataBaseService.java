package com.example.laba.services;

import com.example.laba.entities.FMessage;
import com.example.laba.entities.FChannel;
import com.example.laba.entities.FRoom;
import com.example.laba.entities.FUser;
import com.example.laba.json_objects.OutputMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.PersistenceUnit;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;

@Component
public class InitializeDataBaseService {

    @Autowired
    HandleImageService handleImageService;

    @PersistenceUnit
    SessionFactory sessionFactory;

    @Transactional
    public void initialize() throws IOException {
        String[] login = {"Holmes", "Watson", "Stamford", "Admin"};
        String[] password = {"1", "2", "3", "4"};
        String[] photo_path = {"static/Холмс.jpg", "static/Уотсон.jpg", "static/Стэмфорд.jpg", "static/Админ.jpg"};
        String[] description = {
                "Люблю брейкданс, китайскую оперу и разводить кенгуру.",
                "Люблю брейкданс, китайскую оперу и разводить кенгуру.",
                "Люблю брейкданс, китайскую оперу и разводить кенгуру.",
                "Админ всегда прав."
        };
        String[] sex = {"мужской", "мужской", "мужской", "мужской"};
        boolean[] admin = {false, false, false, true};

        Session session = sessionFactory.getCurrentSession();

        for (int i = 0; i < 4; i++) {
            FUser user = new FUser();

            Resource resource = new ClassPathResource(photo_path[i]);
            byte[] photo = new byte[(int)resource.contentLength()];

            try(InputStream inputStream1 = new FileInputStream(resource.getFile());){
                inputStream1.read(photo);
            } catch(IOException e) {
                System.out.println(e);
            }

            user.setLogin(login[i]);
            user.setPhoto(handleImageService.cropping_scaling(photo));
            user.setPassword(DigestUtils.sha256Hex(password[i]));
            user.setAdmin(admin[i]);
            user.setSex(sex[i]);
            user.setDescription(description[i]);
            session.persist(user);
        }

        for (int i = 1; i <= 4; i++) {
            FRoom room = new FRoom();
            room.setName("Этюд в багровых тонах");
            room.setDescription("Повесть Артура Конан Дойла 'Этюд в багровых тонах'");
            room.setCreator(session.get(FUser.class, i));
            room.setStatus("not started");

            FChannel channel_lobby = new FChannel();
            channel_lobby.setName("лобби");
            session.persist(channel_lobby);

            room.addChannel(channel_lobby);

            session.persist(room);
        }

        Resource chat = new ClassPathResource("static/chat.json");
        ObjectMapper objectMapper = new ObjectMapper();
        OutputMessage[] messages = objectMapper.readValue(chat.getFile(), OutputMessage[].class);

        for (OutputMessage message : messages) {
            FMessage mes = new FMessage();
            mes.setChannel(session.getReference(FChannel.class, 1));
            mes.setText(message.text);
            mes.setUser(session.bySimpleNaturalId(FUser.class).load(message.username));
            session.persist(mes);
        }
    }
}
