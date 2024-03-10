package com.example.laba.services;

import com.example.laba.entities.FMessage;
import com.example.laba.entities.FPunishment;
import com.example.laba.entities.FSection;
import com.example.laba.entities.FUser;
import com.example.laba.json_objects.OutputMessage;
import com.example.laba.objects_to_fill_templates.TmplPunishment;
import com.example.laba.objects_to_fill_templates.TmplSection;
import com.example.laba.objects_to_fill_templates.TmplUser;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class OverturningTheEarthAndTramplingTheHeavensDAOService {

    @Autowired
    CatMaidService catMaidService;

    @PersistenceUnit
    SessionFactory sessionFactory;

    @Transactional
    public TmplUser get_user_by_login(String login) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(login);

        if (user == null)
            throw new ServiceException("the login don't exist.");

        return new TmplUser(user, catMaidService.getUwUDegree(user.getDate_UwU()));
    }

    @Transactional
    public String get_password(String login) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(login);

        if (user == null)
            throw new ServiceException("the login don't exist.");

        return user.getPassword();
    }

    @Transactional
    public byte[] get_photo(String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user =  session.bySimpleNaturalId(FUser.class).load(username);

        if (user == null)
            throw new ServiceException("the user_id don't exist.");

        return user.getPhoto();
    }

    @Transactional
    public List<OutputMessage> get_messages(long section_id, long offset, long limit) {
        Session session = sessionFactory.getCurrentSession();
        FSection section = session.get(FSection.class, section_id);

        if (section == null)
            throw new ServiceException("the section_id don't exist.");

        List <FMessage> messages =
                session.createSelectionQuery("from FMessage m join fetch m.user where m.section.id=:section_id order by m.id limit :limit offset :offset", FMessage.class)
                        .setParameter("section_id", section_id)
                        .setParameter("offset", offset)
                        .setParameter("limit", limit)
                        .getResultList();

        List<OutputMessage> result = new ArrayList<>();

        for (FMessage message : messages) {
            result.add(new OutputMessage(
                    message.getUser().getLogin(),
                    message.getText(),
                    catMaidService.getUwUDegree(message.getUser().getDate_UwU())));
        }

        return result;
    }

    @Transactional
    public List<TmplSection> get_sections() {
        Session session = sessionFactory.getCurrentSession();

        List <FSection> sections = session.createSelectionQuery("from FSection s join fetch s.creator", FSection.class).getResultList();

        List<TmplSection> result = new ArrayList<>();

        for (FSection section : sections) {
            result.add(new TmplSection(section.getId(), section.getCreator().getLogin(), section.getName(), section.getDescription()));
        }

        return result;
    }

    @Transactional
    public void add_user(String username, String password, boolean admin) {
        FUser user = new FUser();
        user.setLogin(username);
        user.setPassword(password);
        user.setAdmin(admin);
        user.setSex("мужской");

        Session session = sessionFactory.getCurrentSession();
        session.persist(user);
    }

    @Transactional
    public void update_user(String username, String sex, String description, byte[] photo) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        user.setSex(sex);
        user.setDescription(description);

        if (photo.length > 0)
            user.setPhoto(photo);
    }

    @Transactional
    public void add_section(TmplSection section) {
        FSection new_section = new FSection();
        new_section.setDescription(section.description);
        new_section.setName(section.name);
        new_section.setMessages(new HashSet<>());

        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(section.creator);

        if (user == null)
            throw new ServiceException("the user_id don't exist.");

        new_section.setCreator(user);

        session.persist(new_section);
    }

    @Transactional
    public void add_message(OutputMessage message, long section_id) {
        FMessage new_message = new FMessage();
        new_message.setText(message.text);

        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(message.username);
        FSection section = session.get(FSection.class, section_id);

        if (user == null)
            throw new ServiceException("the user_id don't exist.");

        if (section == null)
            throw new ServiceException("the section_id don't exist.");

        new_message.setSection(section);
        new_message.setUser(user);

        session.persist(new_message);
    }

    @Transactional
    public void update_punishments_status(String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        session.createMutationQuery(
                "update FPunishment p set p.active=false " +
                        "where p.user = :user and p.active = true and p.date_finish < :time_now")
                .setParameter("user", user)
                .setParameter("time_now", OffsetDateTime.now()).executeUpdate();
    }

    @Transactional
    public void try_punish(String username, long rule, String description) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        if (user == null)
            throw new ServiceException("the user_id don't exist.");

        FPunishment punishment = session.createSelectionQuery(
                "from FPunishment p where p.user = :user and p.rule = :rule and p.active = true", FPunishment.class)
                .setParameter("user", user)
                .setParameter("rule", rule)
                .getSingleResultOrNull();

        if (punishment != null)
            return;

        punishment = new FPunishment();
        punishment.setDate_start(OffsetDateTime.now());
        punishment.setDate_finish(punishment.getDate_start().plusSeconds(30));
        //punishment.setDate_finish(punishment.getDate_start().plusDays(1));
        punishment.setRule(rule);
        punishment.setDescription(description);
        punishment.setUser(user);
        punishment.setActive(true);

        session.persist(punishment);
    }

    @Transactional
    public void try_forgive(String username, long rule) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        if (user == null)
            throw new ServiceException("the user_id don't exist.");

        FPunishment punishment = session.createSelectionQuery(
                        "from FPunishment p where p.user = :user and p.rule = :rule and p.active = true", FPunishment.class)
                .setParameter("user", user)
                .setParameter("rule", rule)
                .getSingleResultOrNull();

        if (punishment != null) {
            session.remove(punishment);
        }
    }

    @Transactional
    public List<TmplPunishment> get_punishments(String username, boolean active) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        List<TmplPunishment> punishments = new ArrayList<>();

        for (FPunishment punishment : user.getPunishments()) {

            if (punishment.getActive() == active) {

                TmplPunishment punishment1 = new TmplPunishment();

                punishment1.setUsername(username);
                punishment1.setId(punishment.getId());
                punishment1.setDescription(punishment.getDescription());
                punishment1.setDate_start(punishment.getDate_start());
                punishment1.setDate_finish(punishment.getDate_finish());
                punishment1.setRule(punishment.getRule());

                punishments.add(punishment1);
            }
        }

        return punishments;
    }

    @Transactional
    public boolean has_ban(String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        FPunishment punishment = session.createSelectionQuery(
                        "from FPunishment p where p.user = :user and p.active = true", FPunishment.class)
                .setParameter("user", user)
                .getSingleResultOrNull();

        return (punishment != null);
    }

    @Transactional
    public void punish_UwU(String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        if (user.getDate_UwU() == null)
            user.setDate_UwU(OffsetDateTime.now());
    }

    @Transactional
    public void forgive_UwU(String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        user.setDate_UwU(null);
    }

    @Transactional
    public boolean has_UwU(String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        if (user.getDate_UwU() != null) {
            return true;
        }

        return false;
    }
}
