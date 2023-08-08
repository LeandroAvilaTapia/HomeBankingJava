package com.mindhub.homebankingAP.controllers;

import com.mindhub.homebankingAP.dtos.ClientDTO;
import com.mindhub.homebankingAP.models.Account;
import com.mindhub.homebankingAP.models.Client;
import com.mindhub.homebankingAP.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping ("api")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/clients")
    public List<ClientDTO> getAll() {
        return clientRepository.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @RequestMapping("clients/{id}")
    public ClientDTO getAccount(@PathVariable Long id){
        return clientRepository.findById(id).map(ClientDTO::new).orElse(null);

    }
}
