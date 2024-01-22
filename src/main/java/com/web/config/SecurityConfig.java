package com.web.config;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.web.jwt.JwtFilter;
import com.web.jwt.JWTUtil;
import com.web.jwt.LoginFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	};

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		http
			.cors((cors) -> cors
					.configurationSource(new CorsConfigurationSource(){

						@Override
						public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
							
							CorsConfiguration configuration = new CorsConfiguration();
							
							configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
	                        configuration.setAllowedMethods(Collections.singletonList("*"));
	                        configuration.setAllowCredentials(true);
	                        configuration.setAllowedHeaders(Collections.singletonList("*"));
	                        configuration.setMaxAge(3600L);

							configuration.setExposedHeaders(Collections.singletonList("Authorization"));

	                        return configuration;
	                        
						}
						
					}));
		
		//csrf disable
		http
			.csrf((auth) -> auth.disable()); 
		
		//From 로그인 방식 disable
		http
			.formLogin((auth) -> auth.disable());
		
		//http basic 인증방식 disable
		http
			.httpBasic((auth) -> auth.disable());
		
		http
			.authorizeHttpRequests((auth) -> auth
					.antMatchers("/login", "/", "/join").permitAll()
					.antMatchers("/admin").hasRole("ADMIN")
					.anyRequest().permitAll()
					);

        http
        .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);
        
		http
			.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
		
		//세션 stateless 설정
		http
			.sessionManagement((session) -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		return http.build();
	}
}