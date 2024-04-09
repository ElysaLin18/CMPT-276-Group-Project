package com.github.elysalin18.cmpt276groupproject.donatedesk.models;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.oauth2Client(Customizer.withDefaults()).authorizeHttpRequests((auth) -> auth.anyRequest().permitAll()).formLogin((login) -> login.disable()).logout((logout -> logout.disable())).cors((cors) -> cors.disable()).csrf((csrf -> csrf.disable())).httpBasic((basic) -> basic.disable());
        return http.build();
    }
}
