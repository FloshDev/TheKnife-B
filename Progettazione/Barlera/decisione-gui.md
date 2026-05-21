# Decisione GUI — JavaFX

## Scelta: JavaFX (vs Swing)

### Motivazioni

| Criterio | Swing | JavaFX | Vincitore |
|----------|-------|--------|-----------|
| Incluso in JDK 21 | Sì | No (dep Maven) | Swing |
| Setup Maven | Zero config | `javafx-maven-plugin` | Swing |
| Look moderno | Difficile | CSS nativo + temi | **JavaFX** |
| Separazione UI/logica | Manuale, verboso | FXML + Controller | **JavaFX** |
| Lavoro parallelo team | Difficile | FXML separato per schermata | **JavaFX** |
| SceneBuilder | No | Sì | **JavaFX** |
| Ecosistema librerie | Limitato | Ricco (AtlantaFX, Ikonli…) | **JavaFX** |
| Java 21 supporto | Completo | Completo (JavaFX 21/22) | Pari |

**Verdetto:** JavaFX vince su tutto ciò che impatta la qualità del prodotto finale e la collaborazione tra 4 persone. Il costo è solo il setup iniziale del `pom.xml`.

### Vincolo spec soddisfatto

Slide 19 specifica: *"realizzare l'applicazione con un'opportuna interfaccia grafica, usando il linguaggio Java e **gli strumenti utili**"* → nessuna restrizione su librerie esterne. Maven è nello stack obbligatorio, quindi dipendenze Maven sono implicite.

---

## Librerie adottate

### AtlantaFX
- Temi moderni pronti (Primer Light/Dark, Nord, Dracula, Cupertino)
- Componenti styled out-of-the-box: card, sidebar, navbar, badge, tag
- Stile adeguato a un'app TheFork-like senza lavoro grafico manuale
- Repo: https://github.com/mkpaz/atlantafx

### Ikonli
- Icone vettoriali SVG via Maven (niente file immagine)
- Pack disponibili: Material Design, FontAwesome, Bootstrap Icons
- Integrazione nativa con JavaFX (`IconView` come nodo JavaFX)
- Usato per icone stelle recensioni, navigazione, pulsanti azione

### ControlsFX
- Componenti extra non presenti in JavaFX standard:
  - `RatingControl` → stelle 1-5 per recensioni (utile!)
  - `Notifications` → toast/popup feedback operazioni
  - `SearchableComboBox` → filtri ricerca ristoranti
- Repo: https://github.com/controlsfx/controlsfx

### AnimateFX
- Animazioni predefinite su nodi JavaFX (FadeIn, SlideIn, ecc.)
- Usato con moderazione per transizioni tra schermate
- Repo: https://github.com/Typhon0/AnimateFX

### JXMapViewer2
- Mappa interattiva puro Java (tile OpenStreetMap) embeddabile in JavaFX via `SwingNode`
- Motivazione: spec slide 5 include `Latitudine` e `Longitudine` nella tabella `RistorantiTheKnife` → i dati ci sono, la mappa li valorizza
- Vincolo rispettato: puro Java, niente JavaScript/browser (slide 19: "usando il linguaggio Java")
- Repo: https://github.com/msteiger/jxmapviewer2

#### Personalizzazioni possibili

| Elemento | Personalizzabile | Come |
|----------|-----------------|------|
| Marker ristorante | Sì, completamente | `WaypointRenderer` custom con Java2D + antialiasing |
| Colori marker | Sì | Palette AtlantaFX (hex) passata al renderer |
| Tile provider | Sì | Sostituire con CartoDB Dark Matter o Stadia Maps → tema dark coerente con AtlantaFX |
| Overlay (cerchio ricerca, cluster) | Sì | `AbstractPainter` sopra la mappa |
| UI controls Swing (scrollbar, bordi) | No | Nascosti mettendo il `SwingNode` in `StackPane` senza clip visibile |

#### Trade-off

| Pro | Contro |
|-----|--------|
| Puro Java, nessun linguaggio esterno | Componente Swing dentro JavaFX — leggero mismatch architetturale |
| Marker e overlay completamente custom | Controlli nativi Swing (se esposti) non seguono tema AtlantaFX |
| Tile OSM gratis, niente API key | Richiede connessione internet per scaricare tile |
| Zoom, pan, click su marker out-of-the-box | `SwingNode` ha limitazioni con effetti JavaFX (es. blur, clip) |

#### Flusso schermata mappa

```
[Risultati ricerca]
  ├── Lista card ristoranti (sinistra)
  └── Mappa JXMapViewer2 (destra)
        ├── marker per ogni risultato
        ├── click marker → evidenzia card in lista
        └── click card → centra mappa su marker
```

---

## Setup Maven (snippet da aggiungere al pom.xml)

```xml
<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.2</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>21.0.2</version>
</dependency>

<!-- AtlantaFX -->
<dependency>
    <groupId>io.github.mkpaz</groupId>
    <artifactId>atlantafx-base</artifactId>
    <version>2.0.1</version>
</dependency>

<!-- Ikonli + pack Material -->
<dependency>
    <groupId>org.kordamp.ikonli</groupId>
    <artifactId>ikonli-javafx</artifactId>
    <version>12.3.1</version>
</dependency>
<dependency>
    <groupId>org.kordamp.ikonli</groupId>
    <artifactId>ikonli-materialdesign2-pack</artifactId>
    <version>12.3.1</version>
</dependency>

<!-- ControlsFX -->
<dependency>
    <groupId>org.controlsfx</groupId>
    <artifactId>controlsfx</artifactId>
    <version>11.2.1</version>
</dependency>

<!-- AnimateFX -->
<dependency>
    <groupId>io.github.typhon0</groupId>
    <artifactId>AnimateFX</artifactId>
    <version>1.2.4</version>
</dependency>

<!-- JXMapViewer2 -->
<dependency>
    <groupId>org.jxmapviewer</groupId>
    <artifactId>jxmapviewer2</artifactId>
    <version>2.6</version>
</dependency>

<!-- JavaFX Swing interop (per SwingNode) -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-swing</artifactId>
    <version>21.0.2</version>
</dependency>
```

> **Alternativa da chiedere al prof:** WebView + Leaflet.js (puro JavaScript, mappa superiore, zero Swing). Tecnicamente il linguaggio applicativo rimane Java — JavaScript è solo il renderer della mappa dentro un componente JavaFX. Se il prof approva, sostituisce JXMapViewer2 completamente.

---

## Alternativa alla mappa: Radar view (cerchio distanze)

### Idea

Invece di una mappa geografica reale, mostrare un **cerchio radar** centrato sull'utente dove ogni ristorante è un punto posizionato in base a distanza e angolo calcolati da lat/lon.

```
         N
    .  ·  ·  .
  ·   🍴    🍴  ·
 ·  🍴   📍    ·   📍 = utente
  ·      🍴   ·
    .  ·  ·  .
```

### Come funziona

1. **Distanza** (in km): formula Haversine da lat/lon utente → lat/lon ristorante
2. **Angolo** (bearing): `atan2(Δlon, Δlat)` → direzione cardinale
3. **Posizione sul canvas**: proiezione polare → coordinate cartesiane scalate al raggio del cerchio UI
4. Click su punto → apre dettaglio ristorante

### Implementazione JavaFX

- `Canvas` JavaFX con `GraphicsContext` — nessuna libreria esterna
- Cerchi concentrici = fasce di distanza (es. 1 km, 3 km, 5 km)
- Punti colorati per tipo cucina o fascia prezzo (usa palette TheKnife)
- Hover → tooltip con nome ristorante + distanza

### Tradeoff

| Pro | Contro |
|-----|--------|
| Zero dipendenze esterne | Nessun contesto geografico (strade, quartieri) |
| Nessuna connessione internet | Distanze approssimate (terra sferica ≠ percorso reale) |
| Puro Java, coerente con vincolo spec | Meno intuitivo per luoghi non familiari |
| Personalizzazione totale colori/stile | — |

### Quando preferirla

Adatta se il team vuole evitare la complessità di `SwingNode` + JXMapViewer2 o se la connessione internet non è garantita durante la demo d'esame.

---

## Prossimo step (compito Barlera)

Produrre mappa delle schermate per ruolo utente — da presentare al team nella sessione successiva.
