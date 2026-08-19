package theknife.client.ui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import theknife.client.service.RistoranteService;

/**
 * Controller della schermata iniziale (S01).
 *
 * @author Barlera Marco, 760000, VA
 */
public class SplashController {
    /** Contenitore radice, usato solo per togliere il focus dal campo di testo all'apertura. */
    @FXML private VBox root;
    /** La card, la cui larghezza è agganciata alla finestra (grafica responsive). */
    @FXML private VBox card;
    /** Campo di testo per correggere a mano la località rilevata. */
    @FXML private TextField modificaLocalitaField;
    /** Mostra la località rilevata dall'IP della connessione. */
    @FXML private Label localitaLabel;

    /** Invia al server la richiesta di localizzazione iniziale (decisione 18). */
    private final RistoranteService ristoranteService = new RistoranteService();
    /**
     * Località rilevata dall'IP, se la geolocalizzazione riesce ({@code null}
     * altrimenti). Ripiego per {@link #handleConferma(ActionEvent)} quando
     * l'utente non scrive nulla in {@link #modificaLocalitaField}.
     */
    private String luogoRilevato;

    /**
     * Toglie il focus dal campo di testo (altrimenti JavaFX lo assegna
     * automaticamente all'apertura, coprendo il segnaposto) e tenta di
     * rilevare la posizione dell'utente dall'IP della connessione (decisione
     * 18). Su IP locale o di rete privata la geolocalizzazione fallisce
     * sistematicamente (decisione 30): in quel caso, come in ogni errore,
     * {@code localitaLabel} guida l'utente a scrivere la località a mano in
     * {@code modificaLocalitaField} invece di restare vuota.
     */
    @FXML private void initialize() {
        Platform.runLater(() -> root.requestFocus());
        Responsive.aggancia(card, 0.45, 360, 480);

        TaskRunner.run(
            () -> ristoranteService.ottieniLocalitaIniziale(),
            posizione -> {
                String luogo = posizione.getLuogo();
                if (luogo != null && !luogo.isBlank()) {
                    luogoRilevato = luogo;
                    localitaLabel.setText("Sembri trovarti a " + luogo);
                } else {
                    localitaLabel.setText("Non siamo riusciti a rilevare la tua posizione: scrivila qui sotto");
                }
            },
            eccezione -> localitaLabel.setText("Non siamo riusciti a rilevare la tua posizione: scrivila qui sotto")
        );
    }

    /**
     * Naviga alla Home, sia che l'utente abbia effettuato il login sia che
     * prosegua come ospite: senza autenticazione le due cose non si
     * distinguono, quindi è un solo percorso invece di due. Pre-riempie il
     * campo città di Home con la località corretta a mano in
     * {@link #modificaLocalitaField}, o in mancanza con quella rilevata
     * automaticamente ({@link #luogoRilevato}) — quella digitata ha sempre
     * la precedenza, coerente con la decisione 18 (l'utente ha l'ultima
     * parola sul luogo).
     *
     * @param event l'evento generato dal click sul bottone "Conferma e continua"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleConferma(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        String cittaCorretta = modificaLocalitaField.getText();
        String citta = (cittaCorretta != null && !cittaCorretta.isBlank()) ? cittaCorretta : luogoRilevato;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/home.fxml"));
        Parent root = loader.load();
        if (citta != null) {
            HomeController controller = loader.getController();
            controller.impostaCitta(citta);
        }
        stage.getScene().setRoot(root);
    }

    /**
     * Naviga alla schermata di login.
     *
     * @param event l'evento generato dal click sul link di login
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleLogin(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Non crei una finestra nuova, riusi quella che già esiste
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/login.fxml"));
        stage.getScene().setRoot(root);
    }
}
