package com.mindhub.homebankingAP.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableWebSecurity
@Configuration
class WebAuthorization {

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                // Permitir acceso público a recursos estáticos
                .antMatchers("/web/index.html", "/web/css/**", "/web/img/**", "/web/js/index.js").permitAll()
                .antMatchers("/web/index.html").permitAll()

                // Permitir registro y autenticación para todos
                .antMatchers(HttpMethod.POST, "/api/login", "/api/logout", "/api/clients").permitAll()

                // Restringir acceso a rutas administrativas (ADMIN)
                .antMatchers("/rest/**", "/h2-console/**").hasAuthority("ADMIN")

                // Restringir acceso a rutas de clientes (CLIENT)
                //.antMatchers("/web/**", "/api/**").hasAuthority("CLIENT")

                // Permitir acceso a la información del cliente autenticado
                .antMatchers("/web/**","/api/clients/current/**","/api/clients/current/accounts").hasAuthority("CLIENT")

                // Restringir acceso a crear cuentas y tarjetas para clientes (CLIENT)
                .antMatchers(HttpMethod.POST, "/api/clients/current/accounts", "/api/clients/current/cards", "/api/transactions").hasAuthority("CLIENT")

                // Denegar acceso a todos los demás endpoints
                .anyRequest().denyAll();


        http.formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        http.csrf().disable();


        //disabling frameOptions so h2-console can be accessed

        http.headers().frameOptions().disable();

        // if user is not authenticated, just send an authentication failure response

        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication

        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response

        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response

        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

        return http.build();

    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {

            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        }

    }


}