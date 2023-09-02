package com.mindhub.homebankingAP.repositories;

import com.mindhub.homebankingAP.models.Account;
import com.mindhub.homebankingAP.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByNumber(String number);
    Account findById (long id);
    Account findByNumberAndClient(String accountNumber, Client client);
    
}
