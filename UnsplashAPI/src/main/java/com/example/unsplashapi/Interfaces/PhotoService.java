package com.example.unsplashapi.Interfaces;


import com.example.unsplashapi.JsonEntity.UnsplashPhoto;
import com.example.unsplashapi.Response.AggregatedPhotoData;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.util.List;
// Steg 3: Opprett PhotoService som henter bilder
/*
    PhotoService definerer hva applikasjonen trenger (å hente bilder),
    mens UnsplashClient definerer hvordan det gjøres (ved å kalle Unsplash API-et).
    PhotoService kan senere håndtere caching, kombinere flere bildekilder,
    eller transformere data, uten å endre UnsplashClient.
    UnsplashClient er bare en spesifikk måte å hente bilder på,
    mens PhotoService representerer applikasjonens behov.
 */

public interface PhotoService {
    Mono<AggregatedPhotoData> getPhotos(String query, int page, int perPage) ; // added pagination
}
