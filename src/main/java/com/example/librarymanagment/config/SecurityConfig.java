package com.example.librarymanagment.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http ) throws Exception {
    http
      .csrf(csrf -> csrf.disable( )) // Désactiver CSRF pour simplifier le développement (à ne pas faire en production sans protection adéquate)
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/").permitAll() // Autoriser l'accès à la page d'accueil
        .requestMatchers("/index.html").permitAll() // Autoriser l'accès direct à index.html
        .requestMatchers("/script.js").permitAll() // Autoriser l'accès à script.js
        .requestMatchers("/style.css").permitAll() // Autoriser l'accès à style.css
        .requestMatchers("/api/**").permitAll() // Autoriser l'accès à toutes vos API REST
        .requestMatchers("/swagger-ui.html").permitAll() // Autoriser l'accès à Swagger UI
        .requestMatchers("/swagger-ui/**").permitAll() // Autoriser l'accès aux ressources Swagger UI
        .requestMatchers("/v3/api-docs/**").permitAll() // Autoriser l'accès aux définitions OpenAPI
        .requestMatchers("/h2-console/**").permitAll() // Autoriser l'accès à la console H2 (si utilisée)
        .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
      )
      .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // Nécessaire pour la console H2
    return http.build( );
  }
}
