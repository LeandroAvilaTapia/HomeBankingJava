package com.mindhub.homebankingAP.services.implement;

import com.mindhub.homebankingAP.models.ClientLoan;
import com.mindhub.homebankingAP.repositories.ClientLoanRepository;
import com.mindhub.homebankingAP.services.ClientLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientLoanServiceImplement implements ClientLoanService{
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Override
    public void saveClientLoanInRepository(ClientLoan clientLoan) {
        clientLoanRepository.save(clientLoan);
    }
}
