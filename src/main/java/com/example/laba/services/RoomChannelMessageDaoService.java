package com.example.laba.services;

import com.example.laba.entities.*;
import com.example.laba.json_objects.*;
import com.example.laba.objects_to_fill_templates.TmplMessage;
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
public class RoomChannelMessageDaoService {
    @Autowired
    CatMaidService catMaidService;

    @PersistenceUnit
    SessionFactory sessionFactory;

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
    public long add_room(InputRoom room, String creator) {
        FRoom new_room = new FRoom();
        new_room.setName(room.name);
        new_room.setDescription(room.description);
        new_room.setMin_players(room.min_players);
        new_room.setMax_players(room.max_players);
        new_room.setHandle_code(room.handle_code);
        new_room.setInit_code(room.init_code);
        new_room.setStatus("not started");

        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(creator);

        if (user == null)
            throw new ServiceException("the user_id don't exist.");

        if (user.getRoom() != null)
            throw new ServiceException("the creator has joined to another room");

        new_room.setCreator(user);
        new_room.addPlayer(user);

        FChannel channel_lobby = new FChannel();
        channel_lobby.setName("лобби");
        channel_lobby.setRead_mask((1L << 30) - 1);
        channel_lobby.setWrite_mask((1L << 30) - 1);
        session.persist(channel_lobby);

        FChannel channel_help = new FChannel();
        channel_help.setName("помощь");
        channel_help.setRead_mask((1L << 30) - 1);
        channel_help.setWrite_mask((1L << 30) - 1);
        session.persist(channel_help);

        FMessage message = new FMessage();
        message.setDate(OffsetDateTime.now());
        message.setUser(user);
        message.setText(room.help);
        message.setChannel(channel_help);
        session.persist(message);

        new_room.addChannel(channel_lobby);
        new_room.addChannel(channel_help);

        session.persist(new_room);

        session.flush();
        session.refresh(new_room);
        return new_room.getId();
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
    public boolean set_room_status(String username, long room_id, String status) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        FRoom room = session.get(FRoom.class, room_id);

        if (!user.equals(room.getCreator())) {
            return false;
        }

        room.setStatus(status);
        return true;
    }

    @Transactional
    public List<String> numerate_player(long room_id) {
        Session session = sessionFactory.getCurrentSession();
        FRoom room = session.get(FRoom.class, room_id);

        long index = 0;
        List <String> result = new ArrayList<>();

        for (FUser player : room.getPlayers()) {
            result.add(player.getLogin());
            player.setPlayer_index(index++);
        }

        return result;
    }

    @Transactional
    public OutputStateRoom get_state_for_player(long room_id, String username) {
        Session session = sessionFactory.getCurrentSession();
        FUser user = session.bySimpleNaturalId(FUser.class).load(username);
        FRoom room = session.get(FRoom.class, room_id);
        OutputStateRoom outputStateRoom = new OutputStateRoom();
        outputStateRoom.setStage(room.getStage());
        outputStateRoom.setFinish_stage(room.getFinish_stage());
        outputStateRoom.setStatus(room.getStatus());

        List<OutputStateChannel> outputStateChannels = new ArrayList<>();
        for (FChannel channel : room.getChannels()) {
            OutputStateChannel outputChannel = new OutputStateChannel();

            outputChannel.setChannel_id(channel.getId());
            outputChannel.setName(channel.getName());
            outputChannel.setCan_read((channel.getRead_mask() & (1L << user.getPlayer_index())) != 0);
            outputChannel.setCan_write((channel.getWrite_mask() & (1L << user.getPlayer_index())) != 0);

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

            List<Long> candidates = new ArrayList<>();
            for (long i = 0; i < 30; i++) {
                if ((poll.getMask_candidates() & (1L << i)) != 0) {
                    candidates.add(i);
                }
            }
            outputPoll.setCandidates(candidates);

            outputStatePolls.add(outputPoll);
        }
        outputStateRoom.setPolls(outputStatePolls);

        return outputStateRoom;
    }

    @Transactional
    public void set_state(long room_id, InputStateRoom inputStateRoom, boolean init) {
        Session session = sessionFactory.getCurrentSession();
        FRoom room = session.get(FRoom.class, room_id);

        room.setStage(inputStateRoom.getStage());
        room.setFinish_stage(OffsetDateTime.now().plusSeconds(inputStateRoom.getDuration()));

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
                } else {
                    continue;
                }
            }

            channel.setRoom(room);
            channel.setName(inputChannel.getName());
            channel.setRead_mask(inputChannel.getRead_mask());
            channel.setWrite_mask(inputChannel.getWrite_mask());
        }

        session.createMutationQuery("delete FPoll p where p.room = :room").setParameter("room", room).executeUpdate();

        for (InputStatePoll inputPoll : inputStateRoom.getPolls()) {

            FPoll poll = new FPoll();

            poll.setRoom(room);
            poll.setName(inputPoll.getName());
            poll.setMask_observers(inputPoll.getMask_observers());
            poll.setMask_voters(inputPoll.getMask_voters());
            poll.setMask_candidates(inputPoll.getMask_candidates());

            session.persist(poll);
        }
    }

    @Transactional
    public boolean add_vote(long poll_id, String username, long candidate) {
        Session session = sessionFactory.getCurrentSession();
        FPoll poll = session.get(FPoll.class, poll_id);
        FUser player = session.bySimpleNaturalId(FUser.class).load(username);


        if (candidate < 0) {
            candidate = 29;
        }

        if (player == null || poll == null || candidate >= 30) {
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
}
