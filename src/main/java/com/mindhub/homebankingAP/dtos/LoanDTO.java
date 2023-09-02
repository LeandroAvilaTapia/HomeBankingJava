package com.mindhub.homebankingAP.dtos;

import com.mindhub.homebankingAP.models.Loan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LoanDTO {
    private long id;
    private String name;
    private Double maxAmount;
    private List<Integer> payments;

    public LoanDTO(Loan loan) {
        this.id= loan.getId();
        this.name = loan.getName();
        this.maxAmount = loan.getMaxAmount();
        this.payments = new ArrayList<>(loan.getPayments());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }
}
