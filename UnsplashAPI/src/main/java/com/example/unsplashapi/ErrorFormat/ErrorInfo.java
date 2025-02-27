package com.example.unsplashapi.ErrorFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ErrorInfo {
    private String errorType; // mer spesifikk feiltype. f.eks "uventet feil" eller ugyldig argument
    private String message; // detaljert feilmelding
}
