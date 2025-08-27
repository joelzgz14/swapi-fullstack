package com.example.swapi.service;

import com.example.swapi.dto.SwapiPage;
import com.example.swapi.model.Person;
import com.example.swapi.model.Planet;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Component
public class SwapiClient {

    private final WebClient client;
    private final String baseUrl;

    public SwapiClient(@Value("${app.swapi-base-url}") String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

        // Create an SSL context that trusts all certificates
        SslContext sslContext;
        try {
            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } catch (SSLException e) {
            throw new RuntimeException("Error creating SSL context", e);
        }

        // Create HTTP client with the SSL context
        HttpClient httpClient = HttpClient.create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext));

        // Build WebClient with custom SSL settings
        this.client = WebClient.builder()
                .baseUrl(this.baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public Mono<SwapiPage<Person>> fetchPeoplePage(int page) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/people/").queryParam("page", page).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ParameterizedTypes.personPage());
    }

    public Mono<SwapiPage<Planet>> fetchPlanetsPage(int page) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/planets/").queryParam("page", page).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ParameterizedTypes.planetPage());
    }

    // helper to avoid TypeReference repetition
    static class ParameterizedTypes {
        static org.springframework.core.ParameterizedTypeReference<SwapiPage<Person>> personPage() {
            return new org.springframework.core.ParameterizedTypeReference<SwapiPage<Person>>() {};
        }
        static org.springframework.core.ParameterizedTypeReference<SwapiPage<Planet>> planetPage() {
            return new org.springframework.core.ParameterizedTypeReference<SwapiPage<Planet>>() {};
        }
    }
}