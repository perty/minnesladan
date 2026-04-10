# Minneslådan

En låda med minnen från mitt liv ihopkopplat med AI för att kunna ställa frågor.

- Språkmodeller för att hitta fakta i en stor text
- Fristående från nätet

## Framtiden
- Taligenkänning
- Röstsyntes
  
## Provköra

Du behöver ha [Docker](https://docker.com) installerat. Om du inte har en aning om vad det är så är det korta svaret ett sätt att köra flera servrar på en dator isolerade från varandra och från värddatorn.

### 1. Bygg
Gå till katalogen `core` som har en fil `Dockerfile`. Den definierar hur appen byggs. Första gången tar det lång tid för den behöver ladda ner allting som krävs. Kör följande kommando:

`docker build -t minnesladan-app:latest .`

Notera punkten. Bygget skapar en `image` för appen.

För att ladda ner LLM till Ollama så kör du:
[init-onprem-llm.sh](core/init-onprem-llm.sh).

### 2. Starta

Nästa steg är att köra appen tillsammans med en databas och en AI-modell.

`docker compose up`

### 3. Använd

Nu finns appen tillgänglig på http://localhost:8080.

Det första du vill göra är att gå in på Admin och ladda in den fejkstory som finns inbyggd. Kräver bara en knapptryckning.

Sedan kan du ställa frågor till personen bakom fejkstoryn.

Se också: [StoryImportService.java](core/src/main/java/se/minnesladan/core/service/StoryImportService.java).

## Utveckla

För att jobba med appen så kör du den i din utvecklingsmiljö som vanligt. Det är en Spring Boot app som byggs med Maven. GUI är standard HTML och Javascript.

Du behöver köra de andra två delarna, databas och AI-modell för att kunna testa. 

För att uppdatera datbasmodellen så skriver du ett script. Se `src/main/resources/db/migration`.

