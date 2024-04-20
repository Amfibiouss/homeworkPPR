package com.example.laba.services;

import com.example.laba.entities.*;
import com.example.laba.json_objects.*;
import com.example.laba.objects_to_fill_templates.TmplMessage;
import com.example.laba.objects_to_fill_templates.TmplRoom;
import com.example.laba.objects_to_fill_templates.TmplUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.RawValue;
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

        try {
            return objectMapper.writeValueAsString(new OutputMessage(username, alias, text, opacity, username_color, id));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String get_output_stage_channel(String name,
                                           long stage_id,
                                           long count,
                                           List<String> messages,
                                           boolean loaded) {

        try {
            return objectMapper.writeValueAsString(new OutputStageChannel(name, stage_id, count, messages, loaded));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public TmplUser createTmplUser(FUser user) {
        if (user.getUwUPunishment() == null) {
            return new TmplUser(user, 0);
        }
        return new TmplUser(user, catMaidService.getUwUDegree(user.getUwUPunishment().getDateUwU()));
    }

    public double getUwUDegree(FUser user) {
        if (user.getUwUPunishment() == null) {
            return 0;
        }
        return catMaidService.getUwUDegree(user.getUwUPunishment().getDateUwU());
    }

    @Transactional
    public List<TmplMessage> get_messages_of_user(String username, long offset, long limit) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        if (user == null)
            throw new ServiceException("the user don't exist.");

        List <FMessage> messages =
                session.createSelectionQuery("from FMessage m join fetch m.channel where m.user = :user order by m.id", FMessage.class)
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
        if (character.getPindex() == -1 || (character.getChannelXRayReadMask() & (1L << channel.getCindex())) != 0) {
            XRayRead = true;
        } else {
            if ((character.getChannelAnonReadMask() & (1L << channel.getCindex())) != 0) {
                AnonRead = true;
            }
        }

        List<RawValue> stages = new ArrayList<>();

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
                    stages.add(new RawValue(get_output_stage_channel(stage.getName(), stage.getId(), stage_channel.getCount(), new ArrayList<String>(), false)));
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
        stages.add(new RawValue(get_output_stage_channel(last_stage.getName(), last_stage.getId(), messages.size(), result_messages, true)));

        try {
            return objectMapper.writeValueAsString(stages);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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

        if (message == null)
            throw new ServiceException("the message_id don't exist.");

        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        FCharacter character = user.getCharacter();

        FChannel channel = message.getChannel();

        if (!channel.getRoom().equals(character.getRoom())) {
            throw new ServiceException("user are not unauthorized to read the data.");
        }

        long channel_mask = 1L << channel.getCindex();

        if ((character.getChannelReadMask() & channel_mask) == 0) {
            throw new ServiceException("user are not unauthorized to read the data.");
        }

        if ((character.getChannelXRayReadMask() & channel_mask) != 0)
            return message.getJsonXRayString();

        if ((character.getChannelAnonReadMask() & channel_mask) != 0)
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

        try {
            user.setRecentRoom(objectMapper.writeValueAsString(room));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        FRoom new_room = new FRoom();
        new_room.setName(room.name);
        new_room.setDescription(room.description);
        new_room.setMin_players(room.min_players);
        new_room.setMax_players(room.max_players);
        new_room.setMode(room.mode);
        new_room.setStatus("not started");

        new_room.setCreator(user);
        session.persist(new_room);

        FChannel channel_lobby = new FChannel();
        channel_lobby.setName("лобби");
        channel_lobby.setCindex(0L);
        session.persist(channel_lobby);
        new_room.addChannel(channel_lobby);

        FChannel channel_newspaper = new FChannel();
        channel_newspaper.setName("газета");
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
        character.setChannelReadMask(1L);
        character.setChannelXRayReadMask(1L);
        character.setChannelWriteMask(1L);
        character.setName(null);
        new_room.addCharacter(character);
        session.persist(character);

        FMessage message = new FMessage();
        message.setDate(OffsetDateTime.now());
        message.setUser(user);
        message.setAlias(user.getLogin());
        message.setText(room.help);
        message.setStage(stage);
        message.setTarget(-1L);
        session.persist(message);
        channel_lobby.addMessage(message);

        session.flush();

        session.refresh(message);
        message.setJsonXRayString(get_output_message(message.getAlias(), message.getAlias(), message.getText(),
                getUwUDegree(user), user.getAdmin()? "#ff0000" : null, message.getId()));

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
            if ((character.getChannelAnonWriteMask() & (1L << channel.getCindex())) != 0) {
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
                getUwUDegree(user), user.getAdmin()? "#ff0000" : null, new_message.getId()));

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
            result.add(new TmplUser(player, getUwUDegree(player)));
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

        if (status.equals(room.getStatus())) {
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

            if ((character.getPollObserveMask() & (1L << poll.getLindex())) == 0)
                continue;

            OutputStatePoll outputPoll = new OutputStatePoll();

            outputPoll.setDescription(poll.getDescription());
            outputPoll.setPoll_id(poll.getId());
            outputPoll.setName(poll.getName());
            outputPoll.setLindex(poll.getLindex());
            outputPoll.setCandidates(poll.getCandidates());
            outputPoll.setMaxSelection(poll.getMaxSelection());
            outputPoll.setMinSelection(poll.getMinSelection());

            outputStatePolls.add(outputPoll);
        }

        outputStateRoom.setPollVoteMask(character.getPollVoteMask());
        outputStateRoom.setPolls(outputStatePolls);

        FChannel newspaper = get_channel(room, "газета");

        FMessage message = session.createSelectionQuery(
                "from FMessage m where m.channel=:channel and m.target=:pindex order by m.date desc limit 1", FMessage.class)
                .setParameter("channel", newspaper)
                .setParameter("pindex", character.getPindex()).getSingleResultOrNull();

        if (message != null) {
            try {
                String res = "";

                if ((character.getChannelXRayReadMask() & 2) != 0)
                    res = message.getJsonXRayString();
                else if ((character.getChannelAnonReadMask() & 2) != 0)
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

            List<RawValue> jsonStrings = new ArrayList<>();
            List<RawValue> jsonAnonStrings = new ArrayList<>();
            List<RawValue> jsonXRayStrings = new ArrayList<>();
            for (FMessage message : messages) {
                jsonStrings.add(new RawValue(message.getJsonString()));
                jsonAnonStrings.add(new RawValue(message.getJsonAnonString()));
                jsonXRayStrings.add(new RawValue(message.getJsonXRayString()));
            }
            try {
                stage_channel.setJsonStrings(objectMapper.writeValueAsString(jsonStrings));
                stage_channel.setJsonAnonStrings(objectMapper.writeValueAsString(jsonAnonStrings));
                stage_channel.setJsonXRayStrings(objectMapper.writeValueAsString(jsonXRayStrings));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            stage_channel.setCount((long) messages.size());
            stage_channel.setChannel(channel);
            stage_channel.setStage(last_stage);

            stage_channel.setXRayReadMask(0L);
            stage_channel.setReadMask(0L);
            stage_channel.setAnonReadMask(0L);
            for (FCharacter character : room.getCharacters()) {
                long character_mask;
                if (character.getPindex() == -1) {
                    if (init) {
                        character_mask =  (1L << 30) - 1;
                    } else {
                        continue;
                    }
                } else {
                    character_mask = 1L << character.getPindex();
                }

                if ((character.getChannelReadMask() & (1L << channel.getCindex())) != 0) {
                    stage_channel.setReadMask(stage_channel.getReadMask() | character_mask);
                }
                if ((character.getChannelAnonReadMask() & (1L << channel.getCindex())) != 0) {
                    stage_channel.setAnonReadMask(stage_channel.getAnonReadMask() | character_mask);
                }
                if ((character.getChannelXRayReadMask() & (1L << channel.getCindex())) != 0) {
                    stage_channel.setXRayReadMask(stage_channel.getXRayReadMask() | character_mask);
                }
            }
            session.persist(stage_channel);
        }


        FStage new_stage = new FStage();
        new_stage.setName(inputStateRoom.getStage());
        new_stage.setDate(OffsetDateTime.now());
        session.persist(new_stage);
        room.addStage(new_stage);

        if (init) {
            for (InputStateChannel inputChannel : inputStateRoom.getChannels()) {
                FChannel channel = new FChannel();

                if (inputChannel.getName().equals("газета") || inputChannel.getName().equals("лобби"))
                    continue;

                channel.setCindex(inputChannel.getCindex());
                channel.setName(inputChannel.getName());
                session.persist(channel);
                room.addChannel(channel);
            }
        }

        session.createMutationQuery("delete FPoll p where p.room = :room").setParameter("room", room).executeUpdate();

        for (InputStatePoll inputPoll : inputStateRoom.getPolls()) {

            FPoll poll = new FPoll();

            poll.setLindex(inputPoll.getLindex());
            poll.setName(inputPoll.getName());
            try {
                poll.setCandidates(objectMapper.writeValueAsString(inputPoll.getCandidates()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            poll.setMask_candidates(inputPoll.getMask_candidates());
            poll.setDescription(inputPoll.getDescription());
            poll.setMaxSelection((inputPoll.getMaxSelection() == null)? 1 : inputPoll.getMaxSelection());
            poll.setMinSelection((inputPoll.getMinSelection() == null)? 1 : inputPoll.getMinSelection());
            session.persist(poll);

            room.addPoll(poll);
        }

        for (InputStateCharacter inputCharacter : inputStateRoom.getCharacters()) {

            FCharacter character = session.createSelectionQuery(
                    "from FCharacter c where c.room = :room and c.pindex = :pindex", FCharacter.class)
                    .setParameter("pindex", inputCharacter.getPindex())
                    .setParameter("room", room)
                    .getSingleResult();

            if (inputCharacter.getName() != null)
                character.setName(inputCharacter.getName());
            character.setChannelReadMask(inputCharacter.getChannelReadMask());
            character.setChannelAnonReadMask(inputCharacter.getChannelAnonReadMask());
            character.setChannelXRayReadMask(inputCharacter.getChannelXRayReadMask());
            character.setChannelWriteMask(inputCharacter.getChannelWriteMask());
            character.setChannelAnonWriteMask(inputCharacter.getChannelAnonWriteMask());
            character.setPollVoteMask(inputCharacter.getPollVoteMask());
            character.setPollObserveMask(inputCharacter.getPollObserveMask());
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
                    getUwUDegree(new_message.getUser()),
                    new_message.getUser().getAdmin()? "#ff0000" : null, new_message.getId()));
        }
    }

    @Transactional
    public void add_vote(long poll_id, String username, long[] candidates) {
        Session session = sessionFactory.getCurrentSession();
        FPoll poll = session.get(FPoll.class, poll_id);
        FUser player = session.bySimpleNaturalId(FUser.class).load(username);
        FCharacter character = player.getCharacter();

        long pindex = player.getCharacter().getPindex();

        if (poll == null || (character.getPollVoteMask() & (1L << poll.getLindex())) == 0
                || candidates.length < poll.getMinSelection()
                || candidates.length > poll.getMaxSelection()) {
            throw new ServiceException("Неправильные данные");
        }

        long mask = 0;

        for (long candidate : candidates) {

            if (candidate < 0 || candidate >= 30
                    || (poll.getMask_candidates() & (1L << candidate)) == 0) {
                throw new ServiceException("Неправильные данные");
            }

            mask |= 1L << candidate;
        }


        long[] poll_table = poll.getPoll_table();
        poll_table[(int) pindex] |= mask;
        poll.setPoll_table(poll_table);

        character.setPollVoteMask(character.getPollVoteMask() ^ (1L << poll.getLindex()));
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

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = REQUIRES_NEW)
    public InputRoom getUserRecentRoom(String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        if (user.getRecentRoom() == null)
            return null;

        try {
            return objectMapper.readValue(user.getRecentRoom(), InputRoom.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
