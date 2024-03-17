package com.example.laba.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationFailureHandler handler,
                                                   AuthenticationEntryPoint entryPoint)
            throws Exception {

        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/public/**", "/", "/public_static/**").permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/public/login")
                        .defaultSuccessUrl("/public/rooms", true)
                        .failureHandler(handler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/"))
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(entryPoint));

        return http.build();
    }
}
