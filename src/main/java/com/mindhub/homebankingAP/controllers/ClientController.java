package com.mindhub.homebankingAP.controllers;

import com.mindhub.homebankingAP.dtos.ClientDTO;
import com.mindhub.homebankingAP.models.Account;
import com.mindhub.homebankingAP.models.Client;
import com.mindhub.homebankingAP.repositories.ClientRepository;
import com.mindhub.homebankingAP.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("api/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<ClientDTO> getAll() {
        return clientRepository.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ClientDTO getClient(@PathVariable Long id) {
        return clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
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

        if (clientRepository.findByEmail(email) != null) {

            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);

        }

        Client currentClient = clientRepository.save(new Client(firstName, lastName, email, passwordEncoder.encode(password)));

        //Asumo que al crear un nuevo cliente, este no tiene cuentas asociadas
        Account newAccount = accountRepository.save(new Account(generateUniqueAccountNumber(), LocalDate.now(), 0.0));

        // Associate the account with the client
        currentClient.addAccounts(newAccount);
        newAccount.setAccounts(currentClient);

        clientRepository.save(currentClient);
        accountRepository.save(newAccount);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @RequestMapping(path = "/current", method = RequestMethod.GET)
    public ClientDTO getCurrentClient(Authentication authentication) {
        return new ClientDTO(clientRepository.findByEmail(authentication.getName()));
    }

    @RequestMapping(path = "/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<String> createAccountForCurrentClient(Authentication authentication) {

        // Get the current client
        ClientDTO currentClientDTO = getCurrentClient(authentication);

        if (currentClientDTO == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client not found");
        }

        Client currentClient = clientRepository.findByEmail(currentClientDTO.getEmail());

        // Chequeo si el cliente ya tiene 3 cuentas creadas
        if (currentClient.getAccounts().size() >= 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client already has 3 accounts");
        }

        // Creo una cuenta nueva
        Account newAccount = accountRepository.save(new Account(generateUniqueAccountNumber(), LocalDate.now(), 0.0));

        //Asocia la cuenta con el cliente
        currentClient.addAccounts(newAccount);
        //Asocia el cliente con la cuenta
        newAccount.setAccounts(currentClient);

        // Guardar el cliente y la cuenta en el repositorio
        clientRepository.save(currentClient);
        accountRepository.save(newAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
    }

    private String generateAccountNumber() {
        // Genera un número aleatorio entre 100000 y 999999
        int randomNumber = (int) (Math.random() * (999999 - 000001 + 1)) + 000001;
        return "VIN-" + randomNumber;
    }

    private boolean isAccountNumberUnique(String accountNumber) {
        //verifica si la cuenta *accountNumber esta repetida en la tabla Account.
        //Retorna: si es un numero no esta repetido devolverá thue, si está repetido devolverá false
        return accountRepository.findByNumber(accountNumber) == null;
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
