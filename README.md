# GPS Locator

App Android che mostra posizione GPS in tempo reale. Usa **solo chip GPS hardware** — nessuna connessione dati, WiFi, o rete cellulare richiesta. Funziona in modalità aereo.

## Cosa mostra

- Latitudine / Longitudine (6 decimali)
- Altitudine
- Precisione (±metri)
- Velocità (km/h)
- Direzione (gradi)
- Satelliti agganciati / totali visibili
- Ora dell'ultimo fix GPS

## Requisiti

- Android Studio **Hedgehog** (2023.1.1) o superiore
- Android SDK 34
- Dispositivo o emulatore con GPS hardware (API 24+)

---

## Come caricare e avviare

### 1. Aprire il progetto in Android Studio

```
File → Open → seleziona la cartella: fallout_detector/
```

Attendere che Gradle sync si completi (prima volta scarica dipendenze, ~2-3 min).

### 2. Build

```
Build → Make Project   (oppure  Ctrl+F9)
```

### 3. Installare su dispositivo fisico

1. Sul telefono: **Impostazioni → Info telefono → tocca "Numero build" 7 volte** (attiva Opzioni sviluppatore)
2. **Impostazioni → Opzioni sviluppatore → Debug USB → ON**
3. Collega telefono al PC via USB
4. In Android Studio: seleziona il dispositivo nel menu a tendina in alto
5. Premi **Run** (▶) o `Shift+F10`

### 4. Installare su emulatore (test GPS simulato)

1. **Tools → Device Manager → Create Device**
2. Scegli un hardware con GPS (es. Pixel 6)
3. Avvia l'emulatore
4. Per simulare posizione GPS: in emulatore apri **Extended Controls (...)→ Location → Set Location**
5. Premi Run (▶)

### 5. Permessi runtime

Al primo avvio l'app chiede il permesso di accesso alla posizione precisa — concederlo. Senza permesso l'app non funziona.

---

## Note GPS

- In ambienti chiusi il fix GPS può richiedere **1-5 minuti** (cold start)
- All'aperto con cielo libero: fix in **15-60 secondi**
- Il telefono deve avere **GPS abilitato** (non basta disattivare WiFi/dati — il GPS è hardware separato)
- L'app NON usa "posizione approssimativa" via rete — solo chip GNSS

## Struttura progetto

```
fallout_detector/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml       # permessi GPS, no INTERNET
│       ├── java/com/example/gpslocator/
│       │   └── MainActivity.java     # logica GPS + UI
│       └── res/
│           ├── layout/activity_main.xml
│           └── values/strings.xml
├── build.gradle
└── settings.gradle
```
