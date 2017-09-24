package org.coursera.cybersecurity.capstone.group8;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/index.html", "/register.html", "/webapi/register", "/webapi/login").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/webapi/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/message_list.html")
                .permitAll()
                .and()
            .logout()
            	.logoutUrl("/webapi/logout")
            	.logoutSuccessUrl("/")
            	.invalidateHttpSession(true)
//            	.deleteCookies(...)
                .permitAll()
            .and().csrf().disable(); // TODO see if it can be enabled later
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	// TODO set up authentication from db
        auth
            .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }
}
