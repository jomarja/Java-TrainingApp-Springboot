package com.trainignapp.trainingapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Adjust these as needed for your front-end domains or local dev
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://frontend.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        configuration.setAllowCredentials(true);
        // configuration.setExposedHeaders(List.of("Custom-Header-1")); // If you need any exposed headers

        // Apply this CORS configuration to all endpoints:
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                //enabling cors
                .cors(Customizer.withDefaults())

                // (A) Keep the default session strategy for form-based login:
                // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // (B) Add the JWT filter before the default UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // (C) Authorize requests
                .authorizeHttpRequests(auth -> auth
                        // 1) Permit the login page and related form actions
                        .requestMatchers("/login", "/perform_login", "/css/**").permitAll()

                        // 2) Also permit token-based login or open endpoints:
                        .requestMatchers("/api/auth/login", "/api/trainers/register", "/api/trainees/register").permitAll()

                        // 3) Anything else requires login (either session from form OR valid JWT)
                        .anyRequest().authenticated()).exceptionHandling(e -> e.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        // Only apply this entry point to /api/** pattern
                        new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/api/**")))

                // (D) Keep form-based login so you can actually visit /login in a browser
                .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/perform_login").defaultSuccessUrl("/home", true).failureUrl("/login?error=true").permitAll())

                // (E) Keep logout for the form-based session
                .logout(logout -> logout.logoutUrl("/perform_logout").logoutSuccessUrl("/login?logout").invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID").permitAll());

        return http.build();
    }
}