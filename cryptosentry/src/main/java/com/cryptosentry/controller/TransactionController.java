package com.cryptosentry.controller;

import com.cryptosentry.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.cryptosentry.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service; // conecta com a camada de serviço

    @PostMapping
    public Transaction create(@RequestBody Transaction transaction) {
        // recebe os dados do corpo da requisição (JSON)
        return service.save(transaction); // salva e retorna
    }

    @GetMapping
    public List<Transaction> list() {
        return service.listAll(); // busca todas no banco
    }
}