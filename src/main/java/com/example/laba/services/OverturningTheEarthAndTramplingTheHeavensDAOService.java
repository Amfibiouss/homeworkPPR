package com.example.laba.services;

import com.example.laba.entities.*;
import com.example.laba.objects_to_fill_templates.TmplPunishment;
import com.example.laba.objects_to_fill_templates.TmplUser;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

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
    public TmplUser get_user_by_email(String email) {
        Session session = sessionFactory.getCurrentSession();

        FUser user = session.createSelectionQuery(
                "from FUser u where u.email=:email", FUser.class)
                        .setParameter("email", email)
                        .getSingleResult();

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
    public void update_password(String username, String password) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        user.setPassword(password);
    }

    @Transactional
    public void add_user(String username, String password, String email, String sex, boolean admin) {
        FUser user = new FUser();
        user.setLogin(username);
        user.setPassword(password);
        user.setAdmin(admin);
        user.setSex(sex);
        user.setEmail(email);

        Session session = sessionFactory.getCurrentSession();
        session.persist(user);
    }

    @Transactional
    public void update_user(String username, String sex, String description, byte[] photo) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        user.setSex(sex);
        user.setDescription(description);

        if (photo.length > 0) {
            user.setPhoto(photo);
            user.setPhoto_eTag(System.currentTimeMillis());
        }
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

        List<FPunishment> punishments = session.createSelectionQuery(
                        "from FPunishment p where p.user = :user and p.active = true", FPunishment.class)
                .setParameter("user", user)
                .getResultList();

        return !punishments.isEmpty();
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


        return user.getDate_UwU() != null;
    }

    @Transactional
    public long get_eTag(String username) {
        Session session = sessionFactory.getCurrentSession();
        return session.bySimpleNaturalId(FUser.class).load(username).getPhoto_eTag();
    }
}
