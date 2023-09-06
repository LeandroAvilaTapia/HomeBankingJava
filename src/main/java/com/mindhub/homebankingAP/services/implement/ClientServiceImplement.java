package com.mindhub.homebankingAP.services.implement;

import com.mindhub.homebankingAP.dtos.ClientDTO;
import com.mindhub.homebankingAP.models.Client;
import com.mindhub.homebankingAP.repositories.ClientRepository;
import com.mindhub.homebankingAP.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;
@Service
public class ClientServiceImplement implements ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public List<ClientDTO> getAllClientDTO() {
        return clientRepository.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @Override
    public ClientDTO getClientDTO(Long id) {
        return clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }

    @Override
    public Client getClientFindByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    @Override
    public void saveClientInRepository(Client client) {
        clientRepository.save(client);
    }

}
