package com.mindhub.homebankingAP.repositories;

import com.mindhub.homebankingAP.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Loan findById (long id);
    List<Loan> findAll();
}

