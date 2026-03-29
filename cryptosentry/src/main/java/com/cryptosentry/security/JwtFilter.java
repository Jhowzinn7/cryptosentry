package com.cryptosentry.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;



@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // pega o header

        String token = null;
        String username = null;


                       // extrai o token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            try {
                username = jwtUtil.extractUsername(token); // extrai o username do token
            } catch (Exception e) {
                System.out.println("token invalido");
            }
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {


                 // verifica se não expirou
            if (!jwtUtil.isTokenExpired(token)) {


                         // cria autenticação
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.emptyList()
                        );

                                   // adiciona detalhes da request
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                                   // autentica no Spring
                SecurityContextHolder.getContext().setAuthentication(authToken); /
            }
        }


        filterChain.doFilter(request, response); // continua a requisição
    }
}