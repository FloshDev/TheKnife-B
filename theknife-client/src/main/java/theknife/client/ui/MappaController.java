package theknife.client.ui;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import theknife.common.dto.RistoranteDTO;

/**
 * Componente mappa riusabile (fx:include su Risultati/Preferiti/Dashboard/
 * Dettaglio): tile OpenStreetMap caricati come {@link Image} e disegnati con
 * {@link ImageView} dentro un {@link Pane}, marker come {@link Circle} —
 * nessun {@code WebView}, nessun JavaScript, nessun bridge Java↔JS (S45:
 * sostituisce la versione precedente basata su Leaflet dentro {@code WebView},
 * privo di accelerazione grafica). Pan/zoom/proiezione (Web Mercator, tile
 * XYZ) gestiti direttamente in Java, sul motore di rendering nativo di
 * JavaFX.
 *
 * @author Barlera Marco, 760000, VA
 */
public class MappaController {

    /**
     * Costruttore vuoto: tutta l'inizializzazione avviene in {@code initialize()},
     * chiamato da FXMLLoader dopo l'injection dei campi {@code @FXML}.
     */
    public MappaController() {
    }

    /** Dimensione in pixel di un tile OpenStreetMap, fissa per lo schema XYZ. */
    private static final int TILE = 256;
    /** Zoom minimo selezionabile (mondo intero). */
    private static final int ZOOM_MINIMO = 2;
    /** Zoom massimo selezionabile, stesso limite del provider di tile OSM. */
    private static final int ZOOM_MASSIMO = 19;
    /** Zoom usato quando c'è un solo marker da mostrare (Dettaglio). */
    private static final int ZOOM_SINGOLO = 14;
    /** Numero massimo di marker disegnati insieme, stesso limite della versione precedente. */
    private static final int MASSIMO_MARKER = 300;
    /** Schema URL dei tile OSM: {@code %d} = zoom, tile X, tile Y, in quest'ordine. */
    private static final String URL_TILE = "https://tile.openstreetmap.org/%d/%d/%d.png";
    /**
     * Policy di OpenStreetMap (osm.wiki/Blocked): richiede uno User-Agent che
     * identifichi l'applicazione, non uno generico — {@link Image} caricata
     * direttamente da URL non lo espone (usa quello di default della JVM),
     * risposta 403 sistematica. Impostato a mano su una richiesta HTTP fatta
     * da noi, i byte passati a {@link Image} dopo (vedi {@link #caricaTile}).
     */
    private static final String USER_AGENT = "TheKnife/1.0 (progetto universitario, Laboratorio Interdisciplinare B)";
    /** Colore di un marker non evidenziato. */
    private static final Color COLORE_MARKER = Color.web("#FAB12F");
    /** Colore del marker evidenziato (S45, decisione 34: stessa palette della versione precedente). */
    private static final Color COLORE_MARKER_EVIDENZIATO = Color.web("#FA812F");
    /** Raggio di un marker non evidenziato. */
    private static final double RAGGIO_MARKER = 8;
    /** Raggio del marker evidenziato, più grande per distinguerlo. */
    private static final double RAGGIO_MARKER_EVIDENZIATO = 11;

    /** Il contenitore radice, usato solo per il clip che ritaglia la mappa ai suoi bordi. */
    @FXML private StackPane root;
    /** Il livello su cui vengono disegnati tile e marker, e su cui si agganciano pan/zoom. */
    @FXML private Pane superficie;
    /** I bottoni +/- di zoom, sovrapposti alla mappa in alto a sinistra. */
    @FXML private VBox controlliZoom;
    /** L'attribuzione OpenStreetMap, obbligatoria per policy, in basso a destra. */
    @FXML private Label attribuzioneLabel;

    /** Centro della vista in pixel-mondo alla zoom corrente (proiezione Web Mercator). */
    private double centroX;
    /** Centro della vista in pixel-mondo alla zoom corrente (proiezione Web Mercator). */
    private double centroY;
    /** Livello di zoom corrente, vincolato a [{@link #ZOOM_MINIMO}, {@link #ZOOM_MASSIMO}]. */
    private int zoom = 5;

    /** Tile attualmente disegnati, chiave "zoom/tileX/tileY". */
    private final Map<String, ImageView> tileVisualizzati = new HashMap<>();
    /** Marker attualmente disegnati, chiave l'id del ristorante. */
    private final Map<Long, Circle> markerVisualizzati = new HashMap<>();
    /** I ristoranti mostrati dall'ultima chiamata a {@link #impostaRistoranti}. */
    private List<RistoranteDTO> ristorantiCorrenti = List.of();
    /** L'id del marker attualmente evidenziato, o -1 se nessuno. */
    private long marcatoreEvidenziato = -1;
    /** Callback invocato al click su un marker, registrato dal chiamante. */
    private Consumer<Long> onMarkerClick;

    /** Posizione del mouse e centro della vista all'inizio di un trascinamento sulla mappa. */
    private double dragOriginX, dragOriginY, dragCentroX, dragCentroY;
    /** Posizione dell'ultimo mousedown su un marker, per distinguere click da drag. */
    private double pressX, pressY;
    /**
     * {@code true} se {@link #adattaVista} è stata chiamata quando la superficie non aveva
     * ancora una dimensione reale (0×0, prima del primo passaggio di layout JavaFX) — capita
     * perché {@link #impostaRistoranti} arriva appena la schermata naviga, prima che il layout
     * sia passato. Con una superficie 1×1 nessun bbox ci sta se non allo zoom minimo: bloccata
     * lì, sembra "non zoomare" perché il salto percepito dallo zoom minimo è enorme. Rifatta
     * al primo resize valido invece di restare sbagliata.
     */
    private boolean adattamentoInSospeso = false;

    /**
     * Ridisegna solo dopo che il ridimensionamento si è fermato per un
     * istante, non a ogni singolo evento — stesso ritardo (150ms) già usato
     * dalla versione WebView per lo stesso motivo.
     */
    private final PauseTransition ritardoRidisegno = new PauseTransition(Duration.millis(150));

    /**
     * Imposta il ritaglio ai bordi, vincola bottoni/attribuzione alla loro
     * dimensione preferita (non allargati a coprire la mappa, vedi commento
     * sotto), centra la vista di default sull'Italia e collega i gestori di
     * pan/zoom (mouse, scroll, pizzico su trackpad).
     */
    @FXML private void initialize() {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());
        root.setClip(clip);

        controlliZoom.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        attribuzioneLabel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        zoom = 5;
        centroX = lonToWorldX(12.5, zoom);
        centroY = latToWorldY(42.5, zoom);

        ritardoRidisegno.setOnFinished(e -> {
            if (adattamentoInSospeso && superficie.getWidth() > 0 && superficie.getHeight() > 0) {
                adattamentoInSospeso = false;
                adattaVista(ristorantiCorrenti);
            } else {
                ridisegna();
            }
        });
        superficie.widthProperty().addListener((o, a, b) -> ritardoRidisegno.playFromStart());
        superficie.heightProperty().addListener((o, a, b) -> ritardoRidisegno.playFromStart());

        superficie.setOnMousePressed(this::handlePress);
        superficie.setOnMouseDragged(this::handleDrag);
        superficie.setOnScroll(this::handleScroll);
        superficie.setOnZoomFinished(e -> {
            impostaZoom(zoom + (e.getTotalZoomFactor() > 1 ? 1 : -1));
            e.consume();
        });
    }

    /**
     * Aumenta lo zoom di un livello, al click sul bottone "+".
     */
    @FXML private void handleZoomIn() {
        impostaZoom(zoom + 1);
    }

    /**
     * Diminuisce lo zoom di un livello, al click sul bottone "-".
     */
    @FXML private void handleZoomOut() {
        impostaZoom(zoom - 1);
    }

    /**
     * Ridisegna la mappa con un marker per ogni ristorante indicato,
     * rimuovendo quelli precedenti, e adatta la vista per mostrarli tutti.
     *
     * @param ristoranti i ristoranti da mostrare, con {@code latitudine}/{@code longitudine} valorizzate
     */
    public void impostaRistoranti(List<RistoranteDTO> ristoranti) {
        List<RistoranteDTO> daMostrare = new ArrayList<>(ristoranti.size() > MASSIMO_MARKER
            ? ristoranti.subList(0, MASSIMO_MARKER)
            : ristoranti);
        markerVisualizzati.values().forEach(c -> superficie.getChildren().remove(c));
        markerVisualizzati.clear();
        ristorantiCorrenti = daMostrare;
        marcatoreEvidenziato = -1;
        adattaVista(daMostrare);
    }

    /**
     * Mostra un solo ristorante, centrando e ingrandendo la mappa sulla sua
     * posizione (per Dettaglio, un pin statico invece della lista).
     *
     * @param ristorante il ristorante da mostrare
     */
    public void impostaPinSingolo(RistoranteDTO ristorante) {
        impostaRistoranti(List.of(ristorante));
    }

    /**
     * Centra la mappa sul marker del ristorante indicato ed evidenzia
     * temporaneamente il suo pallino — usato per collegare il click su una
     * card della lista al marker corrispondente, senza navigare altrove.
     *
     * @param idRistorante l'identificativo del ristorante il cui marker va evidenziato
     */
    public void evidenziaMarker(long idRistorante) {
        aggiornaAspettoMarker(marcatoreEvidenziato, COLORE_MARKER, RAGGIO_MARKER);
        marcatoreEvidenziato = idRistorante;
        aggiornaAspettoMarker(idRistorante, COLORE_MARKER_EVIDENZIATO, RAGGIO_MARKER_EVIDENZIATO);

        ristorantiCorrenti.stream()
            .filter(r -> r.getIdRistorante() == idRistorante)
            .findFirst()
            .ifPresent(r -> {
                centroX = lonToWorldX(r.getLongitudine(), zoom);
                centroY = latToWorldY(r.getLatitudine(), zoom);
                ridisegna();
            });
    }

    /**
     * Registra il callback invocato al click su un marker, con l'id del
     * ristorante corrispondente. Il componente non decide mai da solo cosa
     * fare al click: la decisione (es. aprire il dettaglio) resta al
     * controller della schermata che lo include.
     *
     * @param callback il callback da invocare con l'id del ristorante cliccato
     */
    public void setOnMarkerClick(Consumer<Long> callback) {
        this.onMarkerClick = callback;
    }

    /**
     * Cambia lo zoom di un livello per ogni scatto della rotella del mouse.
     *
     * @param e l'evento di scroll generato sulla superficie della mappa
     */
    private void handleScroll(ScrollEvent e) {
        impostaZoom(zoom + (e.getDeltaY() > 0 ? 1 : -1));
        e.consume();
    }

    /**
     * Registra il punto di partenza di un trascinamento sulla mappa.
     *
     * @param e l'evento generato dalla pressione del mouse sulla superficie
     */
    private void handlePress(MouseEvent e) {
        dragOriginX = e.getSceneX();
        dragOriginY = e.getSceneY();
        dragCentroX = centroX;
        dragCentroY = centroY;
    }

    /**
     * Sposta il centro della vista in base allo spostamento del mouse dal
     * punto di partenza registrato da {@link #handlePress}.
     *
     * @param e l'evento generato dal trascinamento del mouse sulla superficie
     */
    private void handleDrag(MouseEvent e) {
        centroX = dragCentroX - (e.getSceneX() - dragOriginX);
        centroY = dragCentroY - (e.getSceneY() - dragOriginY);
        ridisegna();
    }

    /**
     * Cambia il livello di zoom mantenendo lo stesso centro geografico:
     * la dimensione in pixel-mondo raddoppia a ogni livello, il centro va
     * riscalato di conseguenza. Tile della zoom precedente non riusabili
     * alla nuova scala, ridisegnati da zero.
     *
     * @param nuovoZoom il livello di zoom richiesto, vincolato a [{@value #ZOOM_MINIMO}, {@value #ZOOM_MASSIMO}]
     */
    private void impostaZoom(int nuovoZoom) {
        nuovoZoom = Math.max(ZOOM_MINIMO, Math.min(ZOOM_MASSIMO, nuovoZoom));
        if (nuovoZoom == zoom) {
            return;
        }
        double scala = Math.pow(2, nuovoZoom - zoom);
        centroX *= scala;
        centroY *= scala;
        zoom = nuovoZoom;
        svuotaTile();
        ridisegna();
    }

    /**
     * Sceglie lo zoom più alto in cui tutti i ristoranti indicati rientrano
     * nella superficie visibile (con un margine) e centra la vista sul loro
     * baricentro geografico — equivalente di {@code fitBounds} di Leaflet.
     *
     * @param ristoranti i ristoranti su cui adattare la vista
     */
    private void adattaVista(List<RistoranteDTO> ristoranti) {
        if (ristoranti.isEmpty()) {
            svuotaTile();
            ridisegna();
            return;
        }
        if (ristoranti.size() == 1) {
            RistoranteDTO r = ristoranti.get(0);
            zoom = ZOOM_SINGOLO;
            centroX = lonToWorldX(r.getLongitudine(), zoom);
            centroY = latToWorldY(r.getLatitudine(), zoom);
            svuotaTile();
            ridisegna();
            return;
        }

        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
        for (RistoranteDTO r : ristoranti) {
            minLat = Math.min(minLat, r.getLatitudine());
            maxLat = Math.max(maxLat, r.getLatitudine());
            minLon = Math.min(minLon, r.getLongitudine());
            maxLon = Math.max(maxLon, r.getLongitudine());
        }

        if (superficie.getWidth() <= 0 || superficie.getHeight() <= 0) {
            adattamentoInSospeso = true;
            return;
        }
        double larghezza = superficie.getWidth();
        double altezza = superficie.getHeight();
        int nuovoZoom = ZOOM_MINIMO;
        for (int z = ZOOM_MASSIMO; z >= ZOOM_MINIMO; z--) {
            double bboxLarghezza = lonToWorldX(maxLon, z) - lonToWorldX(minLon, z);
            double bboxAltezza = latToWorldY(minLat, z) - latToWorldY(maxLat, z);
            if (bboxLarghezza <= larghezza * 0.85 && bboxAltezza <= altezza * 0.85) {
                nuovoZoom = z;
                break;
            }
        }

        zoom = nuovoZoom;
        centroX = lonToWorldX((minLon + maxLon) / 2, zoom);
        centroY = latToWorldY((minLat + maxLat) / 2, zoom);
        svuotaTile();
        ridisegna();
    }

    /** Rimuove tutti i tile disegnati — usato prima di un cambio di zoom, mai riusabili alla nuova scala. */
    private void svuotaTile() {
        tileVisualizzati.values().forEach(iv -> superficie.getChildren().remove(iv));
        tileVisualizzati.clear();
    }

    /** Ricalcola e ridisegna tile e marker per il centro/zoom correnti. */
    private void ridisegna() {
        double larghezza = superficie.getWidth();
        double altezza = superficie.getHeight();
        if (larghezza <= 0 || altezza <= 0) {
            return;
        }
        double originX = centroX - larghezza / 2;
        double originY = centroY - altezza / 2;
        aggiornaTile(originX, originY, larghezza, altezza);
        aggiornaMarker(originX, originY);
    }

    /**
     * Calcola quali tile servono per la vista corrente, aggiunge quelli
     * mancanti e rimuove quelli non più visibili — solo i tile mancanti
     * vengono richiesti, quelli già a posto restano dove sono.
     *
     * @param originX pixel-mondo dell'angolo sinistro della vista
     * @param originY pixel-mondo dell'angolo superiore della vista
     * @param larghezza larghezza della superficie visibile, in pixel
     * @param altezza altezza della superficie visibile, in pixel
     */
    private void aggiornaTile(double originX, double originY, double larghezza, double altezza) {
        int n = (int) Math.pow(2, zoom);
        int txMin = (int) Math.floor(originX / TILE) - 1;
        int txMax = (int) Math.floor((originX + larghezza) / TILE) + 1;
        int tyMin = (int) Math.floor(originY / TILE) - 1;
        int tyMax = (int) Math.floor((originY + altezza) / TILE) + 1;

        Set<String> necessari = new HashSet<>();
        for (int tx = txMin; tx <= txMax; tx++) {
            for (int ty = tyMin; ty <= tyMax; ty++) {
                if (tx < 0 || ty < 0 || tx >= n || ty >= n) {
                    continue;
                }
                String chiave = zoom + "/" + tx + "/" + ty;
                necessari.add(chiave);
                ImageView iv = tileVisualizzati.get(chiave);
                if (iv == null) {
                    iv = new ImageView();
                    iv.setFitWidth(TILE);
                    iv.setFitHeight(TILE);
                    tileVisualizzati.put(chiave, iv);
                    superficie.getChildren().add(0, iv);
                    caricaTile(String.format(URL_TILE, zoom, tx, ty), iv);
                }
                iv.setLayoutX(tx * (double) TILE - originX);
                iv.setLayoutY(ty * (double) TILE - originY);
            }
        }
        tileVisualizzati.keySet().removeIf(chiave -> {
            if (necessari.contains(chiave)) {
                return false;
            }
            superficie.getChildren().remove(tileVisualizzati.get(chiave));
            return true;
        });
    }

    /**
     * Scarica un tile in background con lo User-Agent richiesto dalla policy
     * OSM e lo assegna a {@code destinazione} al termine. Un tile fallito
     * (rete, 403, tile fuori mappa) resta vuoto — non blocca gli altri né
     * mostra un errore, non è un'operazione che l'utente ha chiesto.
     *
     * @param url indirizzo del tile da scaricare
     * @param destinazione la ImageView a cui assegnare il tile scaricato
     */
    private void caricaTile(String url, ImageView destinazione) {
        TaskRunner.run(
            () -> {
                URLConnection connessione = new URL(url).openConnection();
                connessione.setRequestProperty("User-Agent", USER_AGENT);
                try (InputStream in = connessione.getInputStream()) {
                    return new Image(in);
                }
            },
            destinazione::setImage,
            eccezione -> { }
        );
    }

    /**
     * Crea (se manca) o riposiziona il marker di ogni ristorante in
     * {@link #ristorantiCorrenti}. Il click, con soglia di movimento tra
     * pressione e rilascio, non consuma il {@code MOUSE_PRESSED}: deve
     * risalire a {@link #handlePress} sulla superficie, altrimenti un
     * trascinamento iniziato su un marker userebbe coordinate di partenza
     * del gesto precedente.
     *
     * @param originX pixel-mondo dell'angolo sinistro della vista
     * @param originY pixel-mondo dell'angolo superiore della vista
     */
    private void aggiornaMarker(double originX, double originY) {
        for (RistoranteDTO r : ristorantiCorrenti) {
            long id = r.getIdRistorante();
            double x = lonToWorldX(r.getLongitudine(), zoom) - originX;
            double y = latToWorldY(r.getLatitudine(), zoom) - originY;

            Circle c = markerVisualizzati.get(id);
            if (c == null) {
                c = new Circle(id == marcatoreEvidenziato ? RAGGIO_MARKER_EVIDENZIATO : RAGGIO_MARKER,
                        id == marcatoreEvidenziato ? COLORE_MARKER_EVIDENZIATO : COLORE_MARKER);
                c.setStroke(Color.WHITE);
                c.setStrokeWidth(2);
                c.setOnMousePressed(e -> {
                    pressX = e.getSceneX();
                    pressY = e.getSceneY();
                });
                c.setOnMouseClicked(e -> {
                    if (Math.abs(e.getSceneX() - pressX) < 4 && Math.abs(e.getSceneY() - pressY) < 4) {
                        evidenziaMarker(id);
                        if (onMarkerClick != null) {
                            onMarkerClick.accept(id);
                        }
                    }
                });
                markerVisualizzati.put(id, c);
                superficie.getChildren().add(c);
            }
            c.setLayoutX(x);
            c.setLayoutY(y);
        }
    }

    /**
     * Cambia colore e raggio del marker indicato e, se è quello evidenziato,
     * lo porta in primo piano (altrimenti può restare sotto ad altri marker
     * disegnati dopo di lui).
     *
     * @param id identificatore del ristorante il cui marker va aggiornato
     * @param colore il nuovo colore del marker
     * @param raggio il nuovo raggio del marker
     */
    private void aggiornaAspettoMarker(long id, Color colore, double raggio) {
        Circle c = markerVisualizzati.get(id);
        if (c != null) {
            c.setFill(colore);
            c.setRadius(raggio);
            if (colore == COLORE_MARKER_EVIDENZIATO) {
                c.toFront();
            }
        }
    }

    /**
     * Longitudine → coordinata X in pixel-mondo alla zoom indicata (proiezione Web Mercator).
     *
     * @param lon longitudine, in gradi
     * @param z livello di zoom
     * @return la coordinata X in pixel-mondo
     */
    private static double lonToWorldX(double lon, int z) {
        return (lon + 180.0) / 360.0 * Math.pow(2, z) * TILE;
    }

    /**
     * Latitudine → coordinata Y in pixel-mondo alla zoom indicata (proiezione Web Mercator).
     *
     * @param lat latitudine, in gradi
     * @param z livello di zoom
     * @return la coordinata Y in pixel-mondo
     */
    private static double latToWorldY(double lat, int z) {
        double rad = Math.toRadians(lat);
        return (1 - Math.log(Math.tan(rad) + 1 / Math.cos(rad)) / Math.PI) / 2 * Math.pow(2, z) * TILE;
    }
}
