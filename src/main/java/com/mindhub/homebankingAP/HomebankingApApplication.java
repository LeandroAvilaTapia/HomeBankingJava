package com.mindhub.homebankingAP;

import com.mindhub.homebankingAP.models.Account;
import com.mindhub.homebankingAP.models.Client;
import com.mindhub.homebankingAP.models.Transaction;
import com.mindhub.homebankingAP.models.TransactionType;
import com.mindhub.homebankingAP.repositories.AccountRepository;
import com.mindhub.homebankingAP.repositories.ClientRepository;
import com.mindhub.homebankingAP.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class HomebankingApApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository){
		return (args -> {
			Client client1 = clientRepository.save(new Client("Melba","Morel","melba@mindhub.com"));
			Client client2 = clientRepository.save(new Client("Leandro","Avila","leandroavila@mindhub.com"));

			Account account1 = accountRepository.save(new Account("VIN001", LocalDate.now(),5000.00));
			Account account2 = accountRepository.save(new Account("VIN002", LocalDate.now().plusDays(1),7500.00));
			Account account3 = accountRepository.save(new Account("VIN003", LocalDate.now().plusDays(2),17500.00));

			Transaction transaction1 = transactionRepository.save(new Transaction(TransactionType.CREDIT,2400.00,"De prueba", LocalDateTime.now()));

			client1.addAccounts(account1);
			client1.addAccounts(account2);
			client2.addAccounts(account3);

			account1.setAccounts(client1);
			account2.setAccounts(client1);
			account3.setAccounts(client2);

			account1.addTransactions(transaction1);

			clientRepository.save(client1);
			clientRepository.save(client2);
			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);



		});
	}
}
