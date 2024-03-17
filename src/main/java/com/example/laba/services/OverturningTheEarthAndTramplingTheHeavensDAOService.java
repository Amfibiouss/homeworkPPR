package com.example.laba.services;

import com.example.laba.entities.*;
import com.example.laba.json_objects.InputRoom;
import com.example.laba.json_objects.OutputMessage;
import com.example.laba.objects_to_fill_templates.TmplMessage;
import com.example.laba.objects_to_fill_templates.TmplPunishment;
import com.example.laba.objects_to_fill_templates.TmplRoom;
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
    public List<TmplMessage> get_messages_of_user(String username, long offset, long limit) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        if (user == null)
            throw new ServiceException("the user don't exist.");

        List <FMessage> messages =
                session.createSelectionQuery("from FMessage m join fetch m.channel where m.user = :user order by m.id limit :limit offset :offset", FMessage.class)
                        .setParameter("user", user)
                        .setParameter("offset", offset)
                        .setParameter("limit", limit)
                        .getResultList();

        List<TmplMessage> result = new ArrayList<>();

        for (FMessage message : messages) {
            TmplMessage tmplMessage = new TmplMessage();

            tmplMessage.setId(message.getId());
            tmplMessage.setUsername(username);
            tmplMessage.setText(message.getText());
            tmplMessage.setChannel(message.getChannel().getId());
            tmplMessage.setDate(message.getDate());

            result.add(tmplMessage);
        }

        return result;
    }

    @Transactional
    public long get_channel_id(long room_id, String name) {
        Session session = sessionFactory.getCurrentSession();
        FRoom room = session.getReference(FRoom.class, room_id);

        FChannel channel = session.createSelectionQuery(
                "from FChannel c where c.room=:room and c.name=:name", FChannel.class)
                        .setParameter("room", room)
                        .setParameter("name", name)
                        .getSingleResultOrNull();

        if (channel == null)
            throw new ServiceException("the channel name don't exist.");

        return channel.getId();
    }

    @Transactional
    public List<OutputMessage> get_messages(long channel_id, long offset, long limit) {
        Session session = sessionFactory.getCurrentSession();
        FChannel channel = session.get(FChannel.class, channel_id);

        if (channel == null)
            throw new ServiceException("the section_id don't exist.");

        List <FMessage> messages =
                session.createSelectionQuery("from FMessage m join fetch m.user where m.channel.id=:channel_id order by m.id limit :limit offset :offset", FMessage.class)
                        .setParameter("channel_id", channel_id)
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
    public List<TmplRoom> get_rooms() {
        Session session = sessionFactory.getCurrentSession();

        List <FRoom> rooms = session.createSelectionQuery("from FRoom r join fetch r.creator", FRoom.class).getResultList();

        List<TmplRoom> result = new ArrayList<>();

        for (FRoom room : rooms) {
            result.add(new TmplRoom(room.getId(), room.getCreator().getLogin(), room.getName(), room.getDescription()));
        }

        return result;
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
    public void add_room(InputRoom room, String creator) {
        FRoom new_room = new FRoom();
        new_room.setName(room.name);
        new_room.setDescription(room.description);
        new_room.setHandle_code(room.handle_code);
        new_room.setInit_code(room.init_code);
        new_room.setStatus("not started");

        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(creator);

        if (user == null)
            throw new ServiceException("the user_id don't exist.");

        new_room.setCreator(user);

        FChannel channel_lobby = new FChannel();
        channel_lobby.setName("лобби");
        session.persist(channel_lobby);

        FChannel channel_players = new FChannel();
        channel_players.setName("игроки");
        session.persist(channel_players);

        FChannel channel_help = new FChannel();
        channel_help.setName("помощь");
        session.persist(channel_help);

        FMessage message = new FMessage();
        message.setDate(OffsetDateTime.now());
        message.setUser(user);
        message.setText(room.help);
        message.setChannel(channel_help);
        session.persist(message);

        new_room.addChannel(channel_lobby);
        new_room.addChannel(channel_players);
        new_room.addChannel(channel_help);

        session.persist(new_room);
    }

    @Transactional
    public void add_message(String username, String text, long channel_id) {
        FMessage new_message = new FMessage();
        new_message.setText(text);

        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        FChannel channel = session.get(FChannel.class, channel_id);

        if (user == null)
            throw new ServiceException("the user_id don't exist.");

        if (channel == null)
            throw new ServiceException("the channel_id don't exist.");

        new_message.setDate(OffsetDateTime.now());
        new_message.setChannel(channel);
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
    public boolean too_much_messages(String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        List<FMessage> messages = session.createSelectionQuery(
                        "from FMessage m where m.user = :user and m.date > :date order by m.date desc",
                        FMessage.class)
                .setParameter("user", user)
                .setParameter("date", OffsetDateTime.now().minusSeconds(5))
                .getResultList();

        if (messages.isEmpty())
            return false;

        if (messages.size() >= 5) {
            return true;
        }

        long sum = 0;

        for (FMessage message: messages) {
            sum += message.getText().length();
        }

        return messages.size() > 1 && sum * 1.0 / 5 > 50;
    }

    @Transactional
    public long get_eTag(String username) {
        Session session = sessionFactory.getCurrentSession();
        return session.bySimpleNaturalId(FUser.class).load(username).getPhoto_eTag();
    }

    @Transactional
    public void add_player(String username, long room_id) {
        Session session = sessionFactory.getCurrentSession();
        FUser player = session.bySimpleNaturalId(FUser.class).load(username);

        if (player == null) {
            throw new ServiceException("the player does not exist");
        }

        FRoom room = session.get(FRoom.class, room_id);

        if (player.getRoom() != null) {

            //System.out.println(player.getRoom().getId());
            //System.out.println(room.getId());
            //System.out.println(player.getRoom().equals(room));


            if (!player.getRoom().equals(room)) {
                throw new ServiceException("the player have joined to another room yet");
            } else {
                return;
            }
        }

        if (!Objects.equals(room.getStatus(), "not started")) {
            throw new ServiceException("the game has already started");
        }

        room.addPlayer(player);
    }

    @Transactional
    public long remove_player(String username, boolean force) {
        Session session = sessionFactory.getCurrentSession();
        FUser player = session.bySimpleNaturalId(FUser.class).load(username);
        FRoom room = player.getRoom();

        if (room == null)
            return -1;

        if (force || Objects.equals(room.getStatus(), "not started")) {
            room.removePlayer(player);
            return room.getId();
        }

        return -1;
    }

    @Transactional
    public List<TmplUser> get_players(long room_id) {
        Session session = sessionFactory.getCurrentSession();
        FRoom room = session.get(FRoom.class, room_id);
        List<FUser> players = room.getPlayers();
        List <TmplUser> result = new ArrayList<>();

        for (FUser player : players) {
            result.add(new TmplUser(player, catMaidService.getUwUDegree(player.getDate_UwU())));
        }

        return result;
    }
}
