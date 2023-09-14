package com.mindhub.homebankingAP;

import com.mindhub.homebankingAP.models.*;
import com.mindhub.homebankingAP.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private TransactionRepository transactionRepository;


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

    @Test
    public void testFindByNumber() {
        Card foundCard = cardRepository.findByNumber("5532-6355-5885-5321");

        // Verificar que se haya encontrado la tarjeta
        assertNotNull(foundCard);
        assertEquals("5532-6355-5885-5321", foundCard.getNumber());
    }

    @Test
    public void testCardListSizeNotGreaterThan3() {
        // Obtener todas las tarjetas de la base de datos
        List<Card> cards = cardRepository.findByClient_email("melba@mindhub.com");

        // Verificar que la lista de tarjetas no tenga más de 3 elementos
        assertThat(cards, hasSize(lessThanOrEqualTo(3)));
    }

    @Test
    public void testFindAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        assertNotNull(transactions);
        assertThat(transactions, hasSize(greaterThanOrEqualTo(2)));
    }

    @Test
    public void testFindTransactionsByDate() {

        LocalDateTime fechaABuscar = LocalDateTime.of(2023, 9, 6, 22, 27, 45, 537746);
        List<Transaction> transactionsWithDate = transactionRepository.findByDate(fechaABuscar);

        assertThat(transactionsWithDate, notNullValue());
    }

}
