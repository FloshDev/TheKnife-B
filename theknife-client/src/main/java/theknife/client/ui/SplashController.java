package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import theknife.client.service.RistoranteService;

/**
 * Controller della schermata iniziale (S01).
 *
 * @author Barlera Marco, 760000, VA
 */
public class SplashController {
    /** Campo di testo per correggere a mano la località rilevata. */
    @FXML private TextField modificaLocalitaField;
    /** Mostra la località rilevata dall'IP della connessione. */
    @FXML private Label localitaLabel;

    /** Invia al server la richiesta di localizzazione iniziale (decisione 18). */
    private final RistoranteService ristoranteService = new RistoranteService();

    /**
     * Tenta di rilevare la posizione dell'utente dall'IP della connessione
     * (decisione 18). Il fallimento è silenzioso e non mostra nulla: su IP
     * locale o di rete privata la geolocalizzazione fallisce sistematicamente,
     * e l'utente corregge a mano con {@code modificaLocalitaField}.
     */
    @FXML private void initialize() {
        TaskRunner.run(
            () -> ristoranteService.ottieniLocalitaIniziale(),
            posizione -> localitaLabel.setText(posizione.getLatitudine() + ", " + posizione.getLongitudine()),
            eccezione -> { /* silenzioso: fallisce sempre su IP locale/LAN, decisione 18 */ }
        );
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

    /**
     * Conferma la località rilevata (o corretta a mano) e naviga alla Home.
     *
     * @param event l'evento generato dal click sul bottone "Conferma"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleConferma(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
        stage.getScene().setRoot(root);
    }
}
