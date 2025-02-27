package com.example.unsplashapi.Response;

import com.example.unsplashapi.JsonEntity.UnsplashPhoto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // ignorer alle egenskaper i JSON-dataene som jeg
// ikke har definert felt for i java klassen min.
// ikke feil men bare hopp over dem
public class UnsplashSearchResponse {
    private List<UnsplashPhoto> results;
    private int total;
    private int total_pages;
}
