package com.mindhub.homebankingAP.dtos;

import com.mindhub.homebankingAP.models.ClientLoan;

public class ClientLoanDTO {

    private long id;
    private long loanId;
    private String name;
    private double Amount;
    private int payments;
    public ClientLoanDTO(ClientLoan clientLoan){
        this.id = clientLoan.getId();
        this.loanId = clientLoan.getLoan().getId();
        this.name = clientLoan.getLoan().getName();
        this.Amount = clientLoan.getAmount();
        this.payments=clientLoan.getPayments();
    }

    public long getId() {
        return id;
    }

    public long getLoanId() {
        return loanId;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return Amount;
    }

    public int getPayments() {
        return payments;
    }
}
