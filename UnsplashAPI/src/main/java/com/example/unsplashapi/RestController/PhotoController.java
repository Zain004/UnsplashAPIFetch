package com.example.unsplashapi.RestController;

import com.example.unsplashapi.Interfaces.PhotoService;
import com.example.unsplashapi.Response.APIResponse;
import com.example.unsplashapi.JsonEntity.UnsplashPhoto;
import com.example.unsplashapi.Response.AggregatedPhotoData;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
//@CrossOrigin(origins = "${cors.allowed-origins}") // Bruk en variabel for å hente tillatte opprinnelser fra application.properties
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.0.xxx:3000"})// Dette er for testing, immdlertidig
// @CrossOrigin(origins = {"https://www.example.com"}) dette er ordentlig
@RequiredArgsConstructor
@RequestMapping("/unsplashController")
public class PhotoController {
    private final PhotoService photoService;

    private HttpHeaders createSecurityHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("X-XSS-Protection", "1; mode=block");
        return headers;
    }

    // implementer security headers
    // også globalExceptionhandler
    @GetMapping("/getPhotos")
    public Mono<ResponseEntity<APIResponse<AggregatedPhotoData>>> getPhotos(
            @RequestParam @NotBlank(message = "Query cannot be blank") String query,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(30) int perPage) {
         return photoService.getPhotos(query, page, perPage).map(photos -> {
             APIResponse<AggregatedPhotoData> response = APIResponse.<AggregatedPhotoData>builder()
                     .success(true)
                     .data(photos)
                     .message("Successfully retrieved photos")
                     .status(HttpStatus.OK)
                     .timeStamp(LocalDateTime.now())
                     .build();
             return ResponseEntity.ok()
                     .headers(createSecurityHeaders())
                     .body(response);
         });
    }
}
