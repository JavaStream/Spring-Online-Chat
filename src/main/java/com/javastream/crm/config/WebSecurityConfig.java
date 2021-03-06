package com.javastream.crm.config;

import com.javastream.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// При старте приложения конфигурирует Security
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()                                                    // Включаем авторизацию для обьекта
                    .antMatchers("/registration", "/static/**", "/activate/*").permitAll()          // Разрешаем доступ без пароля для главной страницы и для Регистрации
                    .anyRequest().authenticated()                                       // Для всех остальных запросов требуется авторизация
                .and()
                    .formLogin()                                       // Включаем форму для ввода логина
                    .loginPage("/login")                               // url для ввода логина
                    .permitAll()
                .and()
                    .rememberMe()
                .and()
                    .logout()                                           // Включаем логаут
                    .permitAll();
    }


    // Менеджер, который обслуживает учетные записи пользователей. Проводим аутентификацию через базу данных
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder);             // шифрует пароли, чтобы они не хранились в явном виде
    }
}