package com.mindhub.homebankingAP.services;

import com.mindhub.homebankingAP.models.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface TransactionService {
    ResponseEntity<Object> performTransaction(String fromAccountNumber, String toAccountNumber, double amount, String description, Authentication authentication);
    void saveTransactionInRepository(Transaction transaction);
}