package com.mindhub.homebankingAP.services;

import com.mindhub.homebankingAP.models.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface TransactionService {
    void saveTransactionInRepository(Transaction transaction);
}