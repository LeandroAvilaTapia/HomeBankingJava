package com.mindhub.homebankingAP.services;

import com.mindhub.homebankingAP.dtos.ClientDTO;
import com.mindhub.homebankingAP.models.Client;

import java.util.List;

public interface ClientService {
    List<ClientDTO> getAllClientDTO();
    ClientDTO getClientDTO(Long id);
    Client getClientFindByEmail(String email);
    void saveClientInRepository(Client client);

}
