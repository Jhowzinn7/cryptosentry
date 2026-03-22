package com.cryptosentry.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Double amount;

    private String merchant;

    private LocalDateTime timestamp = LocalDateTime.now();

    private String status;



}
