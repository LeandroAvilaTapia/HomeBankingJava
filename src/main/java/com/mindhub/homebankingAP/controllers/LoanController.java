package com.mindhub.homebankingAP.controllers;

import com.mindhub.homebankingAP.dtos.LoanApplicationDTO;
import com.mindhub.homebankingAP.dtos.LoanDTO;
import com.mindhub.homebankingAP.models.*;
import com.mindhub.homebankingAP.services.*;
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


@RestController
@RequestMapping("api/loans")
public class LoanController {
    @Autowired
    private LoanService loanService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private TransactionService transactionService;


    @RequestMapping(method = RequestMethod.GET)
    public List<LoanDTO> getAll() {
        return loanService.getAllLoanDTO();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> applyForLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication) {
        // Aquí puedes procesar la solicitud de préstamo utilizando los datos del LoanApplicationDTO
        // Por ejemplo, puedes acceder a loanApplicationDTO.getLoanId(), loanApplicationDTO.getAmount(), etc.
        Loan loan = loanService.loanFindById(loanApplicationDTO.getLoanId()).orElse(null);
        Client clientCurrent = clientService.getClientFindByEmail(authentication.getName());
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
        Account destinoAccount = accountService.accountFindByNumberAndClient(numerodecuenta, clientCurrent);
        if (destinoAccount == null) {
            return new ResponseEntity<>("the destination account does not correspond to the client", HttpStatus.FORBIDDEN);
        }
        //Verifica si la cuenta de destino existe
        if (accountService.findByNumber(loanApplicationDTO.getToAccountNumber()) == null) {
            return new ResponseEntity<>("Destination account not found", HttpStatus.FORBIDDEN);
        }
        //crear una solicitud de préstamo con el monto solicitado sumando el 20% del mismo
        ClientLoan cloan1 = new ClientLoan(clientCurrent, loan, loanApplicationDTO.getAmount() * 1.20, loanApplicationDTO.getPayments());
        clientLoanService.saveClientLoanInRepository(cloan1);
        clientCurrent.addLoan(cloan1);
        loan.addClientLoan(cloan1);

        //Crear una nueva transaccion
        Transaction transaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.getName() + " loan approved", LocalDateTime.now());
        transactionService.saveTransactionInRepository(transaction);

        destinoAccount.setBalance(destinoAccount.getBalance() + loanApplicationDTO.getAmount());
        destinoAccount.addTransactions(transaction);
        transaction.setTransactions(destinoAccount);


        return ResponseEntity.ok("Loan application received successfully");
    }


}
