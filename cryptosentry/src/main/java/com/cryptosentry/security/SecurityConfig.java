package com.cryptosentry.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration indica que essa classe define configurações do Spring
// É aqui que dizemos ao Spring Security como proteger a aplicação
@Configuration
public class SecurityConfig {

    // O JwtFilter será adicionado à cadeia de filtros do Spring Security
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // Define as regras de segurança HTTP da aplicação
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF pois estamos usando JWT (stateless)
                // CSRF é necessário apenas para autenticação baseada em sessão/cookie
                .csrf(csrf -> csrf.disable())

                // Define que a aplicação não guarda sessão no servidor
                // Cada requisição precisa enviar o token JWT — não há "login salvo"
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define quais rotas são públicas e quais precisam de autenticação
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()  // login e registro são livres
                        .anyRequest().authenticated()             // todo o resto exige token válido
                )

                // Adiciona o JwtFilter ANTES do filtro padrão de autenticação do Spring
                // Assim o token é verificado antes de qualquer outra coisa
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean responsável por criptografar senhas com BCrypt
    // BCrypt é seguro pois aplica um "salt" automático — nunca armazene senha em texto puro
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager é usado no processo de login
    // Ele verifica se o usuário e senha estão corretos
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}