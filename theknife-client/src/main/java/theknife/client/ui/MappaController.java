package theknife.client.ui;

import java.util.List;
import java.util.function.Consumer;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import theknife.common.dto.RistoranteDTO;

/**
 * Controller del componente mappa riusabile (fx:include in ogni schermata
 * che la mostra): un {@link WebView} che carica Leaflet.js per il rendering
 * di tile e marker (decisione 34, confermata dal prof: JavaScript limitato
 * alla presentazione e all'interno di JavaFX).
 * <p>
 * Il JavaScript nella pagina caricata ({@code mappa/mappa.html}) fa solo da
 * motore grafico — disegna tile e marker, nessuna logica applicativa, nessun
 * accesso a dati o rete al di fuori dei tile della mappa stessa. I dati
 * (posizione, id, nome dei ristoranti) arrivano dal Java tramite
 * {@link #impostaRistoranti(List)}, passati come argomenti puri a
 * {@code aggiungiMarker(...)} via {@link JSObject#call}, mai letti o decisi
 * dal JS. Ogni interazione (click su un marker) arriva qui tramite il bridge
 * Java↔JS e viene inoltrata al chiamante tramite
 * {@link #setOnMarkerClick(Consumer)}, che decide cosa fare — il componente
 * stesso non naviga mai da solo.
 *
 * @author Barlera Marco, 760000, VA
 */
public class MappaController {

    /** Il WebView che ospita la pagina della mappa. */
    @FXML private WebView webView;

    /** Il motore della pagina caricata, usato per invocare le funzioni JS di disegno. */
    private WebEngine webEngine;
    /** L'oggetto {@code window} della pagina, usato per chiamare le funzioni JS senza costruire stringhe. */
    private JSObject window;
    /** Se la pagina ha finito di caricare ed è pronta a ricevere marker. */
    private boolean pronta = false;
    /** Ristoranti richiesti prima che la pagina fosse pronta, disegnati appena lo è. */
    private List<RistoranteDTO> ristorantiInAttesa;
    /** Callback invocato al click su un marker, registrato dal chiamante. */
    private Consumer<Long> onMarkerClick;
    /**
     * Riferimento forte al bridge esposto a JS come {@code window.javaBridge}: senza un
     * campo che lo tenga vivo lato Java, il garbage collector può raccoglierlo anche se
     * JS lo referenzia ancora — le chiamate JS→Java smettono di funzionare in modo
     * silenzioso dopo un numero variabile di utilizzi (bug classico di JavaFX WebView).
     */
    private Bridge bridge;
    /** Numero massimo di marker disegnati insieme: oltre, il WebView (senza accelerazione grafica) si blocca. */
    private static final int MASSIMO_MARKER = 300;
    /**
     * Ritarda la chiamata a {@code invalidateSize()} finché il ridimensionamento
     * non si ferma per un istante: chiamate troppo ravvicinate durante un
     * resize rapido possono lasciare a Leaflet dei tile non richiesti (zone
     * grigie), invece di una sola chiamata sulla dimensione finale.
     */
    private final PauseTransition ritardoInvalidazione = new PauseTransition(Duration.millis(150));

    /**
     * Carica {@code mappa.html} nel {@link WebEngine} e, al termine del
     * caricamento, inietta il bridge Java↔JS ({@code window.javaBridge}) e
     * disegna eventuali ristoranti richiesti nel frattempo.
     */
    @FXML private void initialize() {
        webView.setContextMenuEnabled(false);
        // styleClass come attributo FXML piatto fallisce silenziosamente (AVVERTENZA nel log,
        // non fatale) sul builder speciale che FXML usa per WebView — stesso limite già visto
        // per contextMenuEnabled. Impostato qui invece che in mappa.fxml.
        webView.getStyleClass().add("mappa-riquadro");
        // WebView ha una dimensione preferita di default enorme (800x600, hardcoded in
        // JavaFX): anche con lo stesso HBox.hgrow del pannello a fianco, questa preferenza
        // di partenza lo fa dominare lo spazio disponibile. Azzerata: lo spazio si spartisce
        // davvero a metà con la lista, come faceva il vecchio pannello segnaposto.
        webView.setMinWidth(0);
        webView.setPrefWidth(1);
        webView.setPrefHeight(1);
        webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((obs, vecchio, nuovo) -> {
            if (nuovo == Worker.State.SUCCEEDED) {
                window = (JSObject) webEngine.executeScript("window");
                bridge = new Bridge();
                window.setMember("javaBridge", bridge);
                pronta = true;
                if (ristorantiInAttesa != null) {
                    disegnaRistoranti(ristorantiInAttesa);
                    ristorantiInAttesa = null;
                }
                invalidaDimensione();
            }
        });
        webEngine.load(getClass().getResource("mappa/mappa.html").toExternalForm());

        ritardoInvalidazione.setOnFinished(e -> invalidaDimensione());
        webView.widthProperty().addListener((obs, vecchia, nuova) -> ritardoInvalidazione.playFromStart());
        webView.heightProperty().addListener((obs, vecchia, nuova) -> ritardoInvalidazione.playFromStart());
    }

    /**
     * Avvisa Leaflet che il contenitore ha cambiato dimensione: senza questa
     * chiamata la libreria non ridisegna da sola i tile nelle zone diventate
     * visibili dopo un ridimensionamento del {@link WebView} (rimangono
     * grigie) — comportamento noto di Leaflet, non un bug nostro.
     */
    private void invalidaDimensione() {
        if (pronta) {
            window.call("invalidaDimensione");
        }
    }

    /**
     * Ridisegna la mappa con un marker per ogni ristorante indicato,
     * rimuovendo quelli precedenti. Se la pagina non ha ancora finito di
     * caricare, i ristoranti vengono disegnati appena è pronta.
     *
     * @param ristoranti i ristoranti da mostrare, con {@code latitudine}/{@code longitudine} valorizzate
     */
    public void impostaRistoranti(List<RistoranteDTO> ristoranti) {
        if (pronta) {
            disegnaRistoranti(ristoranti);
        } else {
            ristorantiInAttesa = ristoranti;
        }
    }

    /**
     * Mostra un solo ristorante, centrando e ingrandendo la mappa sulla sua
     * posizione (per Dettaglio, un pin statico invece della lista) — un solo
     * marker fa scattare il centraggio automatico di {@code adattaVista()},
     * niente da fare in più qui.
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
        if (pronta) {
            window.call("evidenziaMarker", idRistorante);
        }
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
     * Pulisce i marker precedenti e disegna un marker per ogni ristorante,
     * passando i dati come argomenti puri alla funzione JS di disegno.
     *
     * @param ristoranti i ristoranti da disegnare
     */
    private void disegnaRistoranti(List<RistoranteDTO> ristoranti) {
        window.call("pulisciMarker");
        // WebView non ha accelerazione grafica: centinaia di marker DOM la fanno bloccare
        // (ricerche senza filtri o con raggio ampio possono restituire migliaia di risultati).
        // La lista non ha questo limite (è virtualizzata), solo la mappa.
        List<RistoranteDTO> daDisegnare = ristoranti.size() > MASSIMO_MARKER
            ? ristoranti.subList(0, MASSIMO_MARKER)
            : ristoranti;
        for (RistoranteDTO r : daDisegnare) {
            window.call("aggiungiMarker", r.getIdRistorante(), r.getLatitudine(), r.getLongitudine());
        }
        window.call("adattaVista");
    }

    /**
     * Oggetto esposto al JavaScript della pagina come {@code window.javaBridge}:
     * il solo scopo è inoltrare l'id del marker cliccato al callback Java
     * registrato da {@link #setOnMarkerClick(Consumer)} — nessuna logica qui
     * dentro, solo passaggio dell'evento da JS a Java.
     */
    public class Bridge {
        /**
         * Chiamato dal JavaScript della pagina al click su un marker.
         * Il parametro è {@code double}, non {@code long}: JavaScript non ha
         * un tipo intero a 64 bit, i numeri sono sempre a virgola mobile — un
         * parametro {@code long} qui non trova corrispondenza quando
         * chiamato da JS e la chiamata fallisce senza errore visibile.
         *
         * Chiamata da JavaScript/WebKit, non garantito sia il thread JavaFX:
         * il callback (che tocca la UI, selezione nella lista) va sempre
         * incapsulato in {@link Platform#runLater}.
         *
         * @param id l'identificativo del ristorante corrispondente al marker cliccato
         */
        public void onMarkerClick(double id) {
            if (onMarkerClick != null) {
                long idRistorante = (long) id;
                Platform.runLater(() -> onMarkerClick.accept(idRistorante));
            }
        }
    }
}
