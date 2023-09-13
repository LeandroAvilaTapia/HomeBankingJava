package com.mindhub.homebankingAP.controllers;

import com.mindhub.homebankingAP.dtos.AccountDTO;
import com.mindhub.homebankingAP.dtos.ClientDTO;
import com.mindhub.homebankingAP.models.Account;
import com.mindhub.homebankingAP.models.Client;
import com.mindhub.homebankingAP.services.AccountService;
import com.mindhub.homebankingAP.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(path = "")
    public List<ClientDTO> getAll() {
        return clientService.getAllClientDTO();
    }

    @GetMapping(path = "/{id}")
    public ClientDTO getClient(@PathVariable Long id) {
        return clientService.getClientDTO(id);
    }

    @PostMapping(path = "")
    public ResponseEntity<Object> register(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String password) {
        if (firstName.isEmpty()) {
            return new ResponseEntity<>("Missing firstname", HttpStatus.FORBIDDEN);
        } else if (lastName.isEmpty()) {
            return new ResponseEntity<>("Missing lastname", HttpStatus.FORBIDDEN);
        } else if (email.isEmpty()) {
            return new ResponseEntity<>("Missing email", HttpStatus.FORBIDDEN);
        } else if (password.isEmpty()) {
            return new ResponseEntity<>("Missing password", HttpStatus.FORBIDDEN);
        }

        if (clientService.getClientFindByEmail(email) != null) {

            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);

        }

        Client currentClient = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        clientService.saveClientInRepository(currentClient);
        //Asumo que al crear un nuevo cliente, este no tiene cuentas asociadas
        Account newAccount = new Account(generateUniqueAccountNumber(), LocalDate.now(), 0.0);
        accountService.saveAccountInRepository(newAccount);
        // Associate the account with the client
        currentClient.addAccounts(newAccount);
        newAccount.setAccounts(currentClient);

        clientService.saveClientInRepository(currentClient);
        accountService.saveAccountInRepository(newAccount);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @GetMapping(path = "/current")
    public ClientDTO getCurrentClient(Authentication authentication) {
        return new ClientDTO(clientService.getClientFindByEmail(authentication.getName()));
    }

    @PostMapping(path = "/current/accounts")
    public ResponseEntity<String> createAccountForCurrentClient(Authentication authentication) {

        // Get the current client
        ClientDTO currentClientDTO = getCurrentClient(authentication);

        if (currentClientDTO == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client not found");
        }

        Client currentClient = clientService.getClientFindByEmail(currentClientDTO.getEmail());

        // Chequeo si el cliente ya tiene 3 cuentas creadas
        if (currentClient.getAccounts().size() >= 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client already has 3 accounts");
        }

        // Creo una cuenta nueva
        Account newAccount = new Account(generateUniqueAccountNumber(), LocalDate.now(), 0.0);
        accountService.saveAccountInRepository(newAccount);
        //Asocia la cuenta con el cliente
        currentClient.addAccounts(newAccount);
        //Asocia el cliente con la cuenta
        newAccount.setAccounts(currentClient);

        // Guardar el cliente y la cuenta en el repositorio
        clientService.saveClientInRepository(currentClient);
        accountService.saveAccountInRepository(newAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
    }

    @GetMapping(path = "/current/accounts")
    public ResponseEntity<List<AccountDTO>> getCurrentClientAccounts(Authentication authentication) {
        String currentClientEmail = authentication.getName();
        Client currentClient = clientService.getClientFindByEmail(currentClientEmail);

        if (currentClient == null) {
            return ResponseEntity.notFound().build();
        }

        List<AccountDTO> accountDTOs = currentClient.getAccounts().stream()
                .map(AccountDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(accountDTOs);
    }

    public List<AccountDTO> getCurrentClientAccounts(String email) {
        Client currentClient = clientService.getClientFindByEmail(email);

        if (currentClient == null) {
            return Collections.emptyList();
        }

        return currentClient.getAccounts().stream()
                .map(AccountDTO::new)
                .collect(Collectors.toList());
    }

    private String generateAccountNumber() {
        // Genera un número aleatorio entre 100000 y 999999
        int randomNumber = (int) (Math.random() * (999999 - 000001 + 1)) + 000001;
        return "VIN-" + randomNumber;
    }

    private boolean isAccountNumberUnique(String accountNumber) {
        //verifica si la cuenta *accountNumber esta repetida en la tabla Account.
        //Retorna: si es un numero no esta repetido devolverá thue, si está repetido devolverá false
        return accountService.findByNumber(accountNumber) == null;
    }

    private String generateUniqueAccountNumber() {
        /*
        Proposito: Crea un numero de cuenta unico.
        retorna: un numero de cuenta unico en String
        * */
        boolean verificacionDeCuenta = true;
        String numberAccount = generateAccountNumber();
        do {
            if (isAccountNumberUnique(numberAccount)) {
                verificacionDeCuenta = false;
            }
        } while (verificacionDeCuenta);
        return numberAccount;

    }
}
