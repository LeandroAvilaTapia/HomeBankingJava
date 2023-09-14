package com.mindhub.homebankingAP.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;


@SpringBootTest
class CardUtilsTest {

    @Test
    void generateCardNumber() {
    }

    @Test
    public void testGenerateRandomCVV() {
        short cvv = CardUtils.generateRandomCVV();

        // Verifica que el CVV esté dentro del rango correcto (001-999)
        assertTrue(cvv >= 1 && cvv <= 999);
    }

    @Test
    public void testGenerateCardNumber() {
        String cardNumber = CardUtils.generateCardNumber();

        // Verifica que el número de tarjeta tenga el formato correcto
        assertTrue(cardNumber.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}"));
    }

    @Test

    public void cardNumberIsCreated(){

        String cardNumber = CardUtils.generateCardNumber();

        assertThat(cardNumber,is(not(emptyOrNullString())));

    }
}