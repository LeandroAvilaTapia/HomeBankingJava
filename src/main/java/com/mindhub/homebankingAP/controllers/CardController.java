package com.mindhub.homebankingAP.controllers;

import com.mindhub.homebankingAP.dtos.ClientDTO;
import com.mindhub.homebankingAP.models.Card;
import com.mindhub.homebankingAP.models.CardColor;
import com.mindhub.homebankingAP.models.CardType;
import com.mindhub.homebankingAP.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindhub.homebankingAP.repositories.ClientRepository;

import com.mindhub.homebankingAP.repositories.CardRepository;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/clients/current")
public class CardController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ClientController clientController;

    @PostMapping("cards")
    public ResponseEntity<String> createCard(Authentication authentication,
                                             @RequestParam CardColor color, @RequestParam CardType type) {
        ClientDTO currentClientDTO = clientController.getCurrentClient(authentication);

        if (currentClientDTO == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client not found");
        }

        Client currentClient = clientRepository.findByEmail(currentClientDTO.getEmail());

        // Check if the client already has 3 cards of the same type
        long cardCount = currentClient.getCards().stream()
                .filter(card -> card.getType() == type)
                .count();

        if (cardCount >= 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client already has 3 cards of this type and color");
        }

        String cardNumber = generateUniqueCardNumber();
        String cardHolder = currentClient.getFirstName() + " " + currentClient.getLastName();
        short cvv = generateRandomCVV();
        LocalDate startDate = LocalDate.now();
        LocalDate expirationDate = startDate.plusYears(5);
        //Card(String cardHolder, CardType type, CardColor color, String number, short cvv, LocalDate thruDate, LocalDate fromDate)
        //Card newCard = new Card(cardNumber, cardHolder, cvv, startDate, expirationDate, color, type, currentClient);
        Card newCard = new Card(cardHolder, type, color, cardNumber,cvv,startDate,expirationDate);
        cardRepository.save(newCard);
        currentClient.addCards(newCard);

        return ResponseEntity.status(HttpStatus.CREATED).body("Card created successfully");
    }


    private String generateCardNumber() {
        // Genera un string aleatorio entre 0001 y 9999 con el formato xxxx-xxxx-xxxx-xxxx
        int randomNumber = (int) (Math.random() * (9999 - 0001 + 1)) + 0001;
        int randomNumber2 = (int) (Math.random() * (9999 - 0001 + 1)) + 0001;
        int randomNumber3= (int) (Math.random() * (9999 - 0001 + 1)) + 0001;
        int randomNumber4 = (int) (Math.random() * (9999 - 0001 + 1)) + 0001;
        return randomNumber+"-"+randomNumber2+"-"+randomNumber3+"-"+randomNumber4;
    }
    private short generateRandomCVV(){
        return (short) ((short) (Math.random() * (999 - 001 + 1)) + 001);
    }

    private boolean isAccountNumberUnique(String accountNumber) {
        //verifica si la cuenta *accountNumber esta repetida en la tabla Account.
        //Retorna: si es un numero no esta repetido devolverá thue, si está repetido devolverá false
        return cardRepository.findByNumber(accountNumber) == null;
    }

    private String generateUniqueCardNumber() {
        /*
        Proposito: Crea un numero de cuenta unico.
        retorna: un numero de cuenta unico en String
        * */
        boolean verificacionDeCuenta = true;
        String numberCard= generateCardNumber();
        do {
            if (isAccountNumberUnique(numberCard)) {
                verificacionDeCuenta = false;
            }
        } while (verificacionDeCuenta);
        return numberCard;

    }

}
