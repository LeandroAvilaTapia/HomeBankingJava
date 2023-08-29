package com.mindhub.homebankingAP.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface TransactionService {
    ResponseEntity<Object> performTransaction(String sourceAccountNumber, String targetAccountNumber, double amount, String description, Authentication authentication);
}