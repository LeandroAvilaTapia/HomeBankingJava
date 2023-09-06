package com.mindhub.homebankingAP.services.implement;

import com.mindhub.homebankingAP.models.Account;
import com.mindhub.homebankingAP.models.Transaction;
import com.mindhub.homebankingAP.models.TransactionType;
import com.mindhub.homebankingAP.repositories.AccountRepository;
import com.mindhub.homebankingAP.repositories.TransactionRepository;
import com.mindhub.homebankingAP.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
public class TransactionServiceImplement implements TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    @Transactional
    public ResponseEntity<Object> performTransaction(String fromAccountNumber, String toAccountNumber, double amount, String description, Authentication authentication) {
        // Verificar si los campos están vacíos
        if (description.isEmpty() || amount <= 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Description and amount must not be empty.");
        }

        // Verificar si alguno de los números de cuenta está vacío
        if (fromAccountNumber.isEmpty() || toAccountNumber.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account numbers must not be empty.");
        }

        // Obtener cuentas de origen y destino
        Account sourceAccount = accountRepository.findByNumber(fromAccountNumber);
        Account targetAccount = accountRepository.findByNumber(toAccountNumber);

        // Verificar si las cuentas existen
        if (sourceAccount == null || targetAccount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Accounts not found");
        }

        // Verificar si el saldo de la cuenta de origen es suficiente
        if (sourceAccount.getBalance() < amount) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient balance.");
        }

        // Crear transacciones y actualizar balances
        Transaction debitTransaction = new Transaction(TransactionType.DEBIT, amount, description, LocalDateTime.now());
        sourceAccount.addTransactions(debitTransaction);
        debitTransaction.setTransactions(sourceAccount);

        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now());
        targetAccount.addTransactions(creditTransaction);
        creditTransaction.setTransactions(targetAccount);

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        // Actualizar saldos de cuentas
        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction completed successfully");
    }

    @Override
    public void saveTransactionInRepository(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}

