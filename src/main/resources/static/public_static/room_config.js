function pop_count(n){
    let cnt = 0;
    do {
        if (n & 1)
            ++cnt;
        } while(n >>= 1);

    return cnt;
}

function get_mask_of_role(role) {

    let mask = 0;

    for (i = 0; i < 30; i++) {

        if (roles[i] == role)
            mask = mask | (1 << i);
    }

    return all_mask & mask;
}

function get_mask_of_dead(role) {

    let mask = 0;

    for (i = 0; i < 30; i++) {

        if (dead[i] == 1)
            mask = mask | (1 << i);
    }

    return all_mask & mask;
}


function initialize_room(data) {

    players = data;

    console.log(players);

    roles = new Array(30).fill("мирный");
    dead = new Array(30);
    roles[0] = "мафия";
    roles[1] = "мафия";
    roles[2] = "мафия";

    all_mask = (1 << 12) - 1;
    mafia_mask = get_mask_of_role("мафия");
    curr_stage = "day";

    messages = new Array(players.length);
    for (var i = 0; i < players.length; i++) {
        messages[i] = "Ваша роль: " + roles[i] + ". ";
        if (roles[i] == "мафия") {
            messages[i] += "Ваша цель уничтожить всех мирных."
            + " Каждую ночь убивайте одного на ночном голосовании. Не попадитесь!";
        } else {
            messages[i] += "Ваша цель уничтожить всех мафий."
            + " Каждую день убивайте одного рандо... кхм подозрительного человека на дневном голосовании.";
        }
    }
    return {"channels" : [{"name" : "газета", "read_real_username_mask":all_mask, "read_mask" : all_mask, "write_mask" : 0},
		{"name" : "лобби", "read_real_username_mask":all_mask, "read_mask" : 0, "write_mask" : 0},
        {"name" : "общий", "read_real_username_mask":2, "read_mask" : all_mask, "write_mask" : all_mask},
        {"name" : "мафия", "read_real_username_mask":2, "read_mask" : mafia_mask, "write_mask" : mafia_mask},
        {"name" : "мертвые", "read_real_username_mask": 0, "read_mask" : 0, "write_mask" : 0}],
        "polls" : [{"name": "дневное ГС", "mask_voters": all_mask, "mask_observers": all_mask, "mask_candidates" : all_mask}],
        "messages" : messages,
        "duration" : 15,
        "stage" : "день",
        "status" : "started"};
}

function update_state(data) {
	messages = new Array(30).fill("");

    for (const poll of data) {

        if (poll.name == "ночное ГС" || poll.name == "дневное ГС") {
            let max_value = -1;
            let index = -1;

            for(i = 0; i < 25; i++) {

                if (pop_count(poll.poll_table[i]) == max_value) {
                    index = -1;
                }

                if (pop_count(poll.poll_table[i]) > max_value) {
                    max_value = pop_count(poll.poll_table[i]);
                    index = i;
                }
            }

            if (index != -1) {
                dead[index] = 1;

                for (i = 0; i < 30; i++) {
                     messages[i] = "Был убит игрок " + index + ". Его роль - " + roles[index] + ". Вот бедолага! ";
                }

                messages[index] = "К сожалению вас убили! ";
            }
        }
    }

    curr_stage = (curr_stage == "day") ? "night" : "day";

    for (var i = 0; i < players.length; i++) {
        messages[i] += "Вы дожили до " + ((curr_stage == "day") ? "рассвета." : "заката.");
    }

    mafia_mask = get_mask_of_role("мафия");
    citizens_mask =  get_mask_of_role("мирный");
    ghost_mask =  get_mask_of_dead();
    alive_mask = all_mask ^ ghost_mask;

    if (curr_stage == "day") {
        return {"channels" : [
        {"name" : "общий", "read_mask" : all_mask, "write_mask" : alive_mask & alive_mask},
        {"name" : "мафия", "read_mask" : mafia_mask, "write_mask" : mafia_mask & alive_mask}],
        "polls" : [{"name": "дневное ГС",
        "mask_voters": alive_mask,
        "mask_observers": all_mask,
        "mask_candidates" : alive_mask}],
        "messages" : messages,
        "duration" : 15,
        "stage" : "день",
        "status" : (mafia_mask == 0 || citizens_mask == 0)? "finished" : "started"};
    } else {
        return {"channels" : [
        {"name" : "общий", "read_mask" : all_mask, "write_mask" : 0},
        {"name" : "мафия", "read_mask" : mafia_mask, "write_mask" : mafia_mask & alive_mask},
        {"name" : "мертвые", "read_mask" : ghost_mask, "write_mask" : ghost_mask}],
        "polls" : [{"name": "ночное ГС",
            "mask_voters": mafia_mask & alive_mask,
            "mask_observers": mafia_mask,
            "mask_candidates" : citizens_mask & alive_mask}],
            "messages" : messages,
            "duration" : 10,
            "stage" : "ночь",
            "status" : (mafia_mask == 0 || citizens_mask == 0)? "finished" : "started"};
    }
}