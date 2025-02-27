package com.example.unsplashapi.Interfaces;

import com.example.unsplashapi.JsonEntity.UnsplashPhoto;
import com.example.unsplashapi.Response.AggregatedPhotoData;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.util.List;

// Steg 2: opprett et interface

// navne suffikset Client er vanlig for klasser som er ansvarige for å kommunisere med eksterne tjenester
public interface UnsplashClient {
    // bruker List istedenfor Page, fordi jeg mottar List fra api'et deres og må bruke paginering i service layer

    /**
     *
     * @param query
     * @param page
     * @param perPage
     * @return
     * @throws JsonProcessingException
     */
    Mono<AggregatedPhotoData> searchPhotos(String query, int page, int perPage) throws JsonProcessingException;

}
