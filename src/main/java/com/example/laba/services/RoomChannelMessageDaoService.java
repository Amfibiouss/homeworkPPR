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
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

import static java.util.Collections.shuffle;

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
                                     String username_color) {

        return String.format("{\"username\": %s, \"text\": \"%s\", \"alias\":\"%s\"," +
                        " \"opacity\":\"%s\", \"username_color\": %s}",
                (username == null)? null : "\"" + username + "\"", text, alias, opacity,
                (username_color == null)? null : "\"" + username_color + "\"");

    }

    public String get_output_stage_channel(String name,
                                     long count,
                                     String messages) {

        return String.format("{\"stage_name\": \"%s\", \"count\":%d, \"messages\": %s}", name, count, messages);
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
    public String get_messages(long channel_id, String username) {
        Session session = sessionFactory.getCurrentSession();
        FChannel channel = session.get(FChannel.class, channel_id);
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        if (channel == null)
            throw new ServiceException("the channel_id don't exist.");

        List <FStageFChannel> stage_channels = session.createSelectionQuery(
                "from FStageFChannel s join fetch s.id.stage where s.id.channel=:channel order by s.id.stage.date", FStageFChannel.class)
                .setParameter("channel", channel)
                .getResultList();


        boolean XRayRead = false;
        boolean AnonRead = false;
        if (user.getPlayer_index() == -1 || (channel.getRead_real_username_mask() & (1L << user.getPlayer_index())) != 0) {
            XRayRead = true;
        } else {
            if ((channel.getAnon_read_mask() & (1L << user.getPlayer_index())) != 0) {
                AnonRead = true;
            }
        }

        List<String> stages = new ArrayList<>();

        FStage last_stage = null;

        if (!Objects.equals(channel.getName(), "газета")) {

            last_stage = session.createSelectionQuery(
                            "from FStage s where s.room=:room order by s.date desc limit 1", FStage.class)
                    .setParameter("room", channel.getRoom())
                    .getSingleResult();

            for (FStageFChannel stage_channel : stage_channels) {
                FStage stage = stage_channel.getId().getStage();

                if (stage != last_stage) {
                    String jsonString = (XRayRead) ? stage_channel.getJsonXRayStrings() :
                            ((AnonRead) ? stage_channel.getJsonAnonStrings() : stage_channel.getJsonStrings());
                    stages.add(get_output_stage_channel(stage.getName(), stage_channel.getCount(), jsonString));
                }
            }
        } else {
            last_stage = session.createSelectionQuery(
                    "from FStage s where s.name='начало'", FStage.class)
                    .getSingleResult();
        }


        List <FMessage> messages =
                session.createSelectionQuery("from FMessage m where " +
                                "m.channel=:channel and m.stage=:stage" +
                                " and (m.target=-1 or m.target=:target)", FMessage.class)
                        .setParameter("channel", channel)
                        .setParameter("stage", last_stage)
                        .setParameter("target", user.getPlayer_index())
                        .getResultList();

        List <String> result_messages = new ArrayList<>();

        for (FMessage message : messages) {
            result_messages.add((XRayRead)? message.getJsonXRayString() :
                    ((AnonRead)? message.getJsonAnonString() : message.getJsonString()));
        }
        stages.add(get_output_stage_channel(last_stage.getName(), messages.size(), get_array(result_messages)));

        return get_array(stages);
    }

    @Transactional
    public String get_message(long message_id, String username) {
        Session session = sessionFactory.getCurrentSession();
        FMessage message = session.get(FMessage.class, message_id);
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        if (message == null)
            throw new ServiceException("the message_id don't exist.");

        FChannel channel = message.getChannel();

        if (!channel.getRoom().equals(user.getRoom())) {
            throw new ServiceException("user are not unauthorized to read the data.");
        }

        if (user.getPlayer_index() != -1 && (channel.getRead_mask() & (1L << user.getPlayer_index())) == 0) {
            throw new ServiceException("user are not unauthorized to read the data.");
        }

        if (user.getPlayer_index() == -1
                || (channel.getRead_real_username_mask() & (1L << user.getPlayer_index())) != 0)
            return message.getJsonXRayString();

        if ((channel.getAnon_read_mask() & (1L << user.getPlayer_index())) != 0)
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

        if (user.getRoom() != null)
            throw new ServiceException("the creator has joined to another room");

        user.setPlayer_index(-1);
        new_room.setCreator(user);
        new_room.addPlayer(user);

        FChannel channel_lobby = new FChannel();
        channel_lobby.setName("лобби");
        channel_lobby.setRead_mask((1L << 30) - 1);
        channel_lobby.setWrite_mask((1L << 30) - 1);
        channel_lobby.setRead_real_username_mask(0L);
        channel_lobby.setAnon_write_mask(0L);
        channel_lobby.setAnon_read_mask(0L);
        session.persist(channel_lobby);

        FChannel channel_newspaper = new FChannel();
        channel_newspaper.setName("газета");
        channel_newspaper.setRead_mask(0L);
        channel_newspaper.setWrite_mask(0L);
        channel_newspaper.setRead_real_username_mask(0L);
        channel_newspaper.setAnon_write_mask(0L);
        channel_newspaper.setAnon_read_mask(0L);
        session.persist(channel_newspaper);

        FMessage message = new FMessage();
        message.setDate(OffsetDateTime.now());
        message.setUser(user);
        message.setText(room.help);
        session.persist(message);
        channel_lobby.addMessage(message);

        session.persist(new_room);
        new_room.addChannel(channel_lobby);
        new_room.addChannel(channel_newspaper);

        FStage stage = new FStage();
        stage.setName("начало");
        stage.setDate(OffsetDateTime.now());
        session.persist(stage);
        new_room.addStage(stage);

        session.flush();
        session.refresh(new_room);
        return new_room.getId();
    }

    @Transactional
    public long add_message(String username, String text, long channel_id, long target) {
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
        new_message.setUser(user);
        new_message.setTarget(target);

        if (!Objects.equals(channel.getRoom().getStatus(), "not started")) {

            if ((channel.getAnon_write_mask() & (1L << user.getPlayer_index())) != 0) {
                new_message.setAlias("???");
            } else {
                new_message.setAlias(String.valueOf(user.getPlayer_index()));
            }
        } else {
            new_message.setAlias(user.getLogin());
        }

        new_message.setJsonXRayString(get_output_message(username, new_message.getAlias(), new_message.getText(),
                        catMaidService.getUwUDegree(user.getDate_UwU()), user.getAdmin()? "#ff0000" : null));

        new_message.setJsonAnonString(get_output_message(null, "???", new_message.getText(),
                0f, null));

        new_message.setJsonString(get_output_message(null, new_message.getAlias(),
                new_message.getText(), 0f, null));



        session.persist(new_message);
        channel.addMessage(new_message);

        new_message.setStage(session.createSelectionQuery(
                        "from FStage s where s.room=:room order by s.date desc limit 1", FStage.class)
                .setParameter("room", channel.getRoom())
                .getSingleResult());

        session.flush();
        session.refresh(new_message);
        return new_message.getId();
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
    public void add_player(String username, long room_id) {
        Session session = sessionFactory.getCurrentSession();
        FUser player = session.bySimpleNaturalId(FUser.class).load(username);

        if (player == null) {
            throw new ServiceException("the player does not exist");
        }

        FRoom room = session.get(FRoom.class, room_id);

        if (player.getRoom() != null) {
            if (!player.getRoom().equals(room)) {
                throw new ServiceException("the player have joined to another room yet");
            } else {
                return;
            }
        }

        if (!Objects.equals(room.getStatus(), "not started")) {
            throw new ServiceException("the game has already started");
        }

        long cnt = (long) session
                .createQuery("select count(*) from FUser u where u.room = :room")
                .setParameter("room", room).getSingleResult();

        if (cnt >= room.getMax_players()) {
            throw new ServiceException("there are no places");
        }

        player.setPlayer_index(-1);
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

            if (room.getCreator().equals(player)) {
                room.setStatus("close");
            }

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

    @Transactional
    public boolean authorize_player(String username, long room_id, boolean readonly) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);

        if (user.getRoom() == null)
            return false;

        if (!readonly && user.getRoom().getStatus().equals("close")) {
            return false;
        }

        return user.getRoom().getId() == room_id;
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

        long players_count = room.getPlayers().size();

        ArrayList <Long> permutation = new ArrayList<>();
        for (long i = 0; i < players_count; i++)
            permutation.add(i);
        shuffle(permutation, new Random(System.currentTimeMillis() + 9427347432442L));
        permutation.set(permutation.indexOf(0L), permutation.getFirst());
        permutation.set(0, 0L);

        for (FUser player : room.getPlayers()) {
            if (player.equals(room.getCreator())) {
                player.setPlayer_index(0);
            }
        }

        int index = 0;
        for (FUser player : room.getPlayers()) {
            if (player.equals(room.getCreator())) {
                continue;
            }
            player.setPlayer_index(permutation.get(++index));
        }

        return players_count;
    }

    @Transactional
    public OutputStateRoom get_state_for_player(long room_id, String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        FRoom room = session.get(FRoom.class, room_id);
        OutputStateRoom outputStateRoom = new OutputStateRoom();
        outputStateRoom.setStage(session.createSelectionQuery(
                        "from FStage s where s.room=:room order by s.date desc limit 1", FStage.class)
                .setParameter("room", room)
                .getSingleResult().getName());
        outputStateRoom.setFinish_stage(room.getFinish_stage());
        outputStateRoom.setStatus(room.getStatus());

        List<OutputStateChannel> outputStateChannels = new ArrayList<>();
        for (FChannel channel : room.getChannels()) {
            OutputStateChannel outputChannel = new OutputStateChannel();

            outputChannel.setChannel_id(channel.getId());
            outputChannel.setName(channel.getName());

            if (user.getPlayer_index() == -1) {
                outputChannel.setCan_read(channel.getRead_mask()  != 0);
                outputChannel.setCan_write(channel.getWrite_mask() != 0);
            } else {
                outputChannel.setCan_read((channel.getRead_mask() & (1L << user.getPlayer_index())) != 0);
                outputChannel.setCan_write((channel.getWrite_mask() & (1L << user.getPlayer_index())) != 0);
            }

            if (outputChannel.getCan_read() || outputChannel.getCan_write())
                outputStateChannels.add(outputChannel);
        }
        outputStateRoom.setChannels(outputStateChannels);

        List<OutputStatePoll> outputStatePolls = new ArrayList<>();
        for (FPoll poll : room.getPolls()) {

            if ((poll.getMask_observers() & (1L << user.getPlayer_index())) == 0)
                continue;

            OutputStatePoll outputPoll = new OutputStatePoll();

            outputPoll.setPoll_id(poll.getId());
            outputPoll.setName(poll.getName());
            outputPoll.setCan_vote((poll.getMask_voters() & (1L << user.getPlayer_index())) != 0);


            try {
                outputPoll.setCandidates(objectMapper.readValue(poll.getCandidates(), Map.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            outputStatePolls.add(outputPoll);
        }
        outputStateRoom.setPolls(outputStatePolls);

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
            stage_channel.setId(new FStageFChannelId(last_stage, channel));
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

                    channel.setRead_mask(inputChannel.getRead_mask());
                    channel.setWrite_mask(inputChannel.getWrite_mask());
                    channel.setRead_real_username_mask(inputChannel.getRead_real_username_mask());
                    channel.setAnon_write_mask(inputChannel.getAnon_write_mask());
                    channel.setAnon_read_mask(inputChannel.getAnon_read_mask());
                } else {
                    continue;
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

            channel.setRoom(room);
            channel.setName(inputChannel.getName());
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

            new_message.setJsonString(get_output_message(
                    null, new_message.getAlias(),
                    new_message.getText(), 0f, null));
            new_message.setJsonAnonString(get_output_message(
                    null, "???",
                    new_message.getText(), 0f, null));
            new_message.setJsonXRayString(get_output_message(
                    new_message.getUser().getLogin(),
                    new_message.getAlias(),
                    new_message.getText(),
                    catMaidService.getUwUDegree(new_message.getUser().getDate_UwU()),
                    new_message.getUser().getAdmin()? "#ff0000" : null));

            new_message.setStage(session.createSelectionQuery(
                            "from FStage s where s.name='начало'", FStage.class)
                    .getSingleResult());
            session.persist(new_message);
            newspaper.addMessage(new_message);
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

        int index = (int) player.getPlayer_index();

        if ((poll.getMask_voters() & (1L << index)) == 0) {
            return false;
        }

        long[] poll_table = poll.getPoll_table();
        long[] reverse_poll_table = poll.getReverse_poll_table();

        if (reverse_poll_table[index] != 0) {
            return false;
        }

        poll_table[(int) candidate] |= (1L << index);
        reverse_poll_table[index] |= (1L << candidate);

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

        player.setPlayer_index(index);
    }

    @Transactional
    public Object get_code(long room_id) {
        Session session = sessionFactory.getCurrentSession();
        FRoom room = session.get(FRoom.class, room_id);

        return room.getCode();
    }
}
