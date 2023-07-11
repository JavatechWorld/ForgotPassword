package com.spring.implementation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.spring.implementation.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SpringSecurity {
	

		    @Autowired
		    private UserDetailsServiceImpl userDetailsService;
		  
		    @Bean
		    public BCryptPasswordEncoder passwordEncoder() {
		        return new BCryptPasswordEncoder();
		    }
			
			@Bean
		    public DaoAuthenticationProvider authenticationProvider() {
		        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		        auth.setUserDetailsService(userDetailsService);
		        auth.setPasswordEncoder(passwordEncoder());
		        return auth;
		    }
			
			public AuthenticationManager authenticationManager(AuthenticationConfiguration
					authenticationConfiguration) throws Exception {
		        return authenticationConfiguration.getAuthenticationManager();
		    }
				    
		    @Bean
		    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		        http.authorizeRequests()
		            .antMatchers("/register","/forgotPassword","/resetPassword/**").permitAll()
		            .anyRequest().authenticated()
		            .and()
		            .formLogin()
		            .loginPage("/login").defaultSuccessUrl("/userDashboard")
		            .permitAll()
		            .and()
		            .logout()
		            .permitAll();

		        http.csrf().disable();
		        http.headers().defaultsDisabled().cacheControl();
		        
		        return http.build();
		    }

}
