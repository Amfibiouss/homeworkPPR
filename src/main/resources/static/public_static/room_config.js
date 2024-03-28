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

        if (roles[i] === role)
            mask = mask | (1 << i);
    }

    return all_mask & mask;
}

function get_mask_of_dead() {

    let mask = 0;

    for (i = 0; i < 30; i++) {

        if (dead[i] === 1)
            mask = mask | (1 << i);
    }

    return all_mask & mask;
}

function shuffleArray(array) {
    for (let i = player_count - 1; i > 0; i--) {
        let j = Math.floor(Math.random() * (i + 1));
        let temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}

function initialize_room(data) {

    //console.log(data);
    player_count = Number(data);

    console.log(player_count);

    player_count = 12

    roles = new Array(30);
    roles.fill("мирный", 0, player_count);
    roles.fill("никто", player_count, 30)
    roles[0] = "мафия";//roles[1] = roles[2] = "мафия";
    shuffleArray(roles)

    let messages = new Array(30).fill("");
    for (let i = 0; i < 12; i++) {
        messages[0] += "Роль игрока " + i + " " + roles[i] + ". \n";
    }

    dead = new Array(30).fill(0);

    all_mask = (1 << 12) - 1;
    let mafia_mask = get_mask_of_role("мафия");
    curr_stage = "day";
    day_count = 1;

    for (let i = 0; i < player_count; i++) {
        messages[i] += "Ваша роль: " + roles[i] + ". ";
        if (roles[i] === "мафия") {
            messages[i] += "Ваша цель уничтожить всех мирных."
                + " Каждую ночь убивайте одного на ночном голосовании. Не попадитесь!";
        } else {
            messages[i] += "Ваша цель уничтожить всех мафий."
                + " Каждую день убивайте одного рандо... кхм подозрительного человека на дневном голосовании.";
        }
    }

    return {
        "channels" : [
            {"name" : "газета", "read_real_username_mask":0, "read_mask" : all_mask, "anon_read_mask" : 0, "write_mask" : 0, "anon_write_mask" : 0},
            {"name" : "лобби", "read_real_username_mask":0, "read_mask" : all_mask, "anon_read_mask" : 0, "write_mask" : all_mask, "anon_write_mask" : all_mask},
            {"name" : "общий", "read_real_username_mask":1, "read_mask" : all_mask, "anon_read_mask" : 0, "write_mask" : all_mask, "anon_write_mask" : 0},
            {"name" : "мафия", "read_real_username_mask":1, "read_mask" : mafia_mask, "anon_read_mask" : 0, "write_mask" : mafia_mask, "anon_write_mask" : 0},
            {"name" : "мертвые", "read_real_username_mask": 1, "read_mask" : 0, "anon_read_mask" : 0, "write_mask" : 0, "anon_write_mask" : 0}],
        "polls" : [{
            "name": "дневное ГС",
            "mask_voters": all_mask,
            "mask_observers": all_mask,
            "mask_candidates" : all_mask}],
        "messages" : messages,
        "duration" : 15,
        "stage" : "день " + day_count,
        "status" : "started"};
}

function update_state(data) {
	messages = new Array(30).fill("");

    for (const poll of data) {

        if (poll.name === "ночное ГС" || poll.name === "дневное ГС") {
            let max_value = -1;
            let index = -1;

            for(i = 0; i < player_count; i++) {

                if (pop_count(poll.poll_table[i]) === max_value) {
                    index = -1;
                }

                if (pop_count(poll.poll_table[i]) > max_value) {
                    max_value = pop_count(poll.poll_table[i]);
                    index = i;
                }
            }

            if (index !== -1) {
                dead[index] = 1;

                for (i = 0; i < 30; i++) {
                     messages[i] = "Был убит игрок " + index + ". Его роль - " + roles[index] + ". Вот бедолага! ";
                }

                messages[index] = "К сожалению вас убили! ";
            }
        }
    }

    let mafia_mask = get_mask_of_role("мафия");
    let citizens_mask =  get_mask_of_role("мирный");
    let ghost_mask =  get_mask_of_dead();
    let alive_mask = all_mask ^ ghost_mask;

    curr_stage = (curr_stage === "day") ? "night" : "day";
    let curr_status = ((mafia_mask & alive_mask) === 0 || (citizens_mask & alive_mask) === 0)? "finished" : "started";

    for (let i = 0; i < player_count; i++) {
        messages[i] += "Вы дожили до " + ((curr_stage === "day") ? "рассвета. " : "заката. ");

        if ((mafia_mask & alive_mask) === 0) {
            messages[i] += "Мирные победили! ";
        }
        if ((citizens_mask & alive_mask) === 0) {
            messages[i] += "Мафия победила! ";
        }
    }

    if (curr_stage === "day") {
        return {
            "channels" : [
                {"name" : "общий", "read_mask" : all_mask, "write_mask" : citizens_mask & alive_mask},
                {"name" : "мафия", "read_mask" : mafia_mask, "write_mask" : mafia_mask & alive_mask},
                {"name" : "мертвые", "read_mask" : ghost_mask, "write_mask" : ghost_mask}],
            "polls" : [
                {"name": "дневное ГС",
                "mask_voters": alive_mask,
                "mask_observers": all_mask,
                "mask_candidates" : alive_mask}],
            "messages" : messages,
            "duration" : 15,
            "stage" : "день " + day_count,
            "status" : curr_status};
    } else {
        return {
            "channels" : [
                {"name" : "общий", "read_mask" : all_mask, "write_mask" : 0},
                {"name" : "мафия", "read_mask" : mafia_mask, "write_mask" : mafia_mask & alive_mask},
                {"name" : "мертвые", "read_mask" : ghost_mask, "write_mask" : ghost_mask}],
            "polls" : [
                {"name": "ночное ГС",
                "mask_voters": mafia_mask & alive_mask,
                "mask_observers": mafia_mask,
                "mask_candidates" : citizens_mask & alive_mask}],
            "messages" : messages,
            "duration" : 10,
            "stage" : "ночь " + day_count++,
            "status" : curr_status};
    }
}