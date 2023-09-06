package com.mindhub.homebankingAP.services.implement;

import com.mindhub.homebankingAP.dtos.LoanDTO;
import com.mindhub.homebankingAP.models.Loan;
import com.mindhub.homebankingAP.repositories.LoanRepository;
import com.mindhub.homebankingAP.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class LoanServiceImplement implements LoanService {
    @Autowired
    private LoanRepository loanRepository;
    @Override
    public List<LoanDTO> getAllLoanDTO() {
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(toList());
    }

    @Override
    public Optional<Loan> loanFindById(long id) {
        return loanRepository.findById(id);
    }
}
