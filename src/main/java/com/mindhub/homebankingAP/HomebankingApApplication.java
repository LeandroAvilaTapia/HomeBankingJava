package com.mindhub.homebankingAP;

import com.mindhub.homebankingAP.models.*;
import com.mindhub.homebankingAP.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomebankingApApplication.class, args);
    }

    @Autowired
    private PasswordEncoder passwordEnconder;

    @Bean
    public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
        return (args -> {
            Client client1 = clientRepository.save(new Client("Melba", "Morel", "melba@mindhub.com", passwordEnconder.encode("melba")));
            Client client2 = clientRepository.save(new Client("Leandro", "Avila", "leandroavila@mindhub.com",passwordEnconder.encode("leandro")));

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

            Card card1 = cardRepository.save(new Card(client1.getFirstName() + client1.getLastName(), CardType.DEBIT, CardColor.GOLD, "5555-5555-5555-5555", (short) 541, LocalDate.now(), LocalDate.now().plusYears(5)));
            Card card2 = cardRepository.save(new Card(client1.getFirstName() + client1.getLastName(), CardType.CREDIT, CardColor.TITANIUM, "5532-6355-5885-5321", (short) 879, LocalDate.now(), LocalDate.now().plusYears(5)));
            Card card3 = cardRepository.save(new Card(client2.getFirstName() + client2.getLastName(), CardType.CREDIT, CardColor.SILVER, "1324-5465-8524-9463", (short) 456, LocalDate.now(), LocalDate.now().plusYears(5)));


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

            client1.addCards(card1);
            client1.addCards(card2);
            client2.addCards(card3);

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

            cardRepository.save(card1);
            cardRepository.save(card2);
            cardRepository.save(card3);


        });
    }
}
