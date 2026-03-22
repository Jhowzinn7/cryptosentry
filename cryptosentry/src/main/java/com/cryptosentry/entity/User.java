package com.cryptosentry.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "tb_user")
public class User  {

    @Id
    @GeneratedValue
    private UUID id;


    @Column(unique = true)
    private String username;

    private String  password;
}
