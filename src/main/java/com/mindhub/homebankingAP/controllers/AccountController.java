package com.mindhub.homebankingAP.controllers;


import com.mindhub.homebankingAP.dtos.AccountDTO;
import com.mindhub.homebankingAP.models.Account;
import com.mindhub.homebankingAP.models.Client;
import com.mindhub.homebankingAP.repositories.AccountRepository;
import com.mindhub.homebankingAP.repositories.ClientRepository;
import com.mindhub.homebankingAP.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("api")
public class AccountController {

    @Autowired
    public AccountRepository accountRepository;
    @Autowired
    public AccountService accountService;
    @Autowired
    public ClientRepository clientRepository;

    @RequestMapping("/accounts")
    public List<AccountDTO> getAll() {
        return accountService.getAllAccountDTO();//accountRepository.findAll().stream().map(AccountDTO::new).collect(toList());
    }

    @RequestMapping("/accounts/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable Long id, Authentication authentication) {
        Client client = clientRepository.findByEmail(authentication.getName());
        AccountDTO accountDTO = accountService.getAccountDTOForId(id);
        if (client == null) {
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }
        if (accountDTO == null) {
            return new ResponseEntity<>("Account not found", HttpStatus.FORBIDDEN);
        }
        if ((accountDTO.getClientId()) == client.getId()) {
            return new ResponseEntity<>(accountDTO, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("Transactions not found", HttpStatus.FORBIDDEN);
    }
}
