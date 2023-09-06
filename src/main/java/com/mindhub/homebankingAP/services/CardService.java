package com.mindhub.homebankingAP.services;

import com.mindhub.homebankingAP.dtos.CardDTO;
import com.mindhub.homebankingAP.models.Card;

import java.util.List;

public interface CardService {
    List<CardDTO> getAllCards();
    List<CardDTO> getCurrentClientCards(String email);
    void saveCardInRepository(Card card);
    Card getCardFindByNumber(String number);
}
