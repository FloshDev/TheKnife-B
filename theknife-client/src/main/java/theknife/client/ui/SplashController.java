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
    /** Campo di testo per correggere a mano la località rilevata. */
    @FXML private TextField modificaLocalitaField;
    /** Mostra la località rilevata dall'IP della connessione. */
    @FXML private Label localitaLabel;

    /** Invia al server la richiesta di localizzazione iniziale (decisione 18). */
    private final RistoranteService ristoranteService = new RistoranteService();

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

        TaskRunner.run(
            () -> ristoranteService.ottieniLocalitaIniziale(),
            posizione -> {
                String luogo = posizione.getLuogo();
                localitaLabel.setText(luogo != null && !luogo.isBlank()
                    ? "Sembri trovarti a " + luogo
                    : "Non siamo riusciti a rilevare la tua posizione: scrivila qui sotto");
            },
            eccezione -> localitaLabel.setText("Non siamo riusciti a rilevare la tua posizione: scrivila qui sotto")
        );
    }

    /**
     * Conferma la località rilevata (o corretta a mano) e naviga alla Home.
     * È il percorso principale della schermata (bottone pieno) e quello
     * richiesto dalle direttive del docente: il nome del luogo lo digita
     * l'utente, il sistema non deve indovinarlo.
     *
     * @param event l'evento generato dal click sul bottone "Conferma"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleConferma(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
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

    /**
     * Naviga alla Home senza autenticarsi.
     *
     * @param event l'evento generato dal click sul link "Continua come ospite"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleGuest(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
        stage.getScene().setRoot(root);
    }
}
