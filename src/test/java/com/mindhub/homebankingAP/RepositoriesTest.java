package com.mindhub.homebankingAP;

import com.mindhub.homebankingAP.models.Account;
import com.mindhub.homebankingAP.models.Client;
import com.mindhub.homebankingAP.models.Loan;
import com.mindhub.homebankingAP.repositories.AccountRepository;
import com.mindhub.homebankingAP.repositories.ClientRepository;
import com.mindhub.homebankingAP.repositories.LoanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class RepositoriesTest {


    @Autowired
    LoanRepository loanRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AccountRepository accountRepository;


    @Test
    public void existLoans() {
        List<Loan> loans = loanRepository.findAll();
        assertThat(loans, is(not(empty())));

    }


    @Test
    public void existPersonalLoan() {
        List<Loan> loans = loanRepository.findAll();
        assertThat(loans, hasItem(hasProperty("name", is("Personal"))));
    }

    @Test
    public void existClients() {
        List<Client> clients = clientRepository.findAll();
        assertThat(clients, is(not(empty())));
    }

    @Test
    public void existAdminClient() {
        List<Client> clients = clientRepository.findAll();
        assertThat(clients, hasItem(hasProperty("email", is("admin@mindhub.com"))));
    }

    @Test
    public void existAccounts() {
        Account account = accountRepository.findByNumber("VIN-000001");
        assertThat(account, notNullValue());
    }

    @Test
    public void findById_NonExistingAccount() {
        // Intenta buscar un ID que no existe en el repositorio
        Optional<Account> foundAccount = accountRepository.findById(99999L);

        // Verifica que el Optional esté vacío (sin valor presente)
        assertFalse(foundAccount.isPresent());
    }
}
