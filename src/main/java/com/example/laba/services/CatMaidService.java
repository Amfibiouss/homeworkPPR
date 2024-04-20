package com.example.laba.services;

import com.example.laba.objects_to_fill_templates.TmplPunishment;
import com.example.laba.objects_to_fill_templates.TmplUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static java.lang.Math.abs;

@Component
public class CatMaidService {
    @Value("${catmaid.firstStageSeconds:10}")
    private long firstStageSeconds;
    @Value("${catmaid.secondStageSeconds:10}")
    private long secondStageSeconds;
    @Value("${catmaid.thirdStageSeconds:10}")
    private long thirdStageSeconds;

    public String addCatMaidAccent(String message) {

        message = " " + message + " ";

        message = message.replaceAll(" Админ ", " Братик Админ ");
        message = message.replaceAll(" Админа ", " Братика Админа ");
        message = message.replaceAll(" Админу ", " Братику Админу ");
        message = message.replaceAll(" Админом ", " Братиком Админом ");
        message = message.replaceAll(" Админе ", " Братике Админе ");

        message = message.replaceAll(" админ ", " Братик Админ ");
        message = message.replaceAll(" админа ", " Братика Админа ");
        message = message.replaceAll(" админу ", " Братику Админу ");
        message = message.replaceAll(" админом ", " Братиком Админом ");
        message = message.replaceAll(" админе ", " Братике Админе ");

        message = message.strip();

        Random random = new Random(System.currentTimeMillis() + message.length());

        if (random.nextInt() % 11 < 3) {
            if (random.nextInt() % 101 < 3)
                message = message + ". Братец админ крутой!";
            else
                message = message + ". Ня!";
        }

        message = message.replaceAll("на", "ня");
        message = message.replaceAll("На", "Ня");

        message = message.replaceAll("ма", "мяу");
        message = message.replaceAll("Ма", "Мяу");

        message = message.replaceAll("Ш", "Ф");
        message = message.replaceAll("ш", "ф");

        //message = message.replaceAll("Р", "Л");
        //message = message.replaceAll("р", "л");

        return message;
    }

    public double getUwUDegree(OffsetDateTime date_UwU) {

        if (date_UwU != null) {

            long seconds = abs(date_UwU.until(OffsetDateTime.now(), ChronoUnit.SECONDS));

            if (seconds < firstStageSeconds + secondStageSeconds) {
                return 0;
            }

            if (seconds > firstStageSeconds + secondStageSeconds + thirdStageSeconds) {
                return 1;
            }

            return (double) (seconds - firstStageSeconds - secondStageSeconds) / thirdStageSeconds;
        }

        return 0;
    }

    public int getUwUStage(OffsetDateTime date_UwU) {

        if (date_UwU != null) {

            long seconds = abs(date_UwU.until(OffsetDateTime.now(), ChronoUnit.SECONDS));

            if (seconds < firstStageSeconds) {
                return 1;
            }

            if (seconds < firstStageSeconds + secondStageSeconds) {
                return 2;
            }

            if (seconds < firstStageSeconds + secondStageSeconds + thirdStageSeconds) {
                return 3;
            }

            return 4;
        }

        return 0;
    }

}
