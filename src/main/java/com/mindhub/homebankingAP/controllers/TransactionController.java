package com.mindhub.homebankingAP.controllers;

import com.mindhub.homebankingAP.models.Account;
import com.mindhub.homebankingAP.models.Client;
import com.mindhub.homebankingAP.models.Transaction;
import com.mindhub.homebankingAP.models.TransactionType;
import com.mindhub.homebankingAP.services.AccountService;
import com.mindhub.homebankingAP.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import com.mindhub.homebankingAP.services.TransactionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @PostMapping("/transactions")
    @Transactional
    public ResponseEntity<Object> createTransaction(
            @RequestParam String fromAccountNumber, @RequestParam String toAccountNumber,
            @RequestParam double amount,
            @RequestParam String description,
            Authentication authentication) {

        // Verificar si hay campos vacíos
        if (fromAccountNumber.isEmpty() || toAccountNumber.isEmpty() || description.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Fields cannot be empty");
        }

        // Verificar si los campos están vacíos
        if (description.isEmpty() || amount <= 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Description and amount must not be empty.");
        }

        // Verificar si alguno de los números de cuenta está vacío
        if (fromAccountNumber.isEmpty() || toAccountNumber.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account numbers must not be empty.");
        }


        // Obtener cuentas de origen, destino y el cliente autenticado
        Account sourceAccount = accountService.findByNumber(fromAccountNumber);
        Account targetAccount = accountService.findByNumber(toAccountNumber);
        Client clientcurrent = clientService.getClientFindByEmail(authentication.getName());

        // Verificar silacuentadeorigenno pertenece al cliente autenticado
        if (accountService.accountFindByNumberAndClient(sourceAccount.getNumber(), clientcurrent) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The source account does not belong to the authenticated client.");
        }
        // Verificar si las cuentas existen
        if (sourceAccount == null || targetAccount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Accounts not found");
        }

        // Verificar si el saldo de la cuenta de origen es suficiente
        if (sourceAccount.getBalance() < amount) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient balance.");
        }

        // Crear transacciones y actualizar balances
        Transaction debitTransaction = new Transaction(TransactionType.DEBIT, -1 * amount, description, LocalDateTime.now());
        sourceAccount.addTransactions(debitTransaction);
        debitTransaction.setTransactions(sourceAccount);

        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now());
        targetAccount.addTransactions(creditTransaction);
        creditTransaction.setTransactions(targetAccount);

        transactionService.saveTransactionInRepository(debitTransaction);
        transactionService.saveTransactionInRepository(creditTransaction);

        // Actualizar saldos de cuentas
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        accountService.saveAccountInRepository(sourceAccount);
        accountService.saveAccountInRepository(targetAccount);

        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction completed successfully");


    }
}
