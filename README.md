
<details>
<summary><b style="font-size: 1.7em;">🇬🇧 English</b></summary>

# Weather Application

A simple weather application built with Kotlin and Javalin that integrates with public APIs to fetch weather information for a given city.

## Features

- Users can enter a city and get current weather information
- Validates that the specified location is actually a city
- Integrates with OpenStreetMap API to find coordinates for cities
- Fetches weather data from yr.no API based on coordinates
- Presents relevant weather information (temperature, weather type, wind speed, humidity)
- Robust error handling for various API errors and invalid inputs
- Caching of weather data to reduce the number of API calls (data is cached for 30 minutes)
- Formatted JSON response for better readability
- Logging of cache usage (HIT/MISS)
- Structured frontend with separated HTML, CSS and JavaScript files

## Technologies

- Kotlin
- Javalin (web framework)
- OkHttp (HTTP client)
- Jackson (JSON parsing)
- JUnit and Mockito (for testing)
- Bootstrap (CSS framework)
- Font Awesome (icons)

## Project Structure

```
Docker
├── Dockerfile                   - Dockerfile and compose to run
└── docker-compose.yml

src/main/kotlin/com/weatherapp/
├── Main.kt                      - Main application and server settings
├── controller/
│   └── WeatherController.kt     - Handles HTTP requests
├── service/
│   └── WeatherService.kt        - Contains business logic and API integrations
├── model/
│   ├── WeatherResponse.kt       - Application response model
│   ├── LocationModel.kt         - Models for location data (Coordinates, OpenStreetMapResponse)
│   └── YrModels.kt              - Models for yr.no API responses
├── cache/
│   └── CacheModel.kt            - Cache-related data models
└── exception/
    ├── OpenStreetMapException.kt  - Custom exceptions
    └── WeatherApiException.kt
    
src/main/resources/
└── public/                      - Static resources for frontend
    ├── index.html               - Main HTML structure
    ├── css/
    │   └── styles.css           - Separated CSS styles
    └── js/
        └── weather.js           - Separated JavaScript functionality
    
src/test/kotlin/com/weatherapp/
└── service/
    └── WeatherServiceTest.kt    - Unit tests for WeatherService
```

This structure follows a package-based organization that is common in JVM environments and makes the code more maintainable and clear. The frontend part now uses a structured division where HTML, CSS, and JavaScript are separated for better maintainability.

## Getting Started

### Prerequisites

- JDK 17 or later
- Gradle

### Compilation and Running

**GitHub option:**

1. Clone the project:
```
git clone https://github.com/RamtinGolrang/WeatherApp.git
```
```
cd WeatherApp
```

2. Build the project:
```
./gradlew build
```

3. Run the application:
```
./gradlew run
```

The application will start on `http://localhost:7070`

**Docker option:**

Docker Deployment

1. Pull the Docker image:
```
docker pull ramtin93/weatherapp:latest
```

2. Run the application:
```
docker run -p 7070:7070 ramtin93/weatherapp:latest
```

The application will be accessible at `http://localhost:7070`.

**If port 7070 is occupied you can config it with docker:**

- **Automatic port mapping**: You can use the `-P` flag to let Docker automatically assign a port:
  ```
  docker run -P firstnameLastname/weatherApp:latest
  ```
  Use `docker ps` to see which port was assigned.


- **Custom port mapping**: If you want to use a different port on your host machine:
  ```
  docker run -p 8080:7070 firstnameLastname/weatherApp:latest
  ```
  This makes the app available at `http://localhost:8080`.

### Usage
Retrieve weather data for a city directly via the web interface by opening `http://localhost:7070` in your browser.

Or by using the API:
```
GET http://localhost:7070/weather?city=CITYNAME
```
The response will be in JSON format:
```json
{
  "temperature": 15.2,
  "windSpeed": 3.5,
  "humidity": 65.0,
  "description": "Delvis molnigt",
  "symbolCode": "partlycloudy_day",
  "updatedAt": "2023-10-16T12:00:00Z"
}
```

## Design Decisions and Considerations

### Architecture

I chose to use a package-based structure with clear separation of responsibilities:

- **Controller** - Handles incoming HTTP requests and responses
- **Service** - Contains business logic and API integrations
- **Models** - Represents data from external APIs and internal data structures
- **Cache** - Handles caching of data for optimized performance
- **Exceptions** - Custom exception classes for clear error handling
- **Frontend** - Divided into structured components (HTML, CSS, and JavaScript)

This division makes the code easier to maintain and test, and follows the principle of "separation of concerns". By organizing the code into packages based on functionality, the code also becomes more scalable and easier to navigate as the project grows.

### Frontend Structure

For the frontend, I've chosen to separate HTML, CSS, and JavaScript into different files:

- **index.html** - Contains only the structure and markup
- **styles.css** - Contains all styles and visual definitions
- **weather.js** - Contains all interactive functionality and API calls

This separation makes the code easier to maintain, enables better caching in the browser, and simplifies the workflow if multiple developers are working on the project simultaneously.

### Testability

The code is designed with testability in mind:

- **Open classes and factory methods**: The Service class is designed as `open` with interchangeable dependencies via factory methods, which enables effective mocking.
- **Pure logic**: Business logic is isolated from HTTP and JSON handling, making it easier to test.
- **Clear interfaces**: Each component has a clear area of responsibility, which simplifies test writing.

### Error Handling

I implemented extensive error handling with specific exception classes and HTTP status codes:

- 400 Bad Request - If the user does not specify a city
- 404 Not Found - If the city is not found
- 500 Internal Server Error - For API errors or internal errors

All errors are returned with a clear error message in JSON format and presented in a user-friendly way in the web interface.

### Caching

To reduce the number of API calls and improve performance, I implemented a simple caching mechanism. Weather data is cached for 30 minutes, which provides a good balance between performance and data freshness.

## Unit Tests

The application includes complete unit tests using JUnit and Mockito. The tests verify the application's important functionality:

- **Successful retrieval of weather data**: Verifies that the application correctly fetches and interprets data from the APIs.
- **Error handling for invalid locations**: Tests that the application correctly identifies and handles attempts to search for places that don't exist.
- **Validation of city type**: Ensures that only actual cities are accepted as valid input.
- **Caching functionality**: Verifies that the caching mechanism works as expected and reduces the number of API calls.
- **Robust API error handling**: Tests the application's response when external APIs fail.

The tests use Mockito to simulate external API calls, making the tests fast and reliable without needing to make actual network calls.

## Possible Improvements

- **More robust tests** - Expand the tests to cover more scenarios
- **Persistence of cache** - Implement storing the cache to disk
- **More detailed weather information** - Show forecast for upcoming days
- **Responsive design** - Improve mobile usage of the web interface
- **Animated weather icons** - Add dynamic weather icons
- **Configurability** - Make the cache TTL and other parameters configurable

## Things I Focused On

- Robust API integration with proper error handling
- Well-structured and readable code
- Caching to optimize performance
- Testability
- User-friendly and error-catching interface
- Structured and maintainable frontend with separated components
</details>

<details open>
<summary><b style="font-size: 1.7em;">🇸🇪 Svenska</b></summary>

# Väderapplikation

En enkel väderapplikation byggd med Kotlin och Javalin som integrerar med publika API:er för att hämta väderinformation för en given stad.

## Funktioner

- Användaren kan ange en stad och få aktuell väderinformation
- Validerar att den angivna platsen faktiskt är en stad
- Integrerar med OpenStreetMap API för att hitta koordinater för städer
- Hämtar väderdata från yr.no API baserat på koordinater
- Presenterar relevant väderinformation (temperatur, vädertyp, vindhastighet, luftfuktighet)
- Robust felhantering för olika API-fel och ogiltig indata
- Caching av väderdata för att minska antalet API-anrop (data cachas i 30 minuter)
- Formaterat JSON-svar för bättre läsbarhet
- Loggning av cache-användning (HIT/MISS)
- Strukturerad frontend med separerade HTML, CSS och JavaScript-filer

## Teknologier

- Kotlin
- Javalin (webramverk)
- OkHttp (HTTP-klient)
- Jackson (JSON-parsing)
- JUnit och Mockito (för testning)
- Bootstrap (CSS-ramverk)
- Font Awesome (ikoner)

## Projektstruktur

```
Docker
├── Dockerfile                   - Dockerfil och körbar compose
└── docker-compose.yml

src/main/kotlin/com/weatherapp/
├── Main.kt                      - Huvudapplikation och serverinställningar
├── controller/
│   └── WeatherController.kt     - Hanterar HTTP-förfrågningar
├── service/
│   └── WeatherService.kt        - Innehåller affärslogik och API-integrationer
├── model/
│   ├── WeatherResponse.kt       - Applikationens svarmodell
│   ├── LocationModel.kt         - Modeller för platsdata (Coordinates, OpenStreetMapResponse)
│   └── YrModels.kt              - Modeller för yr.no API-svar
├── cache/
│   └── CacheModel.kt            - Cache-relaterade datamodeller
└── exception/
    ├── OpenStreetMapException.kt  - Anpassade exceptions
    └── WeatherApiException.kt
    
src/main/resources/
└── public/                      - Statiska resurser för frontend
    ├── index.html               - Huvudsaklig HTML-struktur
    ├── css/
    │   └── styles.css           - Separerade CSS-stilar
    └── js/
        └── weather.js           - Separerad JavaScript-funktionalitet
    
src/test/kotlin/com/weatherapp/
└── service/
    └── WeatherServiceTest.kt    - Enhetstester för WeatherService
```

Denna strukturering följer en paketbaserad organisation som är vanlig i JVM-miljöer och gör koden mer underhållbar och överskådlig. Frontend-delen använder nu en strukturerad uppdelning där HTML, CSS och JavaScript är separerade för bättre underhållbarhet.

## Kom igång

### Förutsättningar

- JDK 17 eller senare
- Gradle

### Kompilering och körning

**GitHub alternativ:**

1. Klona projektet:
```
git clone https://github.com/RamtinGolrang/WeatherApp.git
```
```
cd WeatherApp
```

2. Bygg projektet:
```
./gradlew build
```

3. Kör applikationen:
```
./gradlew run
```
Applikationen kommer att starta på `http://localhost:7070`

**Docker alternativ:**

1. Hämta Docker-imagen:
```
docker pull ramtin93/weatherapp:latest
```

2. Kör applikationen:
```
docker run -p 7070:7070 ramtin93/weatherapp:latest
```

Applikationen kommer att vara tillgänglig på `http://localhost:7070`.

**Om port 7070 är upptagen så finns alternativa lösningar:**

- **Automatisk portmappning**: Du kan använda flaggan `-P` för att låta Docker automatiskt tilldela en port:
  ```
  docker run -P ramtin93/weatherapp:latest
  ```
  Använd `docker ps` för att se vilken port som tilldelats.


- **Anpassad portmappning**: Om du vill använda en annan port på din värdmaskin:
  ```
  docker run -p 8080:7070 ramtin93/weatherapp:latest
  ```
  Detta gör applikationen tillgänglig på `http://localhost:8080`.


### Användning
Hämta väderdata för en stad direkt via webbgränssnittet genom att öppna `http://localhost:7070` i din webbläsare.

Eller genom att använda API:et:
```
GET http://localhost:7070/weather?city=STAD
```
Svaret kommer att vara i JSON-format:
```json
{
  "temperature": 15.2,
  "windSpeed": 3.5,
  "humidity": 65.0,
  "description": "Delvis molnigt",
  "symbolCode": "partlycloudy_day",
  "updatedAt": "2023-10-16T12:00:00Z"
}
```

## Designbeslut och tankegångar

### Arkitektur

Jag valde att använda en paketbaserad struktur med tydlig separation av ansvar:

- **Controller** - Hanterar inkommande HTTP-förfrågningar och svar
- **Service** - Innehåller affärslogik och API-integrationer
- **Modeller** - Representerar data från externa API:er och interna datastrukturer
- **Cache** - Hanterar cachning av data för optimerad prestanda
- **Exceptions** - Anpassade undantagsklasser för tydlig felhantering
- **Frontend** - Uppdelad i strukturerade komponenter (HTML, CSS och JavaScript)

Denna uppdelning gör koden lättare att underhålla och testa, och följer principen om "separation of concerns". Genom att organisera koden i paket baserat på funktionalitet blir koden också mer skalbar och lättare att navigera när projektet växer.

### Frontend-struktur

För frontenden har jag valt att separera HTML, CSS och JavaScript i olika filer:

- **index.html** - Innehåller endast strukturen och markup
- **styles.css** - Innehåller alla stilar och visuella definitioner
- **weather.js** - Innehåller all interaktiv funktionalitet och API-anrop

Denna uppdelning gör koden lättare att underhålla, möjliggör bättre caching i webbläsaren och förenklar arbetsflödet om flera utvecklare jobbar på projektet samtidigt.

### Testbarhet

Koden är designad med testbarhet i åtanke:

- **Öppna klasser och factory-metoder**: Service-klassen är designad som `open` med utbytbara beroenden via factory-metoder, vilket möjliggör effektiv mockning.
- **Renodlad logik**: Affärslogiken är isolerad från HTTP- och JSON-hantering, vilket gör den lättare att testa.
- **Tydliga gränssnitt**: Varje komponent har ett tydligt ansvarsområde, vilket förenklar testskrivning.

### Felhantering

Jag implementerade omfattande felhantering med specifika undantagsklasser och HTTP-statuskoder:

- 400 Bad Request - Om användaren inte anger en stad
- 404 Not Found - Om staden inte hittas
- 500 Internal Server Error - För API-fel eller interna fel

Alla fel returneras med ett tydligt felmeddelande i JSON-format och presenteras användarvänligt i webbgränssnittet.

### Caching

För att minska antalet API-anrop och förbättra prestanda implementerade jag en enkel cache-mekanism. Väderdata cachas i 30 minuter, vilket ger en bra balans mellan prestanda och dataaktualitet.

## Enhetstester

Applikationen inkluderar kompletta enhetstester som använder JUnit och Mockito. Testerna verifierar applikationens viktiga funktionalitet:

- **Framgångsrik hämtning av väderdata**: Verifierar att applikationen korrekt hämtar och tolkar data från API:erna.
- **Felhantering vid ogiltiga platser**: Testar att applikationen korrekt identifierar och hanterar försök att söka efter platser som inte finns.
- **Validering av stadstyp**: Säkerställer att bara faktiska städer accepteras som giltiga indata.
- **Caching-funktionalitet**: Verifierar att caching-mekanismen fungerar som förväntat och minskar antalet API-anrop.
- **Robust API-felhantering**: Testar applikationens respons när externa API:er misslyckas.

Testerna använder Mockito för att simulera externa API-anrop, vilket gör testerna snabba och pålitliga utan att behöva göra faktiska nätverksanrop.

## Möjliga förbättringar

- **Mer robusta tester** - Utöka testerna för att täcka fler scenarier
- **Persistens av cache** - Implementera lagring av cache till disk
- **Mer detaljerad väderinformation** - Visa prognos för kommande dagar
- **Responsiv design** - Förbättra mobilanvändning av webbgränssnittet
- **Animerade väderikoner** - Lägga till dynamiska väderikoner
- **Konfigurerbarhet** - Göra cachens TTL och andra parametrar konfigurerbara

## Saker jag fokuserade på

- Robust API-integration med ordentlig felhantering
- Välstrukturerad och läsbar kod
- Caching för att optimera prestanda
- Testbarhet
- Användarvänligt och felfångande gränssnitt
- Strukturerad och underhållbar frontend med separerade komponenter

</details>