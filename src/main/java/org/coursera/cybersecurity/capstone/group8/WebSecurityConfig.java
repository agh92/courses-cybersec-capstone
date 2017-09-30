package org.coursera.cybersecurity.capstone.group8;

import org.coursera.cybersecurity.capstone.group8.internal.UserManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserManagement userManagement;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/index.html", "/register.html", "/webapi/register", 
                		"/webapi/login", "/dbdump", "/password_reset.html", "/password_reset2.html", 
                		"/webapi/passwordReset", "/webapi/passwordReset2", 
                		"/css/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/webapi/loginPage")
                .loginProcessingUrl("/webapi/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/webapi/messageList")
                .permitAll()
                .and()
            .logout()
            	.logoutUrl("/webapi/logout")
            	.logoutSuccessUrl("/")
            	.invalidateHttpSession(true)
            	.deleteCookies("JSESSIONID")
                .permitAll()
            .and().csrf().disable(); // TODO see if it can be enabled later
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	auth.userDetailsService(userManagement).passwordEncoder(passwordEncoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
