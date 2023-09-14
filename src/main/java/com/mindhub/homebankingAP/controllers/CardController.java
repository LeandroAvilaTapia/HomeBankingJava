package com.mindhub.homebankingAP.controllers;

import com.mindhub.homebankingAP.dtos.CardDTO;
import com.mindhub.homebankingAP.models.Card;
import com.mindhub.homebankingAP.models.CardColor;
import com.mindhub.homebankingAP.models.CardType;
import com.mindhub.homebankingAP.models.Client;
import com.mindhub.homebankingAP.services.CardService;
import com.mindhub.homebankingAP.services.ClientService;
import com.mindhub.homebankingAP.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("api")
public class CardController {
    @Autowired
    private CardService cardService;
    @Autowired
    private ClientService clientService;

    @GetMapping("/cards")
    public List<CardDTO> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/clients/current/cards")
    public List<CardDTO> getCurrentClientCards(Authentication authentication) {
        return cardService.getCurrentClientCards(authentication.getName());
        //cardRepository.findByClient_email(authentication.getName()).stream().map(CardDTO::new).collect(Collectors.toList());
    }

    @PostMapping(path = "/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam String cardType, @RequestParam String cardColor, Authentication authentication) {

        Client currentClient = clientService.getClientFindByEmail(authentication.getName());
        ;

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
        if (!canClientCreateCard(currentClient, CardType.valueOf(cardType), CardColor.valueOf(cardColor))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client already has 3 cards of this type");
        }
        String cardHolder = currentClient.getFirstName() + " " + currentClient.getLastName();
        Card newCard = new Card(cardHolder, CardType.valueOf(cardType), CardColor.valueOf(cardColor), generateUniqueCardNumber(), CardUtils.generateRandomCVV(), LocalDate.now().plusYears(5), LocalDate.now());
        //agrega la tarjeta al cliente
        currentClient.addCards(newCard);
        //guarda la tarjeta en la tabla Card
        cardService.saveCardInRepository(newCard);

        return ResponseEntity.status(HttpStatus.CREATED).body("Card created successfully");
    }

    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.DELETE)
    public ResponseEntity<Object> DeleteCard(@RequestParam String cardType, @RequestParam String cardColor, Authentication authentication) {

        Client currentClient = clientService.getClientFindByEmail(authentication.getName());


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
        Card deleteCard = returnCardForClientColorAndType(currentClient, CardColor.valueOf(cardColor), CardType.valueOf(cardType));
        if(deleteCard==null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Card not found");
        }
        cardService.deleteCardInRepository(deleteCard);

        return ResponseEntity.status(HttpStatus.CREATED).body("Card delete successfully");
    }

    private boolean canClientCreateCard(Client client, CardType type, CardColor color) {
        long cardCount = client.getCards().stream()
                .filter(card -> card.getType() == type && card.getColor() == color)
                .count();

        return cardCount < 3;
    }

    private String generateUniqueCardNumber() {
        /*
        Proposito: Crea un numero de cuenta unico.
        retorna: un numero de cuenta unico en String
        * */
        boolean verificacionDeCuenta = true;
        String numberCard = CardUtils.generateCardNumber();
        do {
            if (isAccountNumberUnique(numberCard)) {
                verificacionDeCuenta = false;
            }
        } while (verificacionDeCuenta);
        return numberCard;

    }

    /*private short generateRandomCVV() {
        return (short) ((short) (Math.random() * (999 - 001 + 1)) + 001);
    }*/

    private boolean isAccountNumberUnique(String accountNumber) {
        //verifica si el numero de la tajeta esta repetido o no
        //Retorna: si es un numero no esta repetido devolverá thue, si está repetido devolverá false
        return cardService.getCardFindByNumber(accountNumber) == null;
    }

    private boolean hasDuplicateCard(Client client, CardColor cardColor, CardType cardType) {
        return client.getCards().stream()
                .anyMatch(card -> card.getColor() == cardColor && card.getType() == cardType);
    }

    private Card returnCardForClientColorAndType(Client client, CardColor cardColor, CardType cardType) {
       List <Card> steamCard = client.getCards().stream().collect(Collectors.toList());
        for (Card card : steamCard) {
            if(card.getColor() == cardColor && card.getType() == cardType){
                return card;
            }
        }
        return null;
    }


}
