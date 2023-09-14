package com.mindhub.homebankingAP.repositories;

import com.mindhub.homebankingAP.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card,Long> {
    Card findByNumber(String number);
    List<Card> findByClient_email(String email);
    List<Card> findByClient_emailAndEnabled(String email,boolean enabled);

}
