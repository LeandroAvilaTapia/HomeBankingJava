package com.mindhub.homebankingAP.dtos;

import com.mindhub.homebankingAP.models.Card;
import com.mindhub.homebankingAP.models.CardColor;
import com.mindhub.homebankingAP.models.CardType;

import java.time.LocalDate;

public class CardDTO {
    private long id;
    private String cardHolder;  //titular de la tarjeta
    private CardType type;
    private CardColor color;
    private String number;  //numero de la tarjeta xxxx-xxxx-xxxx-xxxx
    private short cvv;
    private LocalDate thruDate;
    private LocalDate fromDate;

    public CardDTO(Card card) {
        this.id = card.getId();
        this.cardHolder = card.getCardHolder();
        this.type = card.getType();
        this.color = card.getColor();
        this.number = card.getNumber();
        this.cvv = card.getCvv();
        this.thruDate = card.getThruDate();
        this.fromDate = card.getFromDate();
    }

    public long getId() {
        return id;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public CardType getType() {
        return type;
    }

    public CardColor getColor() {
        return color;
    }

    public String getNumber() {
        return number;
    }

    public short getCvv() {
        return cvv;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }
}
