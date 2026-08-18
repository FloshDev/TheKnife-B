package theknife.client.ui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import theknife.client.network.ServerConnection;
import theknife.client.service.AuthService;
import theknife.client.service.RistoranteService;
import theknife.common.dto.CercaRistorantiDTO;
import theknife.common.dto.CercaVicinoDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.Ruolo;

/**
 * Controller della schermata Home (S04).
 *
 * @author Barlera Marco, 760000, VA
 */
public class HomeController {

    /** Contenitore radice, usato solo per togliere il focus dal primo campo all'apertura. */
    @FXML private VBox root;

    /** Campo di testo per filtrare per nome del ristorante. */
    @FXML private TextField nomeField;
    /** Campo di testo per filtrare per città. */
    @FXML private TextField cittaField;
    /** Campo di testo per filtrare per tipo di cucina. */
    @FXML private TextField tipoCucinaField;
    /** Campo di testo per filtrare per fascia di prezzo (1-4). */
    @FXML private TextField fasciaPrezzoField;
    /** Campo di testo per il raggio, in km, della ricerca "vicino a me". */
    @FXML private TextField raggioKmField;
    /** Filtro per i ristoranti che offrono prenotazione online. */
    @FXML private CheckBox prenotazioneOnlineCheck;
    /** Filtro per i ristoranti che offrono consegna a domicilio. */
    @FXML private CheckBox consegnaADomicilioCheck;

    /** Link "Vai al Login", visibile solo da guest. */
    @FXML private Hyperlink loginLink;
    /** Link "Logout", visibile solo da utente autenticato. */
    @FXML private Hyperlink logoutLink;
    /** Link "Preferiti", nascosto da guest (decisione 24: filtro cosmetico, non sostituisce il controllo lato server). */
    @FXML private Hyperlink preferitiLink;
    /** Link "Dashboard", nascosto da guest (decisione 24: filtro cosmetico, non sostituisce il controllo lato server). */
    @FXML private Hyperlink dashboardLink;

    /** Invia al server i comandi di ricerca dei ristoranti. */
    private final RistoranteService ristoranteService = new RistoranteService();
    /** Invia al server i comandi di autenticazione, usato qui per il logout. */
    private final AuthService authService = new AuthService();

    /**
     * Toglie il focus dal primo campo di testo (altrimenti JavaFX lo assegna
     * automaticamente all'apertura della schermata) e mostra solo i link
     * coerenti con stato di autenticazione e ruolo: "Vai al Login" da guest,
     * "Logout" da chiunque sia autenticato, "Preferiti" solo da Cliente
     * (RF08/RF09, non esistono preferiti per un Ristoratore) e "Dashboard"
     * solo da Ristoratore (non ha senso per un Cliente).
     */
    @FXML private void initialize() {
        Platform.runLater(() -> root.requestFocus());

        UtenteDTO utente = ServerConnection.getInstance().getUtenteCorrente();
        boolean ospite = utente == null;
        boolean cliente = utente != null && utente.getRuolo() == Ruolo.CLIENTE;
        boolean ristoratore = utente != null && utente.getRuolo() == Ruolo.RISTORATORE;

        loginLink.setVisible(ospite);
        loginLink.setManaged(ospite);
        logoutLink.setVisible(!ospite);
        logoutLink.setManaged(!ospite);
        preferitiLink.setVisible(cliente);
        preferitiLink.setManaged(cliente);
        dashboardLink.setVisible(ristoratore);
        dashboardLink.setManaged(ristoratore);
    }

    /**
     * Cerca i ristoranti che rispettano i filtri del form e mostra i
     * risultati nella schermata dei risultati.
     *
     * @param event l'evento generato dal click sul bottone di ricerca
     */
    @FXML private void handleCerca(ActionEvent event) {
        String nome = nomeField.getText();
        String citta = cittaField.getText();
        String tipoCucina = tipoCucinaField.getText();
        int fasciaPrezzo;
        try {
            fasciaPrezzo = fasciaPrezzoField.getText().isBlank() ? 0 : Integer.parseInt(fasciaPrezzoField.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Fascia di prezzo non valida, inserisci un numero valido").showAndWait();
            return;
        }
        boolean prenotazioneOnline = prenotazioneOnlineCheck.isSelected();
        boolean consegnaADomicilio = consegnaADomicilioCheck.isSelected();

        CercaRistorantiDTO filtri = new CercaRistorantiDTO(
            nome, citta, tipoCucina, prenotazioneOnline, fasciaPrezzo, consegnaADomicilio, null
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        TaskRunner.run(
        () -> ristoranteService.cercaRistoranti(filtri),
        cercaResult -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/risultati.fxml"));
                Parent root = loader.load();
                RisultatiController controller = loader.getController();
                controller.impostaRisultati(cercaResult);
                stage.getScene().setRoot(root);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
            }
        }
        );
    }

    /**
     * Cerca i ristoranti entro il raggio indicato dal luogo di riferimento
     * (decisione 14): {@code cittaField} se compilato, altrimenti il
     * domicilio dell'utente loggato. Se nessuno dei due è disponibile (guest
     * senza aver scritto una città) mostra subito un avviso, invece di far
     * arrivare l'errore dal server.
     *
     * @param event l'evento generato dal click sul bottone "Vicino a me"
     */
    @FXML private void handleVicinoAMe(ActionEvent event) {
        double raggioKm;
        try {
            raggioKm = Double.parseDouble(raggioKmField.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Raggio non valido, inserisci un numero valido").showAndWait();
            return;
        }

        String luogo = cittaField.getText();
        if (luogo == null || luogo.isBlank()) {
            UtenteDTO utente = ServerConnection.getInstance().getUtenteCorrente();
            luogo = utente != null ? utente.getDomicilio() : null;
        }
        if (luogo == null || luogo.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Inserisci una città o effettua il login").showAndWait();
            return;
        }

        CercaVicinoDTO filtri = new CercaVicinoDTO(raggioKm, luogo);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        TaskRunner.run(
        () -> ristoranteService.cercaVicino(filtri),
        cercaResult -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/risultati.fxml"));
                Parent root = loader.load();
                RisultatiController controller = loader.getController();
                controller.impostaRisultati(cercaResult);
                stage.getScene().setRoot(root);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
            }
        }
        );
    }

    /**
     * Naviga alla schermata di login.
     *
     * @param event l'evento generato dal click sul link di login
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleLogin(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/login.fxml"));
        stage.getScene().setRoot(root);
    }

    /**
     * Invalida la sessione sul server e naviga alla schermata iniziale.
     *
     * @param event l'evento generato dal click sul link di logout
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleLogout(ActionEvent event) throws IOException {
        TaskRunner.run(
        () -> { authService.logout();
                return null;
            },
        _void -> {
            try{
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/splash.fxml"));
                stage.getScene().setRoot(root);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
            }
        }
        );
    }

    /**
     * Naviga alla schermata dei preferiti.
     *
     * @param event l'evento generato dal click sul link "Preferiti"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handlePreferiti(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/preferiti.fxml"));
        stage.getScene().setRoot(root);
    }

    /**
     * Naviga alla dashboard del ristoratore.
     *
     * @param event l'evento generato dal click sul link "Dashboard"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleDashboard(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.getScene().setRoot(root);
    }
}
