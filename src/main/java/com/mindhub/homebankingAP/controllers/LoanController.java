package com.mindhub.homebankingAP.controllers;

import com.mindhub.homebankingAP.dtos.ClientDTO;
import com.mindhub.homebankingAP.dtos.ClientLoanDTO;
import com.mindhub.homebankingAP.dtos.LoanApplicationDTO;
import com.mindhub.homebankingAP.dtos.LoanDTO;
import com.mindhub.homebankingAP.models.Client;
import com.mindhub.homebankingAP.models.Loan;
import com.mindhub.homebankingAP.repositories.AccountRepository;
import com.mindhub.homebankingAP.repositories.ClientRepository;
import com.mindhub.homebankingAP.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("api/loans")
public class LoanController {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<LoanDTO> getAll() {
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(toList());
    }
    @RequestMapping(value = "", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> applyForLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication) {
        // Aquí puedes procesar la solicitud de préstamo utilizando los datos del LoanApplicationDTO
        // Por ejemplo, puedes acceder a loanApplicationDTO.getLoanId(), loanApplicationDTO.getAmount(), etc.
        Loan loan = loanRepository.findById(loanApplicationDTO.getLoanId()).orElse(null);
        Client clientCurrent = clientRepository.findByEmail(authentication.getName());
        //Verifica si el prestamo existe
        if (loan == null) {
            return new ResponseEntity<>("Loan not found", HttpStatus.FORBIDDEN);
        }
        //Verifica si el campo de prestamo es nulo
        if (loanApplicationDTO.getLoanId() == null) {
            return new ResponseEntity<>("Loan not found", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getAmount() <= 0) {
            return new ResponseEntity<>("the amount cannot be negative", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getPayments() <= 0) {
            return new ResponseEntity<>("Payments not found", HttpStatus.FORBIDDEN);
        }
        if (loan.getMaxAmount() < loanApplicationDTO.getAmount()) {
            return new ResponseEntity<>("the requested amount exceeds the maximum loan amount", HttpStatus.FORBIDDEN);
        }
        if (accountRepository.findByNumberAndClient(loanApplicationDTO.getAccountToNumber(), clientCurrent) == null) {
            return new ResponseEntity<>("the requested amount exceeds the maximum loan amount", HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok("Loan application received successfully");
    }



}
