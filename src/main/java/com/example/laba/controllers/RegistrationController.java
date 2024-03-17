package com.example.laba.controllers;

import com.example.laba.objects_to_fill_templates.TmplUser;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.mail.SimpleMailMessage;
import jakarta.mail.internet.MimeMessage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Controller
public class RegistrationController {

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @Autowired
    JavaMailSender mailSender;

    @GetMapping("/public/register/1")
    String get_registration1(@RequestParam(required = false) String error) { return "public/registration1";}

    @GetMapping("/public/register/2")
    String get_register_finish(@RequestParam(required = false) String error) { return "public/registration2";}

    @GetMapping("/public/register/3")
    String get_register_success() {
        return "public/registration3";
    }

    @PostMapping("/public/register/1")
    void register(@RequestParam String username,
                  @RequestParam String password,
                  @RequestParam String pref_email,
                  @RequestParam String suff_email,
                  @RequestParam String sex,
                  HttpServletResponse response,
                  HttpServletRequest request) throws MessagingException {

        String email = pref_email + '@' + suff_email;

        try {

            TmplUser user = DAOService.get_user_by_login(username);

            if (user != null) {
                response.setHeader("Location", "/public/register/1?error="
                        + URLEncoder.encode("Такой логин уже занят.", StandardCharsets.UTF_8));
                response.setStatus(302);
                return;
            }
        } catch (Exception e) {
            //Просто игнорируем... Исключения как раз и должны быть если логин не занят.
        }

        try{
            TmplUser user = DAOService.get_user_by_email(email);

            if (user != null) {
                response.setHeader("Location", "/public/register/1?error="
                        + URLEncoder.encode("Эта электронная почта уже занята.", StandardCharsets.UTF_8));
                response.setStatus(302);
                return;
            }

        } catch (Exception e) {
            //Просто игнорируем... Исключения как раз и должны быть если почта не занята.
        }

        String random_string = BCrypt.hashpw(
                System.currentTimeMillis() + password + email + username,
                BCrypt.gensalt());

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);

            helper.setText("<div style=\"text-align:center;\"><div> Здраствуйте, " + username + "! " +
                    "Ваш код подтверждения электронной почты:</div><div style=\"font-size:1.5rem;\">"
                    + random_string + "</div></div>", true);

            helper.setFrom("ffffforum@yandex.ru");
            helper.setSubject("Код для подтверждения почты на сайте FFFFFORUM");

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            response.setHeader("Location", "/public/register/1?error="
                    + URLEncoder.encode("Не удалось отправить письмо на данную почту.", StandardCharsets.UTF_8));
            response.setStatus(302);
            return;
        }

        HttpSession session = request.getSession();

        session.setAttribute("random_string", new StringBuffer(random_string));
        session.setAttribute("username", new StringBuffer(username));
        session.setAttribute("password", new StringBuffer(BCrypt.hashpw(password, BCrypt.gensalt())));
        session.setAttribute("email", new StringBuffer(email));
        session.setAttribute("sex", new StringBuffer(sex));

        response.setHeader("Location", "/public/register/2");
        response.setStatus(302);
    }
    @PostMapping("/public/register/2")
    void register_finish(HttpServletRequest request,
                  HttpServletResponse response,
                  @RequestParam String secret) {

        HttpSession session = request.getSession();

        String random_string =  new String((StringBuffer) session.getAttribute("random_string"));

        if (!Objects.equals(random_string, secret)) {
            response.setHeader("Location", "/public/register/2?error="
                    + URLEncoder.encode("Секретная строка другая.", StandardCharsets.UTF_8));
            response.setStatus(302);
            return;
        }

        String username = new String((StringBuffer) session.getAttribute("username"));
        String email = new String((StringBuffer)  session.getAttribute("email"));

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);

            helper.setText("<div style=\"text-align:center;\"><div> Здраствуйте, " + username + "! " +
                    " Поздравляем с успешной регистрацией на нашем сайте. </div>", true);

            helper.setFrom("ffffforum@yandex.ru");
            helper.setSubject("Вы успешно зарегестрировались на сайте FFFFFORUM");

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            response.setHeader("Location", "/public/register/2?error="
                    + URLEncoder.encode("Не удалось отправить письмо на данную почту.", StandardCharsets.UTF_8));
            response.setStatus(302);
            return;
        }

        String password = new String((StringBuffer) session.getAttribute("password"));
        String sex = new String((StringBuffer) session.getAttribute("sex"));
        session.setAttribute("random_string", null);
        session.setAttribute("password", null);

        DAOService.add_user(username, password, email, (sex.equals("male"))? "мужской" : "женский", false);

        response.setHeader("Location", "/public/register/3");
        response.setStatus(302);
    }
}
