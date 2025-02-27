package com.example.unsplashapi.ClientToDB;

import com.example.unsplashapi.Interfaces.UnsplashClient;
import com.example.unsplashapi.JsonEntity.UnsplashPhoto;
import com.example.unsplashapi.Response.AggregatedPhotoData;
import com.example.unsplashapi.Response.UnsplashSearchResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Component
public class UnsplashClientImpl implements UnsplashClient {
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(UnsplashClientImpl.class);

    @Value("${unsplash.api-key}")
    private String apiKey;

    @Value("${unsplash.api-url}")
    private String apiUrl;
    private final Validator validator;
    private final Cache<String, AggregatedPhotoData> cache;

    public UnsplashClientImpl(WebClient.Builder webCLientBuilder,
                              @Value("${unsplash.api-key}") String apiKey,
                              @Value("${unsplash.api-url}") String apiUrl,
                              @Value("${cache.expiry}") Long cacheExpiry,
                              Validator validator) { // Injiser Validator
        try {
            this.webClient = webCLientBuilder.baseUrl(apiUrl).build();
            this.apiKey = apiKey;
            this.apiUrl = apiUrl;
            this.validator = validator;
            this.cache = Caffeine.newBuilder() // starter bygging av ny cache instans
                    .expireAfterWrite(cacheExpiry, TimeUnit.SECONDS) // konfiguterer utløpspolicy for cache
                    .maximumSize(1000) // maksimal 1000 oppføirnger
                    .build(); // fullfør bekreft
            logger.info("UnsplashClientImpl initialized with apikey: {}, apiUrl: {}, cacheExpiry: {}", apiKey, apiUrl, cacheExpiry);
        } catch (Exception e) {
            logger.error("Error Initializing UnsplashCLientImpl: ", e);
            throw e;
        }
    }

    @Override
    @CircuitBreaker(name = "unsplash", fallbackMethod = "searchPhotosFallback")
    public Mono<AggregatedPhotoData> searchPhotos(String query, int page, int perPage)  {
        logger.info("Searching for photos on page {} and per page {}.", page, perPage);
        // Definerer cache lagrings nøkkel
        String cacheKey = String.format("%s:%d:%d", query, page, perPage);
        // hvis gammel data er tilgjengelig, hent og returner
        AggregatedPhotoData cachedPhotos = cache.getIfPresent(cacheKey);
        if (cachedPhotos != null) {
            logger.info("Returning cached results for query: {}", query);
            return Mono.just(cachedPhotos); // du vet svaret allerede
        }
        // ellers hent data på nytt
        Mono<UnsplashSearchResponse> unsplashSearchResponseMono =  webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search/photos")
                        .queryParam("query", query)
                        .queryParam("page", page)
                        .queryParam("per_page",perPage)
                        .queryParam("client_id", apiKey)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    HttpStatusCode statusCode = response.statusCode();
                    logger.error("Received error status code {} from Unsplash API", response.statusCode());
                    String message = String.format("Error calling Unsplash API: %s", response.statusCode());

                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(responseBody -> {
                                logger.error("Unsplash API Error: {} - {}", statusCode, message);
                                logger.error("Unsplash API Response Body: {}", responseBody);
                                return Mono.error(WebClientResponseException.create(
                                        statusCode.value(),
                                        statusCode.toString(),
                                        response.headers().asHttpHeaders(),
                                        responseBody.getBytes(),
                                        null // charset
                                        ));
                            });
                })
                .bodyToMono(UnsplashSearchResponse.class) // Forteller WebClient å konvertere JSON-bodyen til en strøm (Flux) av UnsplashPhoto objekter. Tenk på det som å parse JSON-dataene og lage Java-objekter av dem.
                .doOnNext(response -> logger.info("Photos from Unsplash API: {}", response));

        return unsplashSearchResponseMono.flatMap(unsplashSearchResponse -> {
                    List<UnsplashPhoto> photos = new ArrayList<>();
                    for (UnsplashPhoto photo : unsplashSearchResponse.getResults()) {
                        Set<ConstraintViolation<UnsplashPhoto>> violations = validator.validate(photo);
                        if (!violations.isEmpty()) {
                            for (ConstraintViolation<UnsplashPhoto> violation : violations) {
                                logger.warn("Validation error: {} - {}", violation.getPropertyPath(), violation.getMessage());
                            }
                        } else {
                            photos.add(photo);
                        }
                    }
                    logger.info("Caching results for query: {}", query);
                    AggregatedPhotoData data = AggregatedPhotoData.builder()
                                    .photos(photos)
                                    .totalPages(unsplashSearchResponse.getTotal_pages())
                                    .total(unsplashSearchResponse.getTotal())
                                    .build();

                    cache.put(cacheKey, data);
                    return Mono.just(data);
                })
                .doOnError(e -> logger.error("Error calling Unsplash API: " +  e));
    }

    // fallback method for CircuitBreaker: plan b
    // throwable t returnerer et notat om feilen som skjedde
    public Mono<AggregatedPhotoData> searchPhotosFallback(String query, int page, int perPage, Throwable t) {
        logger.warn("Falling back to searchPhotosFallback method for query: {}", query, t);
        logger.error("Exception details: ", t); // Logg stack trace
        AggregatedPhotoData emptyResponse = AggregatedPhotoData.builder()
                .photos(Collections.emptyList())
                .totalPages(0)
                .total(0)
                .build();
        return Mono.just(emptyResponse);
    }
}
