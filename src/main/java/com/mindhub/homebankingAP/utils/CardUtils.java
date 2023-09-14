package com.mindhub.homebankingAP.utils;

public final class CardUtils {
    static public String generateCardNumber() {
        // Genera un string aleatorio entre 0001 y 9999 con el formato xxxx-xxxx-xxxx-xxxx
        int randomNumber = (int) (Math.random() * (9999 - 1000)) + 1000;
        int randomNumber2 = (int) (Math.random() * (9999 - 1000)) + 1000;
        int randomNumber3 = (int) (Math.random() * (9999 - 1000)) + 1000;
        int randomNumber4 = (int) (Math.random() * (9999 - 1000)) + 1000;
        return randomNumber + "-" + randomNumber2 + "-" + randomNumber3 + "-" + randomNumber4;
    }

    static public short generateRandomCVV() {
        return (short) ((short) (Math.random() * (999 - 001 + 1)) + 001);
    }
}
