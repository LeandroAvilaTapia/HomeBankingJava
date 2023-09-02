package com.mindhub.homebankingAP.dtos;

public class LoanApplicationDTO {

    private long loanId;
    private double amount;
    private int payments;
    private String accountToNumber;

    public LoanApplicationDTO() {
    }

    public LoanApplicationDTO(long loanId, double amount, int payments, String accountToNumber) {
        this.loanId = loanId;
        this.amount = amount;
        this.payments = payments;
        this.accountToNumber = accountToNumber;
    }

    public Long getLoanId() {
        return loanId;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public String getAccountToNumber() {
        return accountToNumber;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setPayments(Integer payments) {
        this.payments = payments;
    }

    public void setAccountToNumber(String accountToNumber) {
        this.accountToNumber = accountToNumber;
    }
}

