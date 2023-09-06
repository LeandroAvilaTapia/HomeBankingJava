package com.mindhub.homebankingAP.services;

import com.mindhub.homebankingAP.models.ClientLoan;

public interface ClientLoanService {
    void saveClientLoanInRepository(ClientLoan clientLoan);
}
