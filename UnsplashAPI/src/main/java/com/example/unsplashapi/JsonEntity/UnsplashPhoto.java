package com.example.unsplashapi.JsonEntity;

import com.example.unsplashapi.Response.UnsplashPhotoUrls;
import com.example.unsplashapi.Response.UnsplashUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


// Steg 1: lager en modellKlasse
@Value // Brukes for å gjøre klassen immutable, uforanderlig kun settere
@Builder(toBuilder = true) // genererer en toBuilder metode, true genererer, false gjør ingenting
@JsonIgnoreProperties(ignoreUnknown = true) // bruker denne slik at den ikke kræsjer, fordi jeg ikke vet om feltene mine samsvarer med deres entitet
public class UnsplashPhoto {
    @NotBlank(message = "ID cannot be blank")
    @JsonProperty("id") // den mapper id fra JSON objektet til denne
    private String id;

    @Valid
    @JsonProperty("urls")
    private UnsplashPhotoUrls urls;

    @JsonProperty("user")
    private UnsplashUser user;

    @JsonProperty("alt_description")
    private String altDescription;

    public String getPhotographerName() { // utløses når user ikke er null
        return (user != null) ? user.getName() : null;
    }
    public String getPhotograpgerProfileLink() { // utløses kun npr både user og user.getlinks ikke er null
        return (user != null && user.getLinks() != null) ? user.getLinks().getHtml() : null;
    }
    public String getImageUrl() { // Denne if-metoden utløses kun når både urls og urls.getRegular() ikke er null.
        // Hvis enten urls eller urls.getRegular() er null, returneres null direkte.
        // Hvis begge er gyldige, returneres urls.getRegular()
        return (urls != null && urls.getRegular() != null) ? urls.getRegular() : null;
    }
}
