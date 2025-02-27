package com.example.unsplashapi.ServiceClass;
import com.example.unsplashapi.Interfaces.PhotoService;
import com.example.unsplashapi.Interfaces.UnsplashClient;
import com.example.unsplashapi.Response.AggregatedPhotoData;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
    private final UnsplashClient unsplashClient;
    private final Logger logger = LoggerFactory.getLogger(PhotoServiceImpl.class);

    // legg til i application.properties "unsplash.default-per-page=10"
    @Value("${unsplash.default-per-page}")
    private int defaultPerPage;

    @Override
    public Mono<AggregatedPhotoData> getPhotos(String query, int page, int perPage) {
        int photosPerPage = (perPage > 0) ? perPage : defaultPerPage;
        try {
            return unsplashClient.searchPhotos(query, page, photosPerPage);
        } catch (JsonProcessingException e) {
            logger.error("Error while fetching photos from Unsplash API: {}", e.getMessage());
            return Mono.error(e);
        }
    }
}
