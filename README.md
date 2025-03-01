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

### Användning
Hämta väderdata för en stad direkt via webbgränssnittet genom att öppna `http://localhost:7070` i din webbläsare.

Eller genom att använda API:et:
```
GET http://localhost:7070/weather?city=STADSDEL
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