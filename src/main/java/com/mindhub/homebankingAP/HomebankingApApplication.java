package com.mindhub.homebankingAP;

import com.mindhub.homebankingAP.models.*;
import com.mindhub.homebankingAP.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomebankingApApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository) {
        return (args -> {
            Client client1 = clientRepository.save(new Client("Melba", "Morel", "melba@mindhub.com"));
            Client client2 = clientRepository.save(new Client("Leandro", "Avila", "leandroavila@mindhub.com"));

            Account account1 = accountRepository.save(new Account("VIN001", LocalDate.now(), 5000.00));
            Account account2 = accountRepository.save(new Account("VIN002", LocalDate.now().plusDays(1), 7500.00));
            Account account3 = accountRepository.save(new Account("VIN003", LocalDate.now().plusDays(2), 17500.00));

            Transaction transaction1 = transactionRepository.save(new Transaction(TransactionType.CREDIT, 2400.00, "De prueba", LocalDateTime.now()));
            Transaction transaction2 = transactionRepository.save(new Transaction(TransactionType.DEBIT, -5400.00, "Factura de luz", LocalDateTime.now()));

            Loan loan1 = loanRepository.save(new Loan("Hipotecario", 500000.0, List.of(12, 24, 36, 48, 60)));
            Loan loan2 = loanRepository.save(new Loan("Personal", 100000.0, List.of(6, 12, 24)));
            Loan loan3 = loanRepository.save(new Loan("Automotriz", 300000.0, List.of(6, 12, 24, 36)));

            ClientLoan cloan1 = clientLoanRepository.save(new ClientLoan(client1, loan1, 400000.0, 60));
            ClientLoan cloan2 = clientLoanRepository.save(new ClientLoan(client1, loan2, 50000.0, 12));
            ClientLoan cloan3 = clientLoanRepository.save(new ClientLoan(client2, loan2, 100000.0, 24));
            ClientLoan cloan4 = clientLoanRepository.save(new ClientLoan(client2, loan3, 200000.0, 36));


            client1.addAccounts(account1);
            client1.addAccounts(account2);
            client2.addAccounts(account3);

            account1.setAccounts(client1);
            account2.setAccounts(client1);
            account3.setAccounts(client2);

            account1.addTransactions(transaction1);
            transaction1.setTransactions(account1);

            account2.addTransactions(transaction2);
            transaction2.setTransactions(account2);

            clientRepository.save(client1);
            clientRepository.save(client2);
            accountRepository.save(account1);
            accountRepository.save(account2);
            accountRepository.save(account3);
            transactionRepository.save(transaction1);
            transactionRepository.save(transaction2);

            clientLoanRepository.save(cloan1);
            clientLoanRepository.save(cloan2);
            clientLoanRepository.save(cloan3);
            clientLoanRepository.save(cloan4);


        });
    }
}
