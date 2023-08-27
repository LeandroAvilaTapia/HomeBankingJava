package com.mindhub.homebankingAP.controllers;

import com.mindhub.homebankingAP.dtos.CardDTO;
import com.mindhub.homebankingAP.dtos.ClientDTO;
import com.mindhub.homebankingAP.models.Card;
import com.mindhub.homebankingAP.models.CardColor;
import com.mindhub.homebankingAP.models.CardType;
import com.mindhub.homebankingAP.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.mindhub.homebankingAP.repositories.ClientRepository;

import com.mindhub.homebankingAP.repositories.CardRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class CardController {

    @Autowired
    private ClientController clientController;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CardRepository cardRepository;

    @RequestMapping("/cards")
    public List<CardDTO> getAllCards() {
        return cardRepository.findAll().stream().map(CardDTO::new).collect(Collectors.toList());
    }

    @RequestMapping("/clients/current/cards")
    public List<CardDTO> getCurrentClientCards(Authentication authentication) {
        return cardRepository.findByClient_email(authentication.getName()).stream().map(CardDTO::new).collect(Collectors.toList());
    }

    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> createCard(@RequestParam String cardType, @RequestParam String cardColor, Authentication authentication) {

        Client currentClient = clientRepository.findByEmail(authentication.getName());
        //revisar si los datos del paramentros son nulos o incorrectos
        if (cardType.isEmpty()) {
            return new ResponseEntity<>("Missing data: card type is empty", HttpStatus.FORBIDDEN);
        }
        if (cardColor.isEmpty()) {
            return new ResponseEntity<>("Missing data: card color is empty", HttpStatus.FORBIDDEN);
        }

        if (currentClient == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client not found");
        }
        // Verificar si el cliente ya tiene una tarjeta del mismo color y tipo
        if (hasDuplicateCard(currentClient, CardColor.valueOf(cardColor), CardType.valueOf(cardType))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client already has a card with the same color and type");
        }
        // Verificar si el cliente ya tiene 3 tarjetas del mismo tipo
        if (!canClientCreateCard(currentClient, CardType.valueOf(cardType),CardColor.valueOf(cardColor))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client already has 3 cards of this type");
        }
        String cardHolder = currentClient.getFirstName() + " " + currentClient.getLastName();
        Card newCard = new Card(cardHolder, CardType.valueOf(cardType), CardColor.valueOf(cardColor), generateUniqueCardNumber(), generateRandomCVV(), LocalDate.now().plusYears(5), LocalDate.now());
        //agrega la tarjeta al cliente
        currentClient.addCards(newCard);
        //guarda la tarjeta en la tabla Card
        cardRepository.save(newCard);

        return ResponseEntity.status(HttpStatus.CREATED).body("Card created successfully");
    }

    private boolean canClientCreateCard(Client client, CardType type,CardColor color) {
        long cardCount = client.getCards().stream()
                .filter(card -> card.getType() == type && card.getColor() == color)
                .count();

        return cardCount < 3;
    }

    private String generateCardNumber() {
        // Genera un string aleatorio entre 0001 y 9999 con el formato xxxx-xxxx-xxxx-xxxx
        int randomNumber = (int) (Math.random() * (9999 - 0001 + 1)) + 0001;
        int randomNumber2 = (int) (Math.random() * (9999 - 0001 + 1)) + 0001;
        int randomNumber3 = (int) (Math.random() * (9999 - 0001 + 1)) + 0001;
        int randomNumber4 = (int) (Math.random() * (9999 - 0001 + 1)) + 0001;
        return randomNumber + "-" + randomNumber2 + "-" + randomNumber3 + "-" + randomNumber4;
    }

    private short generateRandomCVV() {
        return (short) ((short) (Math.random() * (999 - 001 + 1)) + 001);
    }

    private boolean isAccountNumberUnique(String accountNumber) {
        //verifica si el numero de la tajeta esta repetido o no
        //Retorna: si es un numero no esta repetido devolverá thue, si está repetido devolverá false
        return cardRepository.findByNumber(accountNumber) == null;
    }

    private boolean hasDuplicateCard(Client client, CardColor cardColor, CardType cardType) {
        return client.getCards().stream()
                .anyMatch(card -> card.getColor() == cardColor && card.getType() == cardType);
    }


    private String generateUniqueCardNumber() {
        /*
        Proposito: Crea un numero de cuenta unico.
        retorna: un numero de cuenta unico en String
        * */
        boolean verificacionDeCuenta = true;
        String numberCard = generateCardNumber();
        do {
            if (isAccountNumberUnique(numberCard)) {
                verificacionDeCuenta = false;
            }
        } while (verificacionDeCuenta);
        return numberCard;

    }

}