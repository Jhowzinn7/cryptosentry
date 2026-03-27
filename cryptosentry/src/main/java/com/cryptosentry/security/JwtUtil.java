package com.cryptosentry.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// @Component registra essa classe como um bean do Spring
// ou seja, ela pode ser injetada em outras classes com @Autowired ou construtor
@Component
public class JwtUtil {

    // @Value lê o valor do application.properties
    // jwt.secret é a chave secreta usada para assinar o token
    @Value("${jwt.secret}")
    private String secret;

    // Tempo de expiração do token em milissegundos (ex: 86400000 = 24h)
    @Value("${jwt.expiration}")
    private long expiration;

    // Converte a string secreta em uma chave criptográfica real
    // O HMAC-SHA é o algoritmo de assinatura usado pelo JWT
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Gera um token JWT para o usuário autenticado
    // O token carrega: quem é o usuário (subject), quando foi criado e quando expira
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())         // quem é o dono do token
                .setIssuedAt(new Date())                // data de criação
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // data de expiração
                .signWith(getSigningKey())              // assina com a chave secreta
                .compact();                            // gera a string final do token
    }

    // Extrai o username (subject) de dentro do token
    // Usado para saber qual usuário está fazendo a requisição
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())         // usa a mesma chave para verificar
                .build()
                .parseClaimsJws(token)                 // lê e valida o token
                .getBody()
                .getSubject();                         // retorna o username
    }

    // Verifica se o token é válido (assinatura correta + não expirado)
    // Retorna false se o token foi alterado ou já expirou
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;  // token válido
        } catch (JwtException e) {
            return false; // token inválido ou expirado
        }
    }

    // Verifica especificamente se o token já passou da data de expiração
    public boolean isTokenExpired(String token) {
        Date expDate = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();                      // pega a data de expiração do token
        return expDate.before(new Date());             // compara com a data atual
    }
}