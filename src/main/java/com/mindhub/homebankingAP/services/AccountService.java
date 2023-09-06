package com.mindhub.homebankingAP.services;

import com.mindhub.homebankingAP.dtos.AccountDTO;
import com.mindhub.homebankingAP.models.Account;
import com.mindhub.homebankingAP.models.Client;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAllAccountDTO();

    Account accountForId(long id);

    AccountDTO getAccountDTOForId(long id);

    void saveAccountInRepository(Account account);

    Account findByNumber(String number);

    Account accountFindByNumberAndClient(String accountNumber, Client client);
}
