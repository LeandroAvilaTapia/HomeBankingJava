package com.mindhub.homebankingAP.repositories;

import com.mindhub.homebankingAP.models.Account;
import com.mindhub.homebankingAP.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByNumber(String number);
    Optional<Account> findById (long id);
    Account findByNumberAndClient(String accountNumber, Client client);
    
}
