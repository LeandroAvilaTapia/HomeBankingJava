package com.mindhub.homebankingAP.services.implement;

import com.mindhub.homebankingAP.models.Transaction;
import com.mindhub.homebankingAP.repositories.TransactionRepository;
import com.mindhub.homebankingAP.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class TransactionServiceImplement implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void saveTransactionInRepository(Transaction transaction) {
        transactionRepository.save(transaction);
    }


}

