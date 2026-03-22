package com.cryptosentry.service;

import com.cryptosentry.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cryptosentry.repository.TransactionRepository;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    public Transaction save(Transaction transaction) {


        if (transaction.getAmount() > 1000) {
            transaction.setStatus("SUSPEITO");
        } else {
            transaction.setStatus("APROVADO");
        }

        return repository.save(transaction);
    }

    public List<Transaction> listAll() {
        return repository.findAll();
    }
}