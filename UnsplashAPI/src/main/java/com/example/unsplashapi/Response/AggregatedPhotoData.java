package com.example.unsplashapi.Response;

import com.example.unsplashapi.JsonEntity.UnsplashPhoto;
import lombok.*;
import java.util.List;


@Value
@AllArgsConstructor
@Builder
public class AggregatedPhotoData {
    private List<UnsplashPhoto> photos;
    private int total;
    private int totalPages;
}
