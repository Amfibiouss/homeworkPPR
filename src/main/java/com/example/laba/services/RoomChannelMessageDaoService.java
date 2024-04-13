package com.example.laba.services;

import com.example.laba.entities.*;
import com.example.laba.json_objects.*;
import com.example.laba.objects_to_fill_templates.TmplMessage;
import com.example.laba.objects_to_fill_templates.TmplRoom;
import com.example.laba.objects_to_fill_templates.TmplUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

import static java.util.Collections.shuffle;
import static org.springframework.transaction.annotation.Propagation.*;

@Component
public class RoomChannelMessageDaoService {
    @Autowired
    CatMaidService catMaidService;

    @Autowired
    ObjectMapper objectMapper;

    @PersistenceUnit
    SessionFactory sessionFactory;

    public String get_output_message(String username,
                                     String alias,
                                     String text,
                                     double opacity,
                                     String username_color,
                                     long id) {

        return String.format("{\"username\": %s, \"text\": \"%s\", \"alias\":\"%s\"," +
                        " \"opacity\":\"%s\", \"username_color\": %s, \"id\": %d}",
                (username == null)? null : "\"" + username + "\"", text, alias, opacity,
                (username_color == null)? null : "\"" + username_color + "\"", id);

    }

    public String get_output_stage_channel(String name,
                                     long stage_id, long count,
                                     String messages, boolean loaded) {

        return String.format("{\"stage_name\": \"%s\", \"stage_id\": \"%d\", " +
                        "\"count\":%d, \"messages\": %s, \"loaded\": %s}",
                name, stage_id, count, messages, loaded);
    }

    public String get_array(List<String> list) {
        boolean isFirst = true;
        StringBuilder result = new StringBuilder("[");
        for (String s : list) {
            if (!isFirst)
                result.append(",");
            isFirst = false;
            result.append(s);
        }
        result.append("]");
        return result.toString();
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

    @Transactional(propagation = MANDATORY)
    public FChannel get_channel(FRoom room, String name) {
        Session session = sessionFactory.getCurrentSession();

        FChannel channel = session.createSelectionQuery(
                        "from FChannel c where c.room=:room and c.name=:name", FChannel.class)
                .setParameter("room", room)
                .setParameter("name", name)
                .getSingleResultOrNull();

        if (channel == null)
            throw new ServiceException("the channel name don't exist.");

        return channel;
    }

    @Transactional
    public String get_messages(long channel_id, String username) {
        Session session = sessionFactory.getCurrentSession();
        FChannel channel = session.get(FChannel.class, channel_id);
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        FCharacter character = user.getCharacter();

        if (channel == null)
            throw new ServiceException("the channel_id don't exist.");

        List <FStageFChannel> stage_channels = session.createSelectionQuery(
                "from FStageFChannel s join fetch s.stage where s.channel=:channel order by s.stage.date", FStageFChannel.class)
                .setParameter("channel", channel)
                .getResultList();


        boolean XRayRead = false;
        boolean AnonRead = false;
        if (character.getPindex() == -1 || (channel.getRead_real_username_mask() & (1L << character.getPindex())) != 0) {
            XRayRead = true;
        } else {
            if ((channel.getAnon_read_mask() & (1L << character.getPindex())) != 0) {
                AnonRead = true;
            }
        }

        List<String> stages = new ArrayList<>();

        FStage last_stage;

        if (!Objects.equals(channel.getName(), "газета")) {

            last_stage = session.createSelectionQuery(
                            "from FStage s where s.room=:room order by s.date desc limit 1", FStage.class)
                    .setParameter("room", channel.getRoom())
                    .getSingleResult();

            for (FStageFChannel stage_channel : stage_channels) {
                FStage stage = stage_channel.getStage();

                if (stage != last_stage) {
                    //String jsonString = (XRayRead) ? stage_channel.getJsonXRayStrings() :
                    //        ((AnonRead) ? stage_channel.getJsonAnonStrings() : stage_channel.getJsonStrings());
                    stages.add(get_output_stage_channel(stage.getName(), stage.getId(), stage_channel.getCount(), "[]", false));
                }
            }
        } else {
            last_stage = session.createSelectionQuery(
                    "from FStage s where s.name='начало' and s.room=:room", FStage.class)
                    .setParameter("room", channel.getRoom()).getSingleResult();
        }


        List <FMessage> messages =
                session.createSelectionQuery("from FMessage m where " +
                                "m.channel=:channel and m.stage=:stage" +
                                " and (m.target=-1 or m.target=:target)", FMessage.class)
                        .setParameter("channel", channel)
                        .setParameter("stage", last_stage)
                        .setParameter("target", character.getPindex())
                        .getResultList();

        List <String> result_messages = new ArrayList<>();

        for (FMessage message : messages) {
            result_messages.add((XRayRead)? message.getJsonXRayString() :
                    ((AnonRead)? message.getJsonAnonString() : message.getJsonString()));
        }
        stages.add(get_output_stage_channel(last_stage.getName(), last_stage.getId(), messages.size(), get_array(result_messages), true));

        return get_array(stages);
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ, propagation = REQUIRES_NEW)
    public String get_stage_messages(long channel_id, long stage_id, String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        FCharacter character = user.getCharacter();
        FStageFChannel stage_channel = session.get(FStageFChannel.class, new FStageFChannelId(stage_id, channel_id));

        if (character.getPindex() == -1) {
            return stage_channel.getJsonStrings();
        }

        if ((stage_channel.getReadMask() & (1L << character.getPindex())) == 0) {
            return "[]";
        } else if ((stage_channel.getAnonReadMask() & (1L << character.getPindex())) != 0) {
            return stage_channel.getJsonAnonStrings();
        } else if ((stage_channel.getXRayReadMask() & (1L << character.getPindex())) != 0) {
            return stage_channel.getJsonXRayStrings();
        } else {
            return stage_channel.getJsonStrings();
        }
    }

    @Transactional
    public String get_message(long message_id, String username) {
        Session session = sessionFactory.getCurrentSession();
        FMessage message = session.get(FMessage.class, message_id);
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        FCharacter character = user.getCharacter();

        if (message == null)
            throw new ServiceException("the message_id don't exist.");

        FChannel channel = message.getChannel();

        if (!channel.getRoom().equals(character.getRoom())) {
            throw new ServiceException("user are not unauthorized to read the data.");
        }

        if (character.getPindex() != -1 && (channel.getRead_mask() & (1L << character.getPindex())) == 0) {
            throw new ServiceException("user are not unauthorized to read the data.");
        }

        if (character.getPindex() == -1
                || (channel.getRead_real_username_mask() & (1L << character.getPindex())) != 0)
            return message.getJsonXRayString();

        if ((channel.getAnon_read_mask() & (1L << character.getPindex())) != 0)
            return message.getJsonAnonString();

        return message.getJsonString();
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
    public long add_room(InputRoom room, String creator) {
        FRoom new_room = new FRoom();
        new_room.setName(room.name);
        new_room.setDescription(room.description);
        new_room.setMin_players(room.min_players);
        new_room.setMax_players(room.max_players);
        new_room.setCode(room.code);
        new_room.setStatus("not started");

        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(creator);

        if (user == null)
            throw new ServiceException("the user_id don't exist.");

        if (user.getCharacter() != null)
            throw new ServiceException("the creator has joined to another room");

        long rooms_cnt = session
                .createQuery("select count(*) from FRoom r where r.creator=:user and r.status != 'closed'", Long.class)
                .setParameter("user", user)
                .getSingleResult();

        if (rooms_cnt != 0) {
            throw new ServiceException("the another user's room has not been finished yet");
        }

        new_room.setCreator(user);
        session.persist(new_room);

        FChannel channel_lobby = new FChannel();
        channel_lobby.setName("лобби");
        channel_lobby.setRead_mask((1L << 30) - 1);
        channel_lobby.setWrite_mask((1L << 30) - 1);
        channel_lobby.setRead_real_username_mask((1L << 30) - 1);
        channel_lobby.setAnon_write_mask(0L);
        channel_lobby.setAnon_read_mask(0L);
        channel_lobby.setCindex(0L);
        session.persist(channel_lobby);
        new_room.addChannel(channel_lobby);

        FChannel channel_newspaper = new FChannel();
        channel_newspaper.setName("газета");
        channel_newspaper.setRead_mask(0L);
        channel_newspaper.setWrite_mask(0L);
        channel_newspaper.setRead_real_username_mask((1L << 30) - 1);
        channel_newspaper.setAnon_write_mask(0L);
        channel_newspaper.setAnon_read_mask(0L);
        channel_newspaper.setCindex(1L);
        session.persist(channel_newspaper);
        new_room.addChannel(channel_newspaper);

        FStage stage = new FStage();
        stage.setName("начало");
        stage.setDate(OffsetDateTime.now());
        session.persist(stage);
        new_room.addStage(stage);

        FCharacter character = new FCharacter();
        character.setPindex(-1L);
        character.setChannelReadMask(3L);
        character.setChannelXRayReadMask(3L);
        character.setChannelWriteMask(3L);
        character.setName(null);
        new_room.addCharacter(character);
        session.persist(character);

        FMessage message = new FMessage();
        message.setDate(OffsetDateTime.now());
        message.setUser(user);
        message.setAlias(user.getLogin());
        message.setText(room.help.replaceAll("\n", "\\\\n"));
        message.setStage(stage);
        message.setTarget(-1L);
        session.persist(message);
        channel_lobby.addMessage(message);

        session.flush();

        session.refresh(message);
        message.setJsonXRayString(get_output_message(message.getAlias(), message.getAlias(), message.getText(),
                catMaidService.getUwUDegree(user.getDate_UwU()), user.getAdmin()? "#ff0000" : null, message.getId()));

        message.setJsonAnonString(get_output_message(null, "???", message.getText(),
                0f, null, message.getId()));

        message.setJsonString(get_output_message(null, message.getAlias(),
                message.getText(), 0f, null, message.getId()));


        session.refresh(new_room);
        return new_room.getId();
    }

    @Transactional
    public long add_message(String username, String text, long channel_id, long target) {
        FMessage new_message = new FMessage();
        new_message.setText(text);

        Session session = sessionFactory.getCurrentSession();

        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        if (user == null)
            throw new ServiceException("the user_id don't exist.");

        FChannel channel = session.get(FChannel.class, channel_id);
        if (channel == null)
            throw new ServiceException("the channel_id don't exist.");

        FCharacter character = user.getCharacter();
        if (character == null)
            throw new ServiceException("the user is not in room.");

        new_message.setDate(OffsetDateTime.now());
        new_message.setUser(user);
        new_message.setTarget(target);

        if (character.getName() != null) {
            if ((channel.getAnon_write_mask() & (1L << character.getPindex())) != 0) {
                new_message.setAlias("???");
            } else {
                new_message.setAlias(character.getName());
            }
        } else {
            new_message.setAlias(user.getLogin());
        }

        session.persist(new_message);
        channel.addMessage(new_message);

        new_message.setStage(session.createSelectionQuery(
                        "from FStage s where s.room=:room order by s.date desc limit 1", FStage.class)
                .setParameter("room", channel.getRoom())
                .getSingleResult());

        session.flush();
        session.refresh(new_message);

        new_message.setJsonXRayString(get_output_message(username, new_message.getAlias(), new_message.getText(),
                catMaidService.getUwUDegree(user.getDate_UwU()), user.getAdmin()? "#ff0000" : null, new_message.getId()));

        new_message.setJsonAnonString(get_output_message(null, "???", new_message.getText(),
                0f, null, new_message.getId()));

        new_message.setJsonString(get_output_message(null, new_message.getAlias(),
                new_message.getText(), 0f, null, new_message.getId()));

        return new_message.getId();
    }

    @Transactional
    public boolean too_much_messages(String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        List<FMessage> messages = session.createSelectionQuery(
                        "from FMessage m where m.user = :user and m.date > :date and m.channel.name != 'газета' order by m.date desc",
                        FMessage.class)
                .setParameter("user", user)
                .setParameter("date", OffsetDateTime.now().minusSeconds(5))
                .getResultList();

        return messages.size() >= 5;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = REQUIRES_NEW)
    public void almost_add_player(String username, long room_id) {
        Session session = sessionFactory.getCurrentSession();
        FUser player = session.bySimpleNaturalId(FUser.class).load(username);
        player.setDesired_room_id(room_id);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = REQUIRES_NEW)
    public void add_player(String username, String sessionId) {
        Session session = sessionFactory.getCurrentSession();
        FUser player = session.bySimpleNaturalId(FUser.class).load(username);

        if (player == null) {
            throw new ServiceException("the player does not exist");
        }

        FRoom room = session.get(FRoom.class, player.getDesired_room_id());

        if (room == null) {
            throw new ServiceException("the room does not exist");
        }

        if (player.getCharacter() != null) {
            if (!player.getCharacter().getRoom().equals(room)) {
                throw new ServiceException("the player have joined to another room yet");
            } else {
                return;
            }
        }

        if (!Objects.equals(room.getStatus(), "not started")) {
            throw new ServiceException("the game has already started");
        }

        long cnt = session
                .createQuery("select count(*) from FUser u where u.character.room = :room", Long.class)
                .setParameter("room", room).getSingleResult();

        if (cnt >= room.getMax_players()) {
            throw new ServiceException("there are no places");
        }

        FCharacter character = room.getCharacters().iterator().next();

        player.setSessionId(sessionId);
        player.setDesired_room_id(null);
        player.setCharacter(character);
    }

    @Transactional
    public long remove_offline_player(String username, String sessionId) {
        Session session = sessionFactory.getCurrentSession();
        FUser player = session.bySimpleNaturalId(FUser.class).load(username);
        FCharacter character = player.getCharacter();

        if (character == null || !Objects.equals(sessionId, player.getSessionId()))
            return -1;

        FRoom room = character.getRoom();

        if (Objects.equals(room.getStatus(), "not started")) {

            player.setSessionId(null);
            player.setCharacter(null);

            if (room.getCreator().equals(player)) {
                room.setStatus("closed");
            }

            return room.getId();
        }

        return -1;
    }

    @Transactional
    public void remove_player(String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser player = session.bySimpleNaturalId(FUser.class).load(username);

        if (player.getCharacter() == null)
            return;

        FRoom room = player.getCharacter().getRoom();
        player.setCharacter(null);
        player.setSessionId(null);

        if (room.getCreator().equals(player)) {
            room.setStatus("closed");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = REQUIRES_NEW, readOnly = true)
    public List<TmplUser> get_players(long room_id) {
        Session session = sessionFactory.getCurrentSession();
        FRoom room = session.get(FRoom.class, room_id);
        List<FUser> players = session.createSelectionQuery(
                "from FUser u where u.character.room=:room", FUser.class)
                .setParameter("room", room).getResultList();

        List <TmplUser> result = new ArrayList<>();

        for (FUser player : players) {
            result.add(new TmplUser(player, catMaidService.getUwUDegree(player.getDate_UwU())));
        }

        return result;
    }

    @Transactional
    public boolean authorize_player(String username, long room_id, boolean readonly) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        if (user.getCharacter() == null)
            return false;

        if (!readonly && user.getCharacter().getRoom().getStatus().equals("closed")) {
            return false;
        }

        return user.getCharacter().getRoom().getId() == room_id;
    }

    @Transactional
    public boolean isHost(long room_id, String username) {
        Session session = sessionFactory.getCurrentSession();
        FRoom room = session.get(FRoom.class, room_id);

        return room.getCreator().getLogin().equals(username);
    }

    @Transactional
    public boolean set_room_status(long room_id, String status) {
        Session session = sessionFactory.getCurrentSession();
        FRoom room = session.get(FRoom.class, room_id);

        if (status.equals("closed")) {
            room.setStatus("closed");
            return true;
        }

        switch (room.getStatus()) {

            case "not_started":
                if (!status.equals("initialization")) {
                    return false;
                }
                break;

            case "initialization":
                if (!status.equals("started")) {
                    return false;
                }
                break;

            case "started":
                if (!status.equals("processing")) {
                    return false;
                }
                break;

            case "processing":
                if (!status.equals("started") && !status.equals("finished")) {
                    return false;
                }
                break;

            case "finished", "closed":
                return false;
        }

        room.setStatus(status);
        return true;
    }

    @Transactional
    public long numerate_player(long room_id) {
        Session session = sessionFactory.getCurrentSession();
        FRoom room = session.get(FRoom.class, room_id);

        List<FUser> players = session.createSelectionQuery(
                        "from FUser u where u.character.room=:room", FUser.class)
                .setParameter("room", room).getResultList();

        long players_count = players.size();

        ArrayList <Long> permutation = new ArrayList<>();
        for (long i = 0; i < players_count; i++)
            permutation.add(i);
        shuffle(permutation, new Random(System.currentTimeMillis() + 9427347432442L));
        permutation.set(permutation.indexOf(0L), permutation.getFirst());
        permutation.set(0, 0L);

        long cnt = 0;
        for (FUser player : players) {
            FCharacter character = new FCharacter();

            if (player.equals(room.getCreator())) {
                character.setName(String.valueOf(0));
                character.setPindex(0L);
            } else {
                character.setName(String.valueOf(++cnt));
                character.setPindex(permutation.get((int)cnt));
            }

            room.addCharacter(character);
            session.persist(character);
            player.setCharacter(character);
        }

        while (cnt < 20) {
            FCharacter character = new FCharacter();
            character.setName(String.valueOf(++cnt));
            character.setPindex(cnt);
            room.addCharacter(character);
            session.persist(character);
        }

        return 20;
        //return players_count;
    }

    @Transactional
    public OutputStateRoom get_state_for_player(long room_id, String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        FCharacter character = user.getCharacter();
        FRoom room = session.get(FRoom.class, room_id);

        OutputStateRoom outputStateRoom = new OutputStateRoom();
        FStage curr_stage = session.createSelectionQuery(
                        "from FStage s where s.room=:room order by s.date desc limit 1", FStage.class)
                .setParameter("room", room)
                .getSingleResult();
        outputStateRoom.setStage(curr_stage.getName());
        outputStateRoom.setStage_id(curr_stage.getId());
        outputStateRoom.setFinish_stage(room.getFinish_stage());
        outputStateRoom.setStatus(room.getStatus());
        outputStateRoom.setChannelReadMask(character.getChannelReadMask());
        outputStateRoom.setChannelAnonReadMask(character.getChannelAnonReadMask());
        outputStateRoom.setChannelXRayReadMask(character.getChannelXRayReadMask());
        outputStateRoom.setChannelWriteMask(character.getChannelWriteMask());
        outputStateRoom.setChannelAnonWriteMask(character.getChannelAnonWriteMask());

        List<OutputStateChannel> outputStateChannels = new ArrayList<>();
        for (FChannel channel : room.getChannels()) {
            OutputStateChannel outputChannel = new OutputStateChannel();
            long cindex_mask = 1L << channel.getCindex();

            outputChannel.setChannel_id(channel.getId());
            outputChannel.setName(channel.getName());
            outputChannel.setCindex(channel.getCindex());

            if ((character.getChannelReadMask() & cindex_mask) != 0)
                outputStateChannels.add(outputChannel);
        }
        outputStateRoom.setChannels(outputStateChannels);

        List<OutputStatePoll> outputStatePolls = new ArrayList<>();
        for (FPoll poll : room.getPolls()) {

            if ((poll.getMask_observers() & (1L << character.getPindex())) == 0)
                continue;

            OutputStatePoll outputPoll = new OutputStatePoll();

            outputPoll.setPoll_id(poll.getId());
            outputPoll.setName(poll.getName());
            outputPoll.setCan_vote((poll.getMask_voters() & (1L << character.getPindex())) != 0);


            try {
                outputPoll.setCandidates(objectMapper.readValue(poll.getCandidates(), Map.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            outputStatePolls.add(outputPoll);
        }
        outputStateRoom.setPolls(outputStatePolls);

        FChannel newspaper = get_channel(room, "газета");

        FMessage message = session.createSelectionQuery(
                "from FMessage m where m.channel=:channel and m.target=:pindex order by m.date desc limit 1", FMessage.class)
                .setParameter("channel", newspaper)
                .setParameter("pindex", character.getPindex()).getSingleResultOrNull();

        if (message != null) {
            try {
                String res = "";

                if ((newspaper.getRead_real_username_mask() & (1L << character.getPindex())) != 0)
                    res = message.getJsonXRayString();
                else if ((newspaper.getAnon_read_mask() & (1L << character.getPindex())) != 0)
                    res = message.getJsonAnonString();
                else
                    res = message.getJsonString();

                outputStateRoom.setNewspaperMessage(objectMapper.readValue(res, OutputMessage.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return outputStateRoom;
    }

    @Transactional
    public void set_state(long room_id, InputStateRoom inputStateRoom, boolean init) {
        Session session = sessionFactory.getCurrentSession();
        FRoom room = session.get(FRoom.class, room_id);

        room.setFinish_stage(OffsetDateTime.now().plusSeconds(inputStateRoom.getDuration()));

        FStage last_stage = session.createSelectionQuery(
                        "from FStage s where s.room=:room order by s.date desc limit 1", FStage.class)
                .setParameter("room", room)
                .getSingleResultOrNull();

        for (FChannel channel : room.getChannels()) {

            List<FMessage> messages = session.createSelectionQuery(
                    "from FMessage m where m.stage=:stage and m.channel=:channel order by m.date", FMessage.class)
                    .setParameter("stage", last_stage)
                    .setParameter("channel", channel)
                    .getResultList();

            if (messages.isEmpty())
                continue;

            FStageFChannel stage_channel = new FStageFChannel();

            stage_channel.setJsonStrings("[");
            stage_channel.setJsonAnonStrings("[");
            stage_channel.setJsonXRayStrings("[");
            boolean isFirst = true;
            for (FMessage message : messages) {

                if (isFirst) {
                    stage_channel.setJsonStrings(stage_channel.getJsonStrings() + message.getJsonString());
                    stage_channel.setJsonAnonStrings(stage_channel.getJsonAnonStrings() + message.getJsonAnonString());
                    stage_channel.setJsonXRayStrings(stage_channel.getJsonXRayStrings() + message.getJsonXRayString());
                } else {
                    stage_channel.setJsonStrings(stage_channel.getJsonStrings() + "," + message.getJsonString());
                    stage_channel.setJsonAnonStrings(stage_channel.getJsonAnonStrings() + "," + message.getJsonAnonString());
                    stage_channel.setJsonXRayStrings(stage_channel.getJsonXRayStrings() + "," + message.getJsonXRayString());
                }
                isFirst = false;
            }
            stage_channel.setJsonStrings(stage_channel.getJsonStrings() + "]");
            stage_channel.setJsonAnonStrings(stage_channel.getJsonAnonStrings() + "]");
            stage_channel.setJsonXRayStrings(stage_channel.getJsonXRayStrings() + "]");

            stage_channel.setCount((long) messages.size());
            stage_channel.setChannel(channel);
            stage_channel.setStage(last_stage);
            stage_channel.setXRayReadMask(channel.getRead_real_username_mask());
            stage_channel.setReadMask(channel.getRead_mask());
            stage_channel.setAnonReadMask(channel.getAnon_read_mask());
            session.persist(stage_channel);
        }


        FStage new_stage = new FStage();
        new_stage.setName(inputStateRoom.getStage());
        new_stage.setDate(OffsetDateTime.now());
        session.persist(new_stage);
        room.addStage(new_stage);

        for (InputStateChannel inputChannel : inputStateRoom.getChannels()) {

            FChannel channel = session.createSelectionQuery(
                            "from FChannel c where c.room=:room and c.name=:name", FChannel.class)
                    .setParameter("room", room)
                    .setParameter("name", inputChannel.getName())
                    .getSingleResultOrNull();

            if (channel == null) {
                if (init) {
                    channel = new FChannel();
                    session.persist(channel);

                    if (inputChannel.getRead_mask() == null
                    || inputChannel.getWrite_mask() == null
                    || inputChannel.getRead_real_username_mask() == null
                    || inputChannel.getAnon_write_mask() == null
                    || inputChannel.getAnon_read_mask() == null) {
                        throw new ServiceException("incorrect parameter.");
                    }

                    channel.setCindex(inputChannel.getCindex());
                    channel.setRead_mask(inputChannel.getRead_mask());
                    channel.setWrite_mask(inputChannel.getWrite_mask());
                    channel.setRead_real_username_mask(inputChannel.getRead_real_username_mask());
                    channel.setAnon_write_mask(inputChannel.getAnon_write_mask());
                    channel.setAnon_read_mask(inputChannel.getAnon_read_mask());
                    channel.setName(inputChannel.getName());

                    room.addChannel(channel);
                }
            } else {
                if (inputChannel.getRead_mask() != null) {
                    channel.setRead_mask(inputChannel.getRead_mask());
                }
                if (inputChannel.getWrite_mask() != null) {
                    channel.setWrite_mask(inputChannel.getWrite_mask());
                }
                if (inputChannel.getRead_real_username_mask() != null) {
                    channel.setRead_real_username_mask(inputChannel.getRead_real_username_mask());
                }
                if (inputChannel.getAnon_write_mask() != null) {
                    channel.setAnon_write_mask(inputChannel.getAnon_write_mask());
                }
                if (inputChannel.getAnon_read_mask() != null) {
                    channel.setAnon_read_mask(inputChannel.getAnon_read_mask());
                }
            }
        }

        session.createMutationQuery("delete FPoll p where p.room = :room").setParameter("room", room).executeUpdate();

        for (InputStatePoll inputPoll : inputStateRoom.getPolls()) {

            FPoll poll = new FPoll();

            poll.setName(inputPoll.getName());
            try {
                poll.setCandidates(objectMapper.writeValueAsString(inputPoll.getCandidates()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            poll.setMask_observers(inputPoll.getMask_observers());
            poll.setMask_voters(inputPoll.getMask_voters());
            poll.setMask_candidates(inputPoll.getMask_candidates());
            session.persist(poll);

            room.addPoll(poll);
        }

        for (FCharacter character : room.getCharacters()) {

            if (character.getPindex() == -1)
                continue;

            character.setChannelReadMask(0L);
            character.setChannelAnonReadMask(0L);
            character.setChannelXRayReadMask(0L);
            character.setChannelWriteMask(0L);
            character.setChannelAnonWriteMask(0L);

            long pindex_mask = 1L << character.getPindex();

            for (FChannel channel : room.getChannels()) {
                if ((channel.getRead_mask() & pindex_mask) != 0) {
                    character.setChannelReadMask(character.getChannelReadMask() | (1L << channel.getCindex()));
                }
                if ((channel.getRead_real_username_mask() & pindex_mask) != 0) {
                    character.setChannelXRayReadMask(character.getChannelXRayReadMask() | (1L << channel.getCindex()));
                }
                if ((channel.getAnon_read_mask() & pindex_mask) != 0) {
                    character.setChannelAnonReadMask(character.getChannelAnonReadMask() | (1L << channel.getCindex()));
                }
                if ((channel.getRead_real_username_mask() & pindex_mask) != 0) {
                    character.setChannelXRayReadMask(character.getChannelXRayReadMask() | (1L << channel.getCindex()));
                }
                if ((channel.getWrite_mask() & pindex_mask) != 0) {
                    character.setChannelWriteMask(character.getChannelWriteMask() | (1L << channel.getCindex()));
                }
                if ((channel.getAnon_write_mask() & pindex_mask) != 0) {
                    character.setChannelAnonWriteMask(character.getChannelAnonWriteMask() | (1L << channel.getCindex()));
                }
            }
        }

        Map <String, String> names = inputStateRoom.getNames();
        if (names != null) {
            for (FCharacter character : room.getCharacters()) {
                String pindex = String.valueOf(character.getPindex());

                if (names.containsKey(pindex)) {
                    character.setName(names.get(pindex));
                }
            }
        }

        long cnt = 0;

        FChannel newspaper = session.createSelectionQuery(
                "from FChannel c where c.room=:room and c.name='газета'", FChannel.class)
                .setParameter("room", room)
                .getSingleResult();

        for (String message_text : inputStateRoom.getMessages()) {

            if (Objects.equals(message_text, "")) {
                ++cnt;
                continue;
            }

            FMessage new_message = new FMessage();
            new_message.setTarget(cnt++);
            new_message.setUser(room.getCreator());
            new_message.setDate(OffsetDateTime.now());
            new_message.setText(message_text);
            new_message.setAlias(String.valueOf(room.getCreator().getLogin()));

            new_message.setStage(session.createSelectionQuery(
                            "from FStage s where s.name='начало' and s.room=:room", FStage.class).
                                setParameter("room", room) .getSingleResult());
            session.persist(new_message);
            newspaper.addMessage(new_message);
            session.flush();
            session.refresh(new_message);

            new_message.setJsonString(get_output_message(
                    null, new_message.getAlias(),
                    new_message.getText(), 0f, null, new_message.getId()));
            new_message.setJsonAnonString(get_output_message(
                    null, "???",
                    new_message.getText(), 0f, null, new_message.getId()));
            new_message.setJsonXRayString(get_output_message(
                    new_message.getUser().getLogin(),
                    new_message.getAlias(),
                    new_message.getText(),
                    catMaidService.getUwUDegree(new_message.getUser().getDate_UwU()),
                    new_message.getUser().getAdmin()? "#ff0000" : null, new_message.getId()));
        }
    }

    @Transactional
    public boolean add_vote(long poll_id, String username, long candidate) {
        Session session = sessionFactory.getCurrentSession();
        FPoll poll = session.get(FPoll.class, poll_id);
        FUser player = session.bySimpleNaturalId(FUser.class).load(username);

        if (player == null || poll == null || candidate < 0 || candidate >= 30
                || (poll.getMask_candidates() & (1L << candidate)) == 0) {
            return false;
        }

        long index = player.getCharacter().getPindex();

        if ((poll.getMask_voters() & (1L << index)) == 0) {
            return false;
        }

        long[] poll_table = poll.getPoll_table();
        long[] reverse_poll_table = poll.getReverse_poll_table();

        if (reverse_poll_table[(int)index] != 0) {
            return false;
        }

        poll_table[(int) candidate] |= (1L << index);
        reverse_poll_table[(int)index] |= (1L << candidate);

        poll.setPoll_table(poll_table);
        poll.setReverse_poll_table(reverse_poll_table);

        return true;
    }

    @Transactional
    public List<OutputPollResult> get_polls_results(long room_id) {

        Session session = sessionFactory.getCurrentSession();
        FRoom room = session.get(FRoom.class, room_id);

        List<OutputPollResult> results = new ArrayList<>();

        for (FPoll poll : room.getPolls()) {
            OutputPollResult pollResult = new OutputPollResult();

            pollResult.setName(poll.getName());
            pollResult.setPoll_table(poll.getPoll_table().clone());
            results.add(pollResult);
        }

        return results;
    }

    @Transactional
    public void change_index(String username, long index) {
        Session session = sessionFactory.getCurrentSession();
        FUser player = session.bySimpleNaturalId(FUser.class).load(username);

        FCharacter character = session.createSelectionQuery(
                "from FCharacter c where c.room = :room and c.pindex = :index", FCharacter.class)
                .setParameter("room", player.getCharacter().getRoom())
                .setParameter("index", index).getSingleResultOrNull();

        if (character == null)
            return;

        player.setCharacter(character);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = REQUIRES_NEW)
    public Long get_room_id(String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        return user.getCharacter().getRoom().getId();
    }
}
