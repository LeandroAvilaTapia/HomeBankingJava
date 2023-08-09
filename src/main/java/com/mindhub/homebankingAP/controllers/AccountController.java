package com.mindhub.homebankingAP.controllers;


import com.mindhub.homebankingAP.dtos.AccountDTO;
import com.mindhub.homebankingAP.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping("/accounts")
    public List<AccountDTO> getAll() {
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(toList());
    }

    @RequestMapping("accounts/{id}")
    public AccountDTO getTransaction(@PathVariable Long id) {
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);

    }

}
