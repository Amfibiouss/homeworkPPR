function pop_count(n){
    let cnt = 0;
    do {
        if (n & 1)
            ++cnt;
    } while(n >>= 1);

    return cnt;
}

function get_mask_of_fraction(fraction_name) {

    let mask = 0;

    for (let i = 0; i < player_count; i++) {

        if (fraction[i] === fraction_name)
            mask = mask | (1 << i);
    }

    return all_mask & mask;
}

function get_mask_of_blocked() {

    let mask = 0;

    for (let i = 0; i < player_count; i++) {

        if (blocked[i] === 1)
            mask = mask | (1 << i);
    }

    return all_mask & mask;
}

function get_mask_of_abducted() {

    let mask = 0;

    for (let i = 0; i < player_count; i++) {

        if (abducted[i] === 1)
            mask = mask | (1 << i);
    }

    return all_mask & mask;
}

function get_mask_of_dead() {

    let mask = 0;

    for (let i = 0; i < player_count; i++) {

        if (dead[i] === 1)
            mask = mask | (1 << i);
    }

    return all_mask & mask;
}

function get_mask_of_active() {
    return all_mask ^ (get_mask_of_abducted() | get_mask_of_dead() | get_mask_of_blocked());
}

function shuffleArray(array) {
    for (let i = player_count - 1; i > 1; i--) {
        let j = Math.floor(Math.random() * i + 1);
        let temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}

function get_candidates_from_mask(mask) {

    let candidates = {};

    for (let i = 0; i < player_count; i++) {
        if ((mask & (1 << i)) !== 0) {
            candidates[window.names[i]] = i;
        }
    }

    return candidates;
}

function calculate_polls_channels(init) {

    let active_mask = get_mask_of_active();
    let ghost_mask =  get_mask_of_dead();
    let alive_mask = all_mask ^ ghost_mask;
    let abducted_mask = get_mask_of_abducted();
    let free_mask = all_mask ^ abducted_mask;

    let curr_status = "started";

    let cnt = 0;
    for (const entry of window.stone_owners) {
        if (roles[entry[1]] === 'Танос')
            cnt++;
    }
    if (cnt === 6 || (alive_mask & (collector_mask | avengers_mask)) === 0) {
        window.winner = "Черный Орден";
        curr_status = "finished";
        for (let i = 0; i < player_count; i++) {
            if (fraction[i] === "Черный Орден") {
                dead[i] = 0;
            }
            messages[i] += "Черный орден победил. Все павшие воины Таноса оживают." +
                " Танос собрал все камни, вы чувствуете что вселенная меняется... \\n";
        }

    } else if ((alive_mask & (collector_mask | black_orden_mask)) === 0) {
        window.winner = "Мстители";
        curr_status = "finished";
        for (let i = 0; i < player_count; i++) {

            messages[i] += "Мстители победили. Многие пали, но баланс Вселенной восстановлен.\\n";
        }
    } else if ((free_mask & alive_mask & (avengers_mask | black_orden_mask)) === 0) {
        window.winner = "Коллекционер";
        curr_status = "finished";
        for (let i = 0; i < player_count; i++) {

            messages[i] += "Коллекционер победил. Его коллекция пополнилась ценными экспонатами.\\n";
        }
    }

    ghost_mask =  get_mask_of_dead();
    alive_mask = all_mask ^ ghost_mask;
    active_mask = get_mask_of_active();

    let new_state;

    if (curr_stage === "day") {
        ++day_count;

        new_state = {
            "channels" : [
                {"name" : "Общий", "write_mask": alive_mask},
                {"name" : "Черный Орден", "write_mask": black_orden_mask & alive_mask}],
            "polls" : [
                {"name": "Дневное ГС",
                    "mask_voters": active_mask,
                    "mask_observers": all_mask,
                    "mask_candidates": alive_mask & free_mask,
                    "candidates": get_candidates_from_mask(alive_mask & free_mask)}],
            "messages" : messages,
            "duration" : 20,
            "stage" : "день " + day_count + ((window.time_branch > 1)? (" (временная ветвь " + window.time_branch + ")") : ""),
            "status" : curr_status,
            "names" : names};

        if (init === 1) {
            new_state.channels = [
            {"name" : "газета", "read_real_username_mask":all_mask, "read_mask" : all_mask, "anon_read_mask" : 0, "write_mask" : 0, "anon_write_mask" : 0, "cindex": 2},
            {"name" : "лобби", "read_real_username_mask":0, "read_mask" : all_mask, "anon_read_mask" : 0, "write_mask" : all_mask, "anon_write_mask" : all_mask, "cindex": 3},
            {"name" : "Общий", "read_real_username_mask":1, "read_mask" : all_mask, "anon_read_mask" : 0, "write_mask" : all_mask, "anon_write_mask" : 0, "cindex": 4},
            {"name" : "Черный Орден", "read_real_username_mask":1, "read_mask" : black_orden_mask, "anon_read_mask" : 0, "write_mask" : black_orden_mask, "anon_write_mask" : 0, "cindex": 5},
            {"name" : "Мертвые", "read_real_username_mask": 1, "read_mask" : 0, "anon_read_mask" : 0, "write_mask" : 0, "anon_write_mask" : 0, "cindex": 6}];
        }
    } else {
        new_state = {
            "channels" : [
                {"name" : "Общий", "write_mask": 0},
                {"name" : "Черный Орден", "write_mask": black_orden_mask & alive_mask}],
            "polls" : [],
            "messages" : messages,
            "duration" : 15,
            "stage" : "ночь " + day_count  + ((window.time_branch > 1)? (" (временная ветвь " + window.time_branch + ")") : ""),
            "status" : curr_status,
            "names" : names
        };
        if (init === 1) {
            new_state.channels = [
                {"name" : "газета", "read_real_username_mask":all_mask, "read_mask" : all_mask, "anon_read_mask" : 0, "write_mask" : 0, "anon_write_mask" : 0},
                {"name" : "лобби", "read_real_username_mask":0, "read_mask" : all_mask, "anon_read_mask" : 0, "write_mask" : all_mask, "anon_write_mask" : all_mask},
                {"name" : "Общий", "read_real_username_mask":1, "read_mask" : all_mask, "anon_read_mask" : 0, "write_mask" : 0, "anon_write_mask" : 0},
                {"name" : "Черный Орден", "read_real_username_mask":1, "read_mask" : black_orden_mask, "anon_read_mask" : 0, "write_mask" : black_orden_mask, "anon_write_mask" : 0},
                {"name" : "Мертвые", "read_real_username_mask": 1, "read_mask" : 0, "anon_read_mask" : 0, "write_mask" : 0, "anon_write_mask" : 0}];
        }

        if (abducted[stone_owners.get("Камень Силы")] === 0) {
            if (fraction[stone_owners.get("Камень Силы")] === "Черный Орден") {
                new_state.polls.push({
                    "name": "Ночное ГС",
                    "mask_voters": black_orden_mask & active_mask,
                    "mask_observers": black_orden_mask,
                    "mask_candidates": (all_mask ^ black_orden_mask) & alive_mask & free_mask,
                    "candidates": get_candidates_from_mask((all_mask ^ black_orden_mask) & alive_mask & free_mask)
                });
            } else {
                let owner_mask = 1 << stone_owners.get("Камень Силы");

                new_state.polls.push({
                    "name": "Ночное ГС",
                    "mask_voters": owner_mask,
                    "mask_observers": owner_mask,
                    "mask_candidates": (all_mask ^ owner_mask) & alive_mask & free_mask,
                    "candidates": get_candidates_from_mask((all_mask ^ owner_mask) & alive_mask & free_mask)
                });
            }
        }

        if (abducted[stone_owners.get("Камень Души")] === 0 && roles[stone_owners.get("Камень Души")] !== 'Гамора' && (day_count - window.soul_stone_last_usage) >= 2) {
            let owner_mask = 1 << stone_owners.get("Камень Души");

            new_state.polls.push({
                "name": "Камень Души",
                "mask_voters": owner_mask,
                "mask_observers": owner_mask,
                "mask_candidates": ghost_mask,
                "candidates": get_candidates_from_mask(ghost_mask)
            });
        }

        if (abducted[stone_owners.get("Камень Реальности")] === 0) {
            if (fraction[stone_owners.get("Камень Реальности")] === 'Коллекционер') {
                let owner_mask = 1 << stone_owners.get("Камень Реальности");

                new_state.polls.push({
                    "name": "Камень Реальности",
                    "mask_voters": owner_mask,
                    "mask_observers": owner_mask,
                    "mask_candidates": (alive_mask & free_mask) ^ owner_mask,
                    "candidates": get_candidates_from_mask((alive_mask & free_mask) ^ owner_mask)
                });
            } else {
                let owner_mask = 1 << stone_owners.get("Камень Реальности");

                new_state.polls.push({
                    "name": "Камень Реальности",
                    "mask_voters": owner_mask,
                    "mask_observers": owner_mask,
                    "mask_candidates": (alive_mask & free_mask) ^ owner_mask,
                    "candidates": get_candidates_from_mask((alive_mask & free_mask) ^ owner_mask)
                });
            }
        }

        if (abducted[stone_owners.get("Камень Времени")] === 0) {
            let owner_mask = 1 << stone_owners.get("Камень Времени");

            new_state.polls.push({
                "name": "Камень Времени",
                "mask_voters": owner_mask,
                "mask_observers": owner_mask,
                "mask_candidates": 1 ^ ((window.time_stone_usage === 1 || window.yesterday === -1) ? 0 : 2),
                "candidates": ((window.time_stone_usage === 1) ? {"Предсказать": 0} : {"Предсказать": 0, "Откат": 1})
            });
        }

        if (abducted[stone_owners.get("Камень Разума")] === 0) {
            let owner_mask = 1 << stone_owners.get("Камень Разума");

            new_state.polls.push({
                "name": "Камень Разума",
                "mask_voters": owner_mask,
                "mask_observers": owner_mask,
                "mask_candidates": (alive_mask & free_mask) ^ owner_mask,
                "candidates": get_candidates_from_mask((alive_mask & free_mask) ^ owner_mask)
            });
        }

        let cnt = 0;
        for (const entry of window.stone_owners) {
            if (fraction[entry[1]] === 'Черный Орден')
                cnt++;
        }

        if (cnt >= 4)
            window.ThunderAxeReady = true;

        if (window.ThunderAxeReady === true && abducted[index_by_role['Тор']] === 0 && dead[index_by_role['Тор']] === 0) {
            let owner_mask = 1 << index_by_role['Тор'];

            new_state.polls.push({
                "name": "Громсекира",
                "mask_voters": owner_mask,
                "mask_observers": owner_mask,
                "mask_candidates": (alive_mask & free_mask) ^ owner_mask,
                "candidates": get_candidates_from_mask((alive_mask & free_mask) ^ owner_mask)
            });
        }

        for (const entry of window.stone_owners) {
            if ((entry[0] === 'Камень Души' && roles[entry[1]] !== 'Гамора')
                || (entry[0] === 'Камень Разума' && roles[entry[1]] !== 'Вижн')
                || (entry[0] !== 'Камень Души' && entry[0] !== 'Камень Разума')) {
                let owner_mask = 1 << entry[1];

                if ((owner_mask & free_mask) !== 0) {
                    new_state.polls.push({
                        "name": "Отдать " + entry[0],
                        "mask_voters": owner_mask,
                        "mask_observers": owner_mask,
                        "mask_candidates": (alive_mask & free_mask) ^ owner_mask,
                        "candidates": get_candidates_from_mask((alive_mask & free_mask) ^ owner_mask)
                    });
                }
            }
        }
    }

    if (init === 0) {
        new_state.channels.push({"name": "Мертвые", "read_mask": ghost_mask, "write_mask": ghost_mask});
    }

    if (curr_stage === 'day')
        window.yesterday = copy_state();

    return new_state;
}

function initialize_room(data) {
    //console.log(data);
    window.player_count = Number(data);
    console.log(window.player_count);
    window.player_count = 12;

    let firstName = [["соленый", "соленая"], ["жареный", "жареная"], ["варёный", "вареный"],
        ["фиолетовый", "фиолетовая"], ["восхитительный", "восхитительная"], ["ароматный", "ароматная"],
        ["нереальный", "нереальная"], ["черно-белый", "черно-белая"], ["пахучий", "пахучая"], ["злой", "злая"],
        ["трусливый", "трусливая"], ["гениальный", "гениальная"], ["сладкий", "сладкая"],
        ["гнилой", "гнилая"], ["смешной", "смешная"], ["мертвый", "мертвая"], ["жирный", "жирная"],
        ["бессмертный", "бессмертная"], ["сумасшедший", "сумасшедшая"], ["крутой", "крутая"], ["отважный", "отважная"],
        ["странный", "странная"], ["лукавый", "лукавая"], ["пучеглазый", "пучеглазая"], ["сочный", "сочная"],
        ["страшный", "страшная"], ["жуткий", "жуткая"], ["падший", "падшая"], ["тёмный", "темная"],
        ["подозрительный", "подозрительная"], ["волшебный", "волшебная"], ["железный", "железная"],
        ["кровожадный", "кровожадная"], ["фанатичный", "фанатичная"], ["вкусный", "вкусная"], ["непобедимый", "непобедимая"],
        ["супер", "супер"], ["хороший", "хорошая"], ["шоколадный", "шоколадная"], ["мармеладный", "мармеладная"]];
    let maleSecondName = ["суп", "торт", "пирожочек", "салат", "банан", "ИИ", "клоун", "начальник", "бобер",
        "псих", "гений", "чел", "некто", "фрукт", "овощ", "хитрец", "трус", "кукловод", "друг", "незнакомец",
        "[ДАННЫЕ УДАЛЕНЫ]", "пони", "бомж", "король", "мудрец", "доктор", "учёный", "единорог", "маг", "качок",
        "космонавт", "психиатр", "воин", "робот", "творожок с изюмом", "мифка"];
    let femaleSecondName = ["котлета", "колбаса", "булочка", "фея", "волшебница", "начальница",
        "папайя", "королева", "медсестра", "учёная", "горничная", "подруга", "незнакомка", "бомжиха", "[ДАННЫЕ УДАЛЕНЫ]",
        "лошадка", "принцесса", "злодейка", "корова", "трусиха", "мышь", "птица", "рыба", "энтропия", "тыква", "луна"];

    window.names = {};
    for (let i = 0; i < player_count; i++) {
        let name;

        do {
            if (Math.floor(Math.random() * 100) % 3 <= 1) {
                let ind1 = Math.floor(Math.random() * firstName.length);
                let ind2 = Math.floor(Math.random() * maleSecondName.length);
                name = firstName[ind1][0] + ' ' + maleSecondName[ind2];
            } else {
                let ind1 = Math.floor(Math.random() * firstName.length);
                let ind2 = Math.floor(Math.random() * femaleSecondName.length);
                name = firstName[ind1][1] + ' ' + femaleSecondName[ind2];
            }

        } while (Object.values(window.names).indexOf(name) !== -1);

        window.names[i] = name;
    }

    window.roles = new Array(30);
    roles[0] = 'Живой трибунал';
    roles[1] = 'Танос';
    roles[2] = 'Кулл Обсидиан';
    roles[3] = 'Эбеновый Зоб';
    roles[4] = 'Танелиир Тиван';
    roles[5] = 'Доктор Стрендж';
    roles[6] = 'Гамора';
    roles[7] = 'Звездный лорд';
    roles[8] = 'Вижн';
    roles[9] = 'Тони Старк';
    roles[10] = 'Локи';
    roles[11] = 'Тор';
    shuffleArray(roles);

    window.stone_owners = new Map();
    window.fraction = ['Верховный Трибунал'];
    window.index_by_role = new Map();

    for (let i = 1; i < player_count; i++) {
        let role = roles[i];

        index_by_role.set(role, i);

        if (role === 'Танос')
            stone_owners.set('Камень Силы', i);

        if (role === 'Доктор Стрендж')
            stone_owners.set('Камень Времени', i);

        if (role === 'Локи')
            stone_owners.set('Камень Пространства', i);

        if (role === 'Гамора')
            stone_owners.set('Камень Души', i);

        if (role === 'Вижн')
            stone_owners.set('Камень Разума', i);

        if (role === 'Танелиир Тиван') {
            stone_owners.set('Камень Реальности', i);
            window.collector_name = names[i];
        }

        if (role === 'Танос' || role === 'Кулл Обсидиан' || role === 'Эбеновый Зоб') {
            fraction.push('Черный Орден');
        } else if (role === 'Танелиир Тиван') {
            fraction.push('Коллекционер');
        } else {
            fraction.push('Мстители');
        }
    }

    window.messages = new Array(player_count).fill("");
    messages[0] += "Как Живой Трибунал, вы ,конечно, знаете роль каждого ничтожного смертного.\\n"
    for (let i = 0; i < player_count; i++) {
        messages[0] += "Роль игрока " + names[i] + " (" + i + ") " + roles[i] + ". \\n";
    }

    window.blocked = new Array(30).fill(0);
    window.abducted = new Array(30).fill(0);
    window.dead = new Array(30).fill(0);
    window.all_mask = (1 << player_count) - 1;
    window.black_orden_mask = get_mask_of_fraction("Черный Орден");
    window.avengers_mask = get_mask_of_fraction("Мстители");
    window.collector_mask = get_mask_of_fraction("Коллекционер");
    window.curr_stage = "day";
    window.day_count = 1;
    window.soul_stone_last_usage = -1;
    window.time_stone_usage = 0;
    window.time_prediction = 0;
    window.yesterday = -1;
    window.time_branch = 1;
    window.ThunderAxeReady = false;

    for (let i = 0; i < player_count; i++) {

        messages[i] += "Приветствую вас " + names[i] + " (" + i + ") , ваша роль: " + roles[i] + ". \\n";

        if (fraction[i] === "Черный Орден") {
            messages[i] +=
                "Вы принадлежите к фракции Черный Орден. " +
                "Ваша цель собрать все 6 Камней Бесконечности в перчатке Таноса. " +
                "Благодаря Камню Силы который уже находится в перчатке Таноса, " +
                "вы можете путем голосования выберать кого убить ночью. " +
                "Но если вы потеряете Камень Силы, то потеряете эту возможность. \\n";
        } else if (fraction[i] === "Мстители") {
            messages[i] += "Вы принадлежите к фракции Мстителей. " +
                "Ваша цель уничтожить Черный Орден и Коллекционера. "
                + "Каждый день убивайте одного рандо... кхм подозрительного человека на дневном голосовании. \\n";
        } else if (fraction[i] === "Коллекционер") {
            messages[i] += "Вы принадлежите к фракции Коллекционера. " +
                "Ваша цель похитить для коллекции весь Черный Орден и Мстителей. " +
                "Каждую ночь похищайте одного человека и принимайте его внешность с помощью Камня Реальности. \\n";
        } else {
            messages[i] += "Вы должны следить за порядком во вселенной и не огорчать Всевышнего.";
        }
    }

    return calculate_polls_channels(1)
}

function get_poll(polls, name) {
    for (const poll of polls) {
        if (poll.name === name)
            return poll;
    }
    return null;
}

function get_max_voting_candidate(poll) {
    let max_value = 0;
    let candidate = -1;
    let active_mask = get_mask_of_active();

    for(let i = 0; i < player_count; i++) {

        let cnt = pop_count(poll.poll_table[i] & active_mask);

        if (cnt === max_value) {
            candidate = -1;
        } else if (cnt > max_value) {
            max_value = cnt;
            candidate = i;
        }
    }

    return candidate;
}

function give_stones_to_random_murders(candidate, murders_mask) {
    murders_mask = murders_mask & (all_mask ^ (get_mask_of_dead() | get_mask_of_abducted()));

    if (murders_mask === 0) {
        murders_mask = all_mask ^ get_mask_of_dead();
    }

    for (const entry of window.stone_owners) {
        if (entry[1] === candidate) {
             let cnt = pop_count(murders_mask);
             let ind =  Math.floor(Math.random() * cnt);

             for (let i = 0; i < player_count; i++) {
                 if ((murders_mask & (1 << i)) !== 0) {

                     if (ind === 0) {
                         stone_owners.set(entry[0], i);
                         messages[i] += "Теперь вы новый владелец предмета " + entry[0] + ".\\n";
                         messages[0] += "Теперь " + names[i] + " новый владелец предмета " + entry[0] + ".\\n";
                         break;
                     }

                     --ind;
                 }
             }
        }
    }
}

function kill_player(candidate, poll) {
    let death_reasons = ['Игрок был зверски зарезан.', 'Игрок замучен досмерти.',
        'Игрок найден в луже крови в сортире.', 'Игрок был задушен.'];
    let reason = death_reasons[Math.floor(Math.random() * death_reasons.length)];

    for (let i = 0; i < player_count; i++) {
        messages[i] += 'Нас покинул ' + names[candidate] + ". " + reason + "\\n";

        if (fraction[i] === 'Черный Орден') {
            messages[i] += "Обыскав труп и дом жертвы вы узнали, что " + names[candidate]
                + " является " + roles[candidate] + ".\\n";
        }
    }

    dead[candidate] = 1;
    give_stones_to_random_murders(candidate, poll.poll_table[candidate]);
    messages[candidate] += ((reason === 'Игрок найден в луже крови в сортире.') ?
        'Вас позорно замочили в сортире...' : 'Вы пали смертью храбрых, сопротивляясь как лев.') + '\\n';
}

function free_player(candidate) {

    for (let i = 0; i < player_count; i++) {
        if ((all_mask & (1 << i)) !== 0)
            messages[i] += "Игрок " + names[candidate] + " освобожден.\\n";
    }
    messages[candidate] += "Вас освободили из плена Коллекционера. \\n";
}

function visit_abducted_player(candidate, mask, reason) {
    for (let i = 0; i < player_count; i++) {
        if ((mask & (1 << i)) !== 0)
            messages[i] += "Вы постетили Игрока " + names[candidate] + ", чтобы " + reason + ". " +
                "Но в его доме пусто, только следы борьбы. Кажется его похитили, или он хочет сделать вид" +
                " будто его похитили...\\n";
    }
}

function copy_state() {

    let state = {};

    state.names = Object.assign({}, window.names);
    state.blocked = Object.assign([], window.blocked);
    state.dead = Object.assign([], window.dead);
    state.abducted = Object.assign([], window.abducted);
    state.stone_owners = new Map(window.stone_owners);
    state.curr_stage = window.curr_stage;
    state.day_count = window.day_count;
    state.soul_stone_last_usage = window.soul_stone_last_usage;
    state.time_stone_usage = window.time_stone_usage;
    state.ThunderAxeReady = window.ThunderAxeReady;
    state.yesterday = structuredClone(window.yesterday);

    console.log(state);

    return state;
}

function set_state(state) {

    window.names = Object.assign({}, state.names);
    window.blocked = Object.assign([], state.blocked);
    window.dead = Object.assign([], state.dead);
    window.abducted = Object.assign([], state.abducted);
    window.stone_owners = new Map(state.stone_owners);
    window.curr_stage = state.curr_stage;
    window.day_count = state.day_count;
    window.soul_stone_last_usage = state.soul_stone_last_usage;
    window.time_stone_usage = state.time_stone_usage;
    window.ThunderAxeReady = state.ThunderAxeReady;
    window.yesterday = structuredClone(state.yesterday);
}

function get_stone_user(mask) {
    for (let i = 0; i < player_count; i++) {
        if ((mask & (1 << i)) !== 0)
            return i;
    }
    return -1;
}


function update_state(data) {
    window.messages = new Array(player_count).fill("");

    let poll;

    let ghost_mask;
    let alive_mask;
    let abducted_mask;
    let free_mask;
    let active_mask;

    blocked.fill(0);

    active_mask = get_mask_of_active();
    ghost_mask =  get_mask_of_dead();
    alive_mask = all_mask ^ ghost_mask;
    abducted_mask = get_mask_of_abducted();
    free_mask = all_mask ^ abducted_mask;
    poll = get_poll(data, 'Камень Времени');
    if (poll != null) {
        let candidate = get_max_voting_candidate(poll);

        if (candidate === 0 && window.time_prediction === 0) {
            let save_point = copy_state();
            window.time_prediction = 1;

            let MAX_PREDICTION_ITERATIONS = 100;
            let MAX_FUTURE_BRANCHES = 100;

            let wins = {'Мстители':0, 'Коллекционер':0, 'Черный Орден':0, 'Бесконечность':0};

            for (let k = 0; k < MAX_FUTURE_BRANCHES; k++) {
                let new_state = update_state(data);
                let iteration = 0;

                while (new_state.status !== 'finished') {
                    let res = [];

                    ++iteration;

                    if (iteration === MAX_PREDICTION_ITERATIONS) {
                        break;
                    }

                    for (let poll of new_state.polls) {
                        let cnt = pop_count(poll.mask_candidates);
                        if ((poll.mask_candidates & 1) !== 0)
                            cnt--;
                        let poll_table = Array(player_count).fill(0);

                        if (poll.name === 'Ночное ГС' || poll.name === 'Дневное ГС' || poll.name === 'Камень Реальности') {
                            let random_candidate = Math.floor(Math.random() * cnt);

                            for (let i = 1; i < player_count; i++) {
                                if ((poll.mask_candidates & (1 << i)) !== 0) {
                                    if (random_candidate === 0) {
                                        poll_table[i] |= (poll.mask_voters & (all_mask - 1));
                                        break;
                                    }
                                    random_candidate--;
                                }
                            }
                        }

                        res.push({name: poll.name, poll_table: poll_table});
                    }

                    new_state = update_state(res);
                }

                if (iteration !== MAX_PREDICTION_ITERATIONS) {
                    wins[window.winner]++;
                } else {
                    wins['Бесконечность']++;
                }

                set_state(save_point);
                window.messages = new Array(player_count).fill("");
            }

            window.time_prediction = 0;
            messages[stone_owners.get('Камень Времени')] += "Вы перебрали " + MAX_FUTURE_BRANCHES + " вариантов развития событий, из них: " +
                wins['Мстители'] + " победных у Мстителей, " + wins['Черный Орден'] + " победных у Черного Ордена, " +
                wins['Коллекционер'] + " победных у Коллекционера, " + wins['Бесконечность'] + " не определены.\\n";
        }
        if (candidate === 1 && yesterday !== -1 && window.time_stone_usage === 0) {
            set_state(yesterday);
            window.time_stone_usage = 1;

            for (let i = 0; i < player_count; i++) {
                messages[i] += "Вы чувствуете, что время течет в обратном направлении...\\n";
            }

            return calculate_polls_channels(0);
        }
    }

    active_mask = get_mask_of_active();
    ghost_mask =  get_mask_of_dead();
    alive_mask = all_mask ^ ghost_mask;
    abducted_mask = get_mask_of_abducted();
    free_mask = all_mask ^ abducted_mask;
    poll = get_poll(data, 'Дневное ГС');
    if (poll != null) {
        let candidate = get_max_voting_candidate(poll);
        if (candidate < 1) {
            for (let i = 0; i < player_count; i++) {
                messages[i] += 'Несколько кандидатов набрали равное количество голосов. Никто не умер. \\n';
            }
        } else {
            let collector_index = window.index_by_role.get('Танелиир Тиван');

            if (names[candidate] === names[collector_index]) {

                for (let i = 0; i < player_count; i++) {
                    messages[i] += "Игрок " + names[candidate] + " попытался сбежать, но его догнали." +
                        " Оказалось, что это коллекционер " + window.collector_name + ". Он был казнен," +
                        " но Камень Реальности, который он использовал для маскировки, был кем-то украден. " +
                        "Все пленные были освобождены.\\n";

                    if (abducted[i] === 1) {
                        free_player(i);
                        abducted[i] = 0;
                    }
                }

                dead[candidate] = 1;
                give_stones_to_random_murders(candidate, poll.poll_table[candidate]);
                messages[candidate] += 'Вас казнили на дневном голосовании((( \\n';
                names[candidate] = window.collector_name;
            } else {

                let death_reasons = ['Игрок был обезглавлен.', 'Игрок был повешен.',
                    'Игрок поджарен на электрическом стуле.', 'Игрок был растерзан толпой.', 'Игрок был расстрелян.'];
                let reason = death_reasons[Math.floor(Math.random() * death_reasons.length)];

                for (let i = 0; i < player_count; i++) {
                    messages[i] += 'Большинство проголосовало за ' + names[candidate] + ". " + reason
                        + ' После обыска и расследования стало ясно что он ' + fraction[candidate] + ".\\n";
                }

                dead[candidate] = 1;
                give_stones_to_random_murders(candidate, poll.poll_table[candidate]);
                messages[candidate] += 'Вас казнили на дневном голосовании((( \\n';
            }
        }
    }

    active_mask = get_mask_of_active();
    ghost_mask =  get_mask_of_dead();
    alive_mask = all_mask ^ ghost_mask;
    abducted_mask = get_mask_of_abducted();
    free_mask = all_mask ^ abducted_mask;
    poll = get_poll(data, 'Камень Реальности');
    if (poll != null) {
        let candidate = get_max_voting_candidate(poll);

        if (candidate > 0 && dead[candidate] === 0) {

            let stone_user = get_stone_user(poll.poll_table[candidate]);

            if (fraction[stone_user] === 'Коллекционер') {

                if (candidate === stone_owners.get('Камень Силы')) {
                    messages[candidate] = "На вас пытались применить Камень Реальности, но вы отбились. \\n";
                    messages[stone_user] = "Вы неудачно попытались похитить игрока " + names[candidate] + ".\\n";
                } else {
                    abducted[candidate] = 1;

                    for (let i = 0; i < player_count; i++)
                        messages[i] += "Игрок " + names[stone_user] + " пропал. \\n";

                    names[stone_user] = names[candidate];
                    messages[stone_user] += "Вы успешно похитили игрока " + names[candidate];
                    messages[candidate] += 'Вас похитил Коллекционер и посадил в гигантскую хомячью клетку!\\n';
                }
            } else {
                if (candidate !== stone_owners.get('Камень Силы')) {
                    blocked[candidate] = 1;
                    messages[stone_user] += "Вы превратили Игрока " + names[candidate] + " в кубики. " +
                        "Теперь он не сможет ничего делать этой ночью.\\n";
                    messages[candidate] += "Вы превратились Игрока в кубики. Теперь он не сможет ничего делать этой ночью.\\n";
                } else {
                    messages[candidate] = "На вас пытались применить Камень Реальности, но вы отбились. \\n";
                    messages[stone_user] = "Вам не удалось превратить в кубики Игрока " + names[candidate] + ".\\n";
                }
            }
        }
    }

    active_mask = get_mask_of_active();
    ghost_mask =  get_mask_of_dead();
    alive_mask = all_mask ^ ghost_mask;
    abducted_mask = get_mask_of_abducted();
    free_mask = all_mask ^ abducted_mask;
    poll = get_poll(data, 'Ночное ГС');
    if (poll != null) {
        let candidate = get_max_voting_candidate(poll);
        if (candidate > 0 && dead[candidate] === 0) {

            let collector_index = window.index_by_role.get('Танелиир Тиван');

            if (names[candidate] === names[collector_index]) {
                for (let i = 0; i < player_count; i++) {
                    if (fraction[i] === 'Черный Орден') {
                        messages[i] += "Игрок " + names[candidate] + " попытался сбежать, но его догнали." +
                            " Оказалось, что это коллекционер " + window.collector_name + ". Он был убит," +
                            ", все пленные освобождены. \\n";
                    }

                    if (abducted[i] === 1) {
                        free_player(i);
                        abducted[i] = 0;
                    }
                }

                names[collector_index] = window.collector_name;
                kill_player(collector_index, poll);

            } else if (abducted[candidate]) {
                visit_abducted_player(candidate, black_orden_mask, "убить под покровом ночи");
            } else {
                kill_player(candidate, poll);
            }
        }
    }

    active_mask = get_mask_of_active();
    ghost_mask =  get_mask_of_dead();
    alive_mask = all_mask ^ ghost_mask;
    abducted_mask = get_mask_of_abducted();
    free_mask = all_mask ^ abducted_mask;
    poll = get_poll(data, 'Громсекира');
    if (poll != null) {
        let candidate = get_max_voting_candidate(poll);

        if (candidate !== -1 && dead[candidate] === 0) {

            if (abducted[candidate]) {
                visit_abducted_player(candidate, 1 << index_by_role['Тор'], "убить Громсекирой");
            } else {
                for (let i = 0; i < player_count; i++) {
                    messages[i] += 'Игрок ' + names[candidate] + " был найден мертвым с электрическими ожогами!" +
                        "Сюдя по уликам, он был " + roles[candidate] + ".\\n";
                }

                dead[candidate] = 1;
                messages[candidate] += 'Тор забил вас Громсекирой.\\n';
            }
        }
    }

    active_mask = get_mask_of_active();
    ghost_mask =  get_mask_of_dead();
    alive_mask = all_mask ^ ghost_mask;
    abducted_mask = get_mask_of_abducted();
    free_mask = all_mask ^ abducted_mask;
    poll = get_poll(data, 'Камень Души');
    if (poll != null) {
        let candidate = get_max_voting_candidate(poll);
        if (candidate !== -1 && roles[candidate] !== 'Гамора' && roles[candidate] !== 'Вижн' && dead[candidate] === 1) {

            for (let i = 0; i < player_count; i++) {
                messages[i] += 'Игрок ' + names[candidate] + " внезапно воскрес!\\n";
            }

            window.soul_stone_last_usage = day_count;
            dead[candidate] = 0;
            messages[candidate] += 'Магия Камня Души вернула вас к жизни.\\n';
        }
    }

    active_mask = get_mask_of_active();
    ghost_mask =  get_mask_of_dead();
    alive_mask = all_mask ^ ghost_mask;
    abducted_mask = get_mask_of_abducted();
    free_mask = all_mask ^ abducted_mask;
    poll = get_poll(data, 'Камень Разума');
    if (poll != null) {
        let candidate = get_max_voting_candidate(poll);
        if (candidate !== -1 && dead[candidate] === 0) {

            let stone_user = get_stone_user(poll.poll_table[candidate]);

            if ((active_mask & (1 << stone_user)) !== 0 && stone_user === stone_owners.get('Камень Разума')) {
                if (abducted[candidate]) {
                    visit_abducted_player(candidate, 1 << stone_user, " прочитать мысли.");
                } else {

                    messages[stone_user] += "Вы успешно прочитали мысли и обнаружили что Игрок " + names[candidate]
                        + " является " + roles[candidate] + "\\n";

                    if (fraction[candidate] === 'Черный Орден') {
                        messages[candidate] += "Вы ощушаете, что в вашей голове покопались...\\n";
                    }
                }
            }

            window.soul_stone_last_usage = day_count;
            dead[candidate] = 0;
            messages[candidate] += 'Магия Камня Души вернула вас к жизни.\\n';
        }
    }

    for (const entry of window.stone_owners) {
        active_mask = get_mask_of_active();
        ghost_mask =  get_mask_of_dead();
        alive_mask = all_mask ^ ghost_mask;
        abducted_mask = get_mask_of_abducted();
        free_mask = all_mask ^ abducted_mask;

        poll = get_poll(data, "Отдать " + entry[0]);

        if (poll != null) {
            let candidate = get_max_voting_candidate(poll);

            if (candidate !== -1  && blocked[candidate] === 0 && dead[candidate] === 0) {

                let stone_user = get_stone_user(poll.poll_table[candidate]);

                if (entry[1] === stone_user) {
                    if (abducted[candidate] === 1) {
                        candidate = index_by_role.get('Танелиир Тиван');
                    }

                    messages[entry[1]] = "Вы передали камень Игроку " + names[candidate] + "\\n";
                    stone_owners.set(entry[0], candidate);
                    messages[candidate] += "Теперь вы новый владелец предмета " + entry[0] + ".\\n";
                    messages[0] += "Теперь " + names[candidate] + " новый владелец предмета " + entry[0] + ".\\n";
                }
            }
        }
    }

    window.curr_stage = (window.curr_stage === "day") ? "night" : "day";
    return calculate_polls_channels(0);
}