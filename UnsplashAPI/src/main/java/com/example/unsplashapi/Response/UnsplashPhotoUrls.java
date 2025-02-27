package com.example.unsplashapi.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnsplashPhotoUrls {
    @JsonProperty("raw")
    @URL(message = "Raw URL must be a valid URL")
    String raw;
    @JsonProperty("full")
    @URL(message = "Full URL must be a valid URL")
    String full;
    @JsonProperty("regular")
    @URL(message = "Regular URL must be a valid URL")
    String regular;
    @JsonProperty("small")
    @URL(message = "Small URL must be a valid URL")
    String small;
    @JsonProperty("thumb")
    @URL(message = "Thumb URL must be a valid URL")
    String thumb;
    @JsonProperty("small_s3") // Nytt felt basert på JSON responsen din
    String smallS3;
}