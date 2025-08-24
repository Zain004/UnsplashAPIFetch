# Unsplash API Fetch – Spring Boot Prosjekt

Dette prosjektet henter bilder fra **Unsplash API** og viser hvordan man kan integrere et eksternt API i en Spring Boot-applikasjon. Prosjektet inkluderer caching, global feilbehandling og strukturert modellering av API-responsene.

---

## Komme i gang

1. Åpne følgende fil i nettleseren for å teste grensesnittet:  
   [Index-side](https://github.com/Zain004/UnsplashAPIFetch/blob/main/UnsplashAPI/src/main/resources/static/index.html)

2. Oppdater `application.properties` med din egen Unsplash API-nøkkel:  

```properties
spring.application.name=UnsplashAPI
unsplash.default-per-page=10
unsplash.api-key=YOUR_UNSPLASH_API_KEY_HERE
unsplash.api-url=https://api.unsplash.com
cache.expiry=600
