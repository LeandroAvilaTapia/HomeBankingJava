package com.mindhub.homebankingAP.services.implement;

import com.mindhub.homebankingAP.dtos.CardDTO;
import com.mindhub.homebankingAP.models.Card;
import com.mindhub.homebankingAP.repositories.CardRepository;
import com.mindhub.homebankingAP.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class CardServiceImplement implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Override
    public List<CardDTO> getAllCards() {
        return cardRepository.findAll().stream().map(CardDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<CardDTO> getCurrentClientCards(String email) {
        return cardRepository.findByClient_email(email).stream().map(CardDTO::new).collect(Collectors.toList());
    }

    @Override
    public void saveCardInRepository(Card card) {
        cardRepository.save(card);
    }

    @Override
    public Card getCardFindByNumber(String number) {
        return cardRepository.findByNumber(number);
    }

    @Override
    public void deleteCardInRepository(Card card) {
        cardRepository.delete(card);
    }
}
