package com.mindhub.homebankingAP.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import com.mindhub.homebankingAP.services.TransactionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(
            @RequestParam String fromAccountNumber, @RequestParam String toAccountNumber,
            @RequestParam double amount,
            @RequestParam String description,
            Authentication authentication) {

        // Verificar si hay campos vacíos
        if (fromAccountNumber.isEmpty() || toAccountNumber.isEmpty() || description.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Fields cannot be empty");
        }

        // Realizar la transacción utilizando el servicio
        ResponseEntity<Object> response = transactionService.performTransaction(fromAccountNumber, toAccountNumber, amount, description, authentication);
        return response;
    }
}
