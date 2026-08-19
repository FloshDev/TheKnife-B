package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import theknife.client.network.ServerConnection;
import theknife.client.service.AuthService;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.Ruolo;

/**
 * Controller della sidebar di navigazione, inclusa (fx:include) in ogni
 * schermata del client. Mostra solo le voci coerenti con ruolo e stato di
 * autenticazione (decisione 24: filtro cosmetico, non sostituisce il
 * controllo lato server) e centralizza la navigazione globale
 * (Ricerca/Preferiti/Dashboard/Gestisci recensioni/Associati/Aggiungi
 * ristorante/Accedi/Esci), prima duplicata in ogni controller di schermata.
 *
 * @author Barlera Marco, 760000, VA
 */
public class SidebarController {

    /** Voce di navigazione, usata da {@link #impostaAttivo} per evidenziare la schermata corrente. */
    public enum Voce { RICERCA, PREFERITI, DASHBOARD, GESTIONE_RECENSIONI, ASSOCIA, AGGIUNGI }

    /** Link "Ricerca", porta a Home. */
    @FXML private Hyperlink ricercaItem;
    /** Link "Preferiti", visibile solo da Cliente. */
    @FXML private Hyperlink preferitiItem;
    /** Link "Dashboard", visibile solo da Ristoratore. */
    @FXML private Hyperlink dashboardItem;
    /** Link "Gestisci recensioni", visibile solo da Ristoratore. */
    @FXML private Hyperlink gestisciRecensioniItem;
    /** Link "Associati a un ristorante", visibile solo da Ristoratore. */
    @FXML private Hyperlink associaRistoranteItem;
    /** Link "Aggiungi ristorante", visibile solo da Ristoratore. */
    @FXML private Hyperlink aggiungiRistoranteItem;

    /** Blocco avatar/username/ruolo, visibile solo da utente autenticato. */
    @FXML private HBox userBlock;
    /** Cerchio con l'iniziale del ruolo ("C"/"R"). */
    @FXML private Label avatarLabel;
    /** Username dell'utente corrente. */
    @FXML private Label usernameLabel;
    /** Ruolo dell'utente corrente. */
    @FXML private Label ruoloLabel;
    /** Link "Accedi", visibile solo da guest. */
    @FXML private Hyperlink accediLink;
    /** Link "Esci", visibile solo da utente autenticato. */
    @FXML private Hyperlink logoutLink;

    /** Invia al server il comando di logout. */
    private final AuthService authService = new AuthService();

    /**
     * Mostra solo le voci coerenti con ruolo/stato di autenticazione e
     * precompila avatar/username/ruolo se autenticato. "Ricerca" è nascosta
     * al Ristoratore (DIRETTIVE_PROGETTO.md assegna la ricerca ristoranti
     * solo ai Clienti), non a guest e Cliente.
     */
    @FXML private void initialize() {
        UtenteDTO utente = ServerConnection.getInstance().getUtenteCorrente();
        boolean ospite = utente == null;
        boolean ristoratore = utente != null && utente.getRuolo() == Ruolo.RISTORATORE;
        boolean cliente = utente != null && utente.getRuolo() == Ruolo.CLIENTE;

        ricercaItem.setVisible(!ristoratore);
        ricercaItem.setManaged(!ristoratore);
        preferitiItem.setVisible(cliente);
        preferitiItem.setManaged(cliente);
        dashboardItem.setVisible(ristoratore);
        dashboardItem.setManaged(ristoratore);
        gestisciRecensioniItem.setVisible(ristoratore);
        gestisciRecensioniItem.setManaged(ristoratore);
        associaRistoranteItem.setVisible(ristoratore);
        associaRistoranteItem.setManaged(ristoratore);
        aggiungiRistoranteItem.setVisible(ristoratore);
        aggiungiRistoranteItem.setManaged(ristoratore);

        userBlock.setVisible(!ospite);
        userBlock.setManaged(!ospite);
        logoutLink.setVisible(!ospite);
        logoutLink.setManaged(!ospite);
        accediLink.setVisible(ospite);
        accediLink.setManaged(ospite);

        if (!ospite) {
            avatarLabel.setText(ristoratore ? "R" : "C");
            usernameLabel.setText(utente.getUsername());
            ruoloLabel.setText(utente.getRuolo().toString());
        }
    }

    /**
     * Evidenzia la voce di navigazione corrispondente alla schermata attiva.
     *
     * @param voce la voce da evidenziare
     */
    public void impostaAttivo(Voce voce) {
        ricercaItem.getStyleClass().remove("attivo");
        preferitiItem.getStyleClass().remove("attivo");
        dashboardItem.getStyleClass().remove("attivo");
        gestisciRecensioniItem.getStyleClass().remove("attivo");
        associaRistoranteItem.getStyleClass().remove("attivo");
        aggiungiRistoranteItem.getStyleClass().remove("attivo");
        switch (voce) {
            case RICERCA -> ricercaItem.getStyleClass().add("attivo");
            case PREFERITI -> preferitiItem.getStyleClass().add("attivo");
            case DASHBOARD -> dashboardItem.getStyleClass().add("attivo");
            case GESTIONE_RECENSIONI -> gestisciRecensioniItem.getStyleClass().add("attivo");
            case ASSOCIA -> associaRistoranteItem.getStyleClass().add("attivo");
            case AGGIUNGI -> aggiungiRistoranteItem.getStyleClass().add("attivo");
        }
    }

    /**
     * Naviga alla schermata Home.
     *
     * @param event l'evento generato dal click sul link "Ricerca"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleRicerca(ActionEvent event) throws IOException {
        naviga(event, "/theknife/client/ui/home.fxml");
    }

    /**
     * Naviga alla schermata dei preferiti.
     *
     * @param event l'evento generato dal click sul link "Preferiti"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handlePreferiti(ActionEvent event) throws IOException {
        naviga(event, "/theknife/client/ui/preferiti.fxml");
    }

    /**
     * Naviga alla dashboard del ristoratore.
     *
     * @param event l'evento generato dal click sul link "Dashboard"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleDashboard(ActionEvent event) throws IOException {
        naviga(event, "/theknife/client/ui/dashboard.fxml");
    }

    /**
     * Naviga alla gestione delle recensioni dei ristoranti gestiti.
     *
     * @param event l'evento generato dal click sul link "Gestisci recensioni"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleGestisciRecensioni(ActionEvent event) throws IOException {
        naviga(event, "/theknife/client/ui/gestioneRecensione.fxml");
    }

    /**
     * Naviga alla schermata per associarsi a un ristorante esistente.
     *
     * @param event l'evento generato dal click sul link "Associati a un ristorante"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleAssociaRistorante(ActionEvent event) throws IOException {
        naviga(event, "/theknife/client/ui/associaRistorante.fxml");
    }

    /**
     * Naviga alla schermata di inserimento di un nuovo ristorante.
     *
     * @param event l'evento generato dal click sul link "Aggiungi ristorante"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleAggiungiRistorante(ActionEvent event) throws IOException {
        naviga(event, "/theknife/client/ui/aggiungiRistorante.fxml");
    }

    /**
     * Naviga alla schermata di login.
     *
     * @param event l'evento generato dal click sul link "Accedi"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleLogin(ActionEvent event) throws IOException {
        naviga(event, "/theknife/client/ui/login.fxml");
    }

    /**
     * Invalida la sessione sul server e naviga alla schermata iniziale.
     *
     * @param event l'evento generato dal click sul link "Esci"
     */
    @FXML private void handleLogout(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        TaskRunner.run(
        () -> { authService.logout();
                return null;
            },
        _void -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/splash.fxml"));
                stage.getScene().setRoot(root);
            } catch (IOException e) {
                Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
            }
        }
        );
    }

    /**
     * Carica una schermata e la imposta come radice della finestra corrente.
     *
     * @param event l'evento da cui risalire alla finestra corrente
     * @param fxml il percorso della risorsa FXML da caricare
     * @throws IOException se il caricamento della schermata fallisce
     */
    private void naviga(ActionEvent event, String fxml) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage.getScene().setRoot(root);
    }
}
