package com.prueba.pruebatecnica.infrastucture.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Esta configuracion está en MODO PERMISIVO, para así facilitar las prubeas locales sin necesidad de un proveedor
    //de identidad (IdP) activo.
    //Se comenta el codigo necesario para activar OAuth2 Resource Server (JWT)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //Deshabilitar CSRF ya que es necesario para APIs REST stateless acesibles desde clientes externos
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        //Seccion 1
                        //Permitimos acceso libre a la documentacion de la API (Swagger UI y OpenAPI docs)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/pedidos/cargar").permitAll()
                        .anyRequest().authenticated()

                        //Seccion 2
                        //Requisito de OAUTH2
                        //Descomentar este bloque y comentar la SECCION 1 para activar la seguridad OAuth2
                        /*
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                        */
                );

        // OAUTH2 Resource Server con JWT
        //Configuracion para validar tokens JWT contra un Issuer ya sea Auth0, Keycloak.
        //Se mantiene comentado para evitar errores de inicio si no hay un IdP activo.
        /*
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        */
        return http.build();
    }
}
