package com.cryptosentry.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.management.relation.Role;
import java.util.Collection;
import java.util.List;

// Implements UserDetails diz ao Spring Security que essa classe
// representa um usuário autenticável no sistema
// O Spring vai usar essa classe para verificar login, senha e permissões
public class User implements UserDetails {


    private Long id;
    private String username;
    private String email;
    private String password;
    // Papel/perfil do usuário (ex: ADMIN, ANALISTA, USUARIO)
    // Controla o que cada um pode fazer no sistema
    private Role role;
    private boolean active;



    // Define as permissões do usuário com base no seu Role
    // O Spring Security usa isso para controlar acesso por perfil
    // Ex: apenas ADMIN pode ver todos os alertas de fraude
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converte o Role enum em um formato que o Spring entende
        // SimpleGrantedAuthority recebe uma string como "ROLE_ADMIN"
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    // Retorna a senha criptografada para o Spring validar no login
    @Override
    public String getPassword() {
        return password;
    }

    // Retorna o username usado como identificador no login e no JWT
    @Override
    public String getUsername() {
        return username;
    }

    // Indica se a conta nunca expira (true = não expira)
    // Pode ser alterado para false se quiser contas temporárias
    @Override
    public boolean