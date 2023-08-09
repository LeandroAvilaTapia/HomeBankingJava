package com.mindhub.homebankingAP.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String number;
    private LocalDate creationDate;
    private Double balance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "accounts_id")
    private Client client;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    Set<Transaction> transactions = new HashSet<>();

    public Account() {
    }

    public Account(String number, LocalDate creationDate, Double balance) {
        this.number = number;
        this.creationDate = creationDate;
        this.balance = balance;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }


    public String getNumber() {
        return number;
    }

    public long getId() {
        return id;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public LocalDate getCreationDate() {
        return creationDate;
    }


    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }


    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @JsonIgnore
    public Client getClient() {
        return client;
    }

    public Client getAccounts() {
        return client;
    }

    public void setAccounts(Client accounts) {
        this.client = accounts;
    }

    public void addTransactions(Transaction transaction) {
        transaction.setTransactions(this);
        transactions.add(transaction);
    }

}
