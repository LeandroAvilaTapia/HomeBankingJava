package com.mindhub.homebankingAP.configurations;

import com.mindhub.homebankingAP.models.Client;
import com.mindhub.homebankingAP.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {


    @Autowired

    ClientRepository clientRepository;


    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(inputName -> {

            Client client = clientRepository.findByEmail(inputName);

            if (client != null) {

                return new User(client.getEmail(), client.getPassword(),

                        AuthorityUtils.createAuthorityList("USER"));

            } else {

                throw new UsernameNotFoundException("Unknown user: " + inputName);

            }

        });

    }

}