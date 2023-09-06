package com.mindhub.homebankingAP.services;

import com.mindhub.homebankingAP.dtos.LoanDTO;
import com.mindhub.homebankingAP.models.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    List<LoanDTO> getAllLoanDTO();
    Optional<Loan> loanFindById (long id);
}
