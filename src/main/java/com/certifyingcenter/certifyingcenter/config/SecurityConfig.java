package com.certifyingcenter.certifyingcenter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/registry").permitAll()
                .antMatchers("/registry_end").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/styles/**").permitAll()
                .antMatchers("/scripts/**").permitAll()
                .antMatchers("/download/root_cert").permitAll()
                //TODO
                .antMatchers("/admin/users_change").hasRole("SUPER_ADMIN")
               // .antMatchers("/scripts/super_admin_app.js").hasRole("SUPER_ADMIN")
               // .antMatchers("/scripts/super_admin_app.js").hasRole("SUPER_ADMIN")
                .antMatchers("/img/**").permitAll()

                .antMatchers("/admin/list_certificate").hasRole("ADMIN")
                .antMatchers("/admin/list_certificate").hasRole("SUPER_ADMIN")
                .antMatchers("/admin/certificate/**").hasRole("ADMIN")
                .antMatchers("/admin/certificate/**").hasRole("SUPER_ADMIN")
                .antMatchers("/admin/list_users").hasRole("SUPER_ADMIN")
                .antMatchers("/admin/user_change").hasRole("SUPER_ADMIN")
                .antMatchers("/default").authenticated()
                .antMatchers("/create_certificate").authenticated()
                .antMatchers("/create_certificate_send").authenticated()
                .antMatchers("/home/**").authenticated()
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/login")
                //TODO fix
                .defaultSuccessUrl("/default", true)
                .usernameParameter("username")
                .passwordParameter("password")

                .and()
                .logout().clearAuthentication(true)
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll();
    }




}
