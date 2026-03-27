package com.cryptosentry.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter garante que esse filtro seja executado
// apenas UMA VEZ por requisição HTTP
// Ele intercepta toda requisição antes de chegar no Controller
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // Injeção via construtor (boa prática no Spring)
    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Lê o cabeçalho Authorization da requisição
        // Formato esperado: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        String authHeader = request.getHeader("Authorization");

        // Só processa se o cabeçalho existir e começar com "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Remove o prefixo "Bearer " para pegar só o token
            String token = authHeader.substring(7);

            // Valida o token antes de autenticar
            if (jwtUtil.validateToken(token)) {

                // Extrai o username do token para buscar o usuário no banco
                String username = jwtUtil.extractUsername(token);
                var userDetails = userDetailsService.loadUserByUsername(username);

                // Cria o objeto de autenticação com o usuário e suas permissões
                // O segundo parâmetro (null) é a senha — não precisa aqui pois o token já provou a identidade
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Adiciona detalhes da requisição (IP, session) ao objeto de autenticação
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Registra o usuário como autenticado no contexto do Spring Security
                // A partir daqui, o Spring sabe quem está fazendo a requisição
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Passa a requisição para o próximo filtro da cadeia (ou para o Controller)
        filterChain.doFilter(request, response);
    }
}