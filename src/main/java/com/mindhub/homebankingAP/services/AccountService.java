package com.mindhub.homebankingAP.services;

import com.mindhub.homebankingAP.dtos.AccountDTO;
import com.mindhub.homebankingAP.models.Account;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAllAccountDTO();
    Account accountForId(long id);
    AccountDTO getAccountDTOForId(long id);
}
