package com.example.laba.controllers;

import com.example.laba.objects_to_fill_templates.TmplUser;
import com.example.laba.services.OverturningTheEarthAndTramplingTheHeavensDAOService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Controller
public class IDontRememberPasswordController {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    OverturningTheEarthAndTramplingTheHeavensDAOService DAOService;

    @GetMapping("/public/i_dont_remember_password/1")
    String get_i_dont_remember_password_page1(@RequestParam (required = false) String error) {
        return "public/i_dont_remember_password1";
    }
    @GetMapping("/public/i_dont_remember_password/2")
    String get_i_dont_remember_password_page2(@RequestParam (required = false) String error) {
        return "public/i_dont_remember_password2";
    }
    @GetMapping("/public/i_dont_remember_password/3")
    String get_i_dont_remember_password_page3() {
        return "public/i_dont_remember_password3";
    }

    @PostMapping("/public/i_dont_remember_password/1")
    void i_dont_remember_password_page1(HttpServletResponse response,
                                        HttpServletRequest request,
                                        @RequestParam String email_login,
                                        @RequestParam String password) {
        String email, username;

        if (email_login.contains("@")) {
            email = email_login;

            try {
                username = DAOService.get_user_by_email(email).getLogin();
            } catch (Exception e) {
                response.setHeader("Location", "/public/i_dont_remember_password/1?error="
                        + URLEncoder.encode("Пользователя с такой почтой или логином не существует.",
                        StandardCharsets.UTF_8));
                response.setStatus(302);
                return;
            }

        } else {
            username = email_login;

            try {
                email = DAOService.get_user_by_login(email_login).getEmail();
            } catch (Exception e) {
                response.setHeader("Location", "/public/i_dont_remember_password/1?error="
                        + URLEncoder.encode("Пользователя с такой почтой или логином не существует.",
                        StandardCharsets.UTF_8));
                response.setStatus(302);
                return;
            }
        }

        String random_string = BCrypt.hashpw(
                System.currentTimeMillis() + email + password,
                BCrypt.gensalt());

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);

            helper.setText("<div style=\"text-align:center;\"><div> Здраствуйте, " + username
                    + "! После подтверждения вашим новым паролем будет: " + password +
                    ". Ваш код для восстановления пароля:</div><div style=\"font-size:1.5rem;\">"
                    + random_string + "</div></div>", true);
            helper.setFrom("ffffforum@yandex.ru");
            helper.setSubject("Код для восстановления пароля на сайте FFFFFORUM");

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            response.setHeader("Location", "/public/i_dont_remember_password/1?error="
                    + URLEncoder.encode("Не удалось отправить письмо на данную почту.",
                    StandardCharsets.UTF_8));
            response.setStatus(302);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("random_string", new StringBuffer(random_string));
        session.setAttribute("password",  new StringBuffer(BCrypt.hashpw(password, BCrypt.gensalt())));
        session.setAttribute("username",  new StringBuffer(username));
        session.setAttribute("email",  new StringBuffer(email));

        response.setHeader("Location", "/public/i_dont_remember_password/2");
        response.setStatus(302);
    }

    @PostMapping("/public/i_dont_remember_password/2")
    void i_dont_remember_password_page2(HttpServletResponse response,
                                        HttpServletRequest request,
                                        @RequestParam String secret) {

        HttpSession session = request.getSession();

        String random_string =  new String((StringBuffer) session.getAttribute("random_string"));
        String password =  new String((StringBuffer) session.getAttribute("password"));
        session.setAttribute("random_string", null);
        session.setAttribute("password", null);

        if (!Objects.equals(random_string, secret)) {
            response.setHeader("Location", "/public/i_dont_remember_password/2?error="
                    + URLEncoder.encode("Секретная строка другая.", StandardCharsets.UTF_8));
            response.setStatus(302);
            return;
        }

        String username = new String((StringBuffer) session.getAttribute("username"));
        String email =  new String((StringBuffer) session.getAttribute("email"));

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);

            helper.setText("<div style=\"text-align:center;\"><div> Здраствуйте, " + username + "! " +
                    " Поздравляем с успешным восстановлением доступа к аккаунту. </div>", true);

            helper.setFrom("ffffforum@yandex.ru");
            helper.setSubject("Вы успешно восстановили пароль на сайте FFFFFORUM");

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            response.setHeader("Location", "/public/i_dont_remember_password/2?error="
                    + URLEncoder.encode("Не удалось отправить письмо на данную почту.", StandardCharsets.UTF_8));
            response.setStatus(302);
            return;
        }

        DAOService.update_password(username, password);

        response.setHeader("Location", "/public/i_dont_remember_password/3");
        response.setStatus(302);
    }

}
