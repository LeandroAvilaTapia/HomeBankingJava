package com.mindhub.homebankingAP.controllers;

import com.mindhub.homebankingAP.dtos.ClientDTO;
import com.mindhub.homebankingAP.dtos.ClientLoanDTO;
import com.mindhub.homebankingAP.dtos.LoanApplicationDTO;
import com.mindhub.homebankingAP.dtos.LoanDTO;
import com.mindhub.homebankingAP.models.*;
import com.mindhub.homebankingAP.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("api/loans")
public class LoanController {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;


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
        String numerodecuenta = loanApplicationDTO.getToAccountNumber();
        //Verifica si el prestamo existe
        if (loan == null) {
            return new ResponseEntity<>("Loan not found", HttpStatus.FORBIDDEN);
        }
        //Verifica si el campo de prestamo es nulo
        if (loanApplicationDTO.getLoanId() == null) {
            return new ResponseEntity<>("Loan not found", HttpStatus.FORBIDDEN);
        }
        //Verifica si el monto es negativo
        if (loanApplicationDTO.getAmount() <= 0) {
            return new ResponseEntity<>("the amount cannot be negative", HttpStatus.FORBIDDEN);
        }
        //Verifica si las cuotas es negativa
        if (loanApplicationDTO.getPayments() <= 0) {
            return new ResponseEntity<>("Payments not found", HttpStatus.FORBIDDEN);
        }
        //Verifica si el monto del prestamo pedido excede al monto maximo del prestamo
        if (loan.getMaxAmount() < loanApplicationDTO.getAmount()) {
            return new ResponseEntity<>("the requested amount exceeds the maximum loan amount", HttpStatus.FORBIDDEN);
        }
        //Verifica si la cuenta de destino pertenece al cliente autenticado
        Account destinoAccount = accountRepository.findByNumberAndClient(numerodecuenta, clientCurrent);
        if (destinoAccount == null) {
            return new ResponseEntity<>("the destination account does not correspond to the client", HttpStatus.FORBIDDEN);
        }
        //Verifica si la cuenta de destino existe
        if (accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber()) == null) {
            return new ResponseEntity<>("Destination account not found", HttpStatus.FORBIDDEN);
        }
        //crear una solicitud de préstamo con el monto solicitado sumando el 20% del mismo
        ClientLoan cloan1 = clientLoanRepository.save(new ClientLoan(clientCurrent, loan, loanApplicationDTO.getAmount() * 1.20, loanApplicationDTO.getPayments()));
        clientCurrent.addLoan(cloan1);
        loan.addClientLoan(cloan1);

        //Crear una nueva transaccion
        Transaction transaction = transactionRepository.save(new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.getName() + " loan approved", LocalDateTime.now()));
        destinoAccount.setBalance(destinoAccount.getBalance() + loanApplicationDTO.getAmount());
        destinoAccount.addTransactions(transaction);
        transaction.setTransactions(destinoAccount);


        return ResponseEntity.ok("Loan application received successfully");
    }


}