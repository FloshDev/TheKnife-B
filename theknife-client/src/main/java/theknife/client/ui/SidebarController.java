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
 * (Ricerca/Preferiti/Le mie recensioni/Dashboard/Gestisci recensioni/
 * Associati/Aggiungi ristorante/Accedi/Esci), prima duplicata in ogni
 * controller di schermata.
 *
 * @author Barlera Marco, 760000, VA
 */
public class SidebarController {
    /**
     * Costruttore vuoto: tutta l'inizializzazione avviene in {@code initialize()},
     * chiamato da FXMLLoader dopo l'injection dei campi {@code @FXML}.
     */
    public SidebarController() {
    }


    /** Voce di navigazione, usata da {@link #impostaAttivo} per evidenziare la schermata corrente. */
    public enum Voce {
        /** Ricerca ristoranti (Home). */
        RICERCA,
        /** Preferiti, visibile solo da Cliente. */
        PREFERITI,
        /** Vicino a me, visibile a Ospite e Cliente. */
        VICINO_A_ME,
        /** Le mie recensioni, visibile solo da Cliente. */
        MIE_RECENSIONI,
        /** Dashboard, visibile solo da Ristoratore. */
        DASHBOARD,
        /** Gestisci recensioni, visibile solo da Ristoratore. */
        GESTIONE_RECENSIONI,
        /** Associati a un ristorante, visibile solo da Ristoratore. */
        ASSOCIA,
        /** Aggiungi ristorante, visibile solo da Ristoratore. */
        AGGIUNGI,
        /** About, sempre visibile. */
        ABOUT
    }

    /** Link "Ricerca", porta a Home. */
    @FXML private Hyperlink ricercaItem;
    /** Link "Preferiti", visibile solo da Cliente. */
    @FXML private Hyperlink preferitiItem;
    /** Link "Le mie recensioni", visibile solo da Cliente. */
    @FXML private Hyperlink mieRecensioniItem;
    /** Link "Vicino a me", visibile a Ospite e Cliente (RF-03/RF-07), non a Ristoratore. */
    @FXML private Hyperlink vicinoAMeItem;
    /** Link "Dashboard", visibile solo da Ristoratore. */
    @FXML private Hyperlink dashboardItem;
    /** Link "Gestisci recensioni", visibile solo da Ristoratore. */
    @FXML private Hyperlink gestisciRecensioniItem;
    /** Link "Associati a un ristorante", visibile solo da Ristoratore. */
    @FXML private Hyperlink associaRistoranteItem;
    /** Link "Aggiungi ristorante", visibile solo da Ristoratore. */
    @FXML private Hyperlink aggiungiRistoranteItem;
    /** Link "About", sempre visibile (nessun ruolo richiesto). */
    @FXML private Hyperlink aboutItem;

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
     * Sostituisce, se impostata, la navigazione standard del link "Ricerca"
     * (va a Home vuota) — usata da Risultati per tornare indietro
     * preservando i filtri dell'ultima ricerca invece di far ripartire il
     * form vuoto.
     */
    private Runnable azioneRicerca;
    /** Stesso scopo di {@link #azioneRicerca}, per il link "Vicino a me". */
    private Runnable azioneVicinoAMe;

    /**
     * Mostra solo le voci coerenti con ruolo/stato di autenticazione e
     * precompila avatar/username/ruolo se autenticato. "Ricerca" è nascosta
     * al Ristoratore (la ricerca ristoranti è una funzionalità riservata
     * ai Clienti), non a guest e Cliente.
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
        mieRecensioniItem.setVisible(cliente);
        mieRecensioniItem.setManaged(cliente);
        vicinoAMeItem.setVisible(!ristoratore);
        vicinoAMeItem.setManaged(!ristoratore);
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
        mieRecensioniItem.getStyleClass().remove("attivo");
        vicinoAMeItem.getStyleClass().remove("attivo");
        dashboardItem.getStyleClass().remove("attivo");
        gestisciRecensioniItem.getStyleClass().remove("attivo");
        associaRistoranteItem.getStyleClass().remove("attivo");
        aggiungiRistoranteItem.getStyleClass().remove("attivo");
        aboutItem.getStyleClass().remove("attivo");
        switch (voce) {
            case RICERCA -> ricercaItem.getStyleClass().add("attivo");
            case PREFERITI -> preferitiItem.getStyleClass().add("attivo");
            case MIE_RECENSIONI -> mieRecensioniItem.getStyleClass().add("attivo");
            case VICINO_A_ME -> vicinoAMeItem.getStyleClass().add("attivo");
            case DASHBOARD -> dashboardItem.getStyleClass().add("attivo");
            case GESTIONE_RECENSIONI -> gestisciRecensioniItem.getStyleClass().add("attivo");
            case ASSOCIA -> associaRistoranteItem.getStyleClass().add("attivo");
            case AGGIUNGI -> aggiungiRistoranteItem.getStyleClass().add("attivo");
            case ABOUT -> aboutItem.getStyleClass().add("attivo");
        }
    }

    /**
     * Naviga alla schermata Home.
     *
     * @param event l'evento generato dal click sul link "Ricerca"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleRicerca(ActionEvent event) throws IOException {
        if (azioneRicerca != null) {
            azioneRicerca.run();
            return;
        }
        naviga(event, "/theknife/client/ui/home.fxml");
    }

    /**
     * Sostituisce l'azione standard del link "Ricerca" con una azione
     * personalizzata, o la ripristina se {@code azione} è {@code null}.
     *
     * @param azione l'azione da eseguire al click, o {@code null} per il comportamento standard
     */
    public void impostaAzioneRicerca(Runnable azione) {
        this.azioneRicerca = azione;
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
     * Naviga alla schermata delle recensioni scritte dall'utente.
     *
     * @param event l'evento generato dal click sul link "Le mie recensioni"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleMieRecensioni(ActionEvent event) throws IOException {
        naviga(event, "/theknife/client/ui/mieRecensioni.fxml");
    }

    /**
     * Naviga alla schermata "Vicino a me".
     *
     * @param event l'evento generato dal click sul link "Vicino a me"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleVicinoAMe(ActionEvent event) throws IOException {
        if (azioneVicinoAMe != null) {
            azioneVicinoAMe.run();
            return;
        }
        naviga(event, "/theknife/client/ui/vicinoAme.fxml");
    }

    /**
     * Sostituisce l'azione standard del link "Vicino a me" con una azione
     * personalizzata, o la ripristina se {@code azione} è {@code null}.
     *
     * @param azione l'azione da eseguire al click, o {@code null} per il comportamento standard
     */
    public void impostaAzioneVicinoAMe(Runnable azione) {
        this.azioneVicinoAMe = azione;
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
     * Naviga alla schermata About.
     *
     * @param event l'evento generato dal click sul link "About"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleAbout(ActionEvent event) throws IOException {
        naviga(event, "/theknife/client/ui/about.fxml");
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
     * Invalida la sessione sul server e naviga alla schermata iniziale. La
     * sessione locale viene comunque azzerata (vedi {@code AuthService.logout})
     * anche se il server non è raggiungibile, quindi si torna a Splash in
     * entrambi i casi — altrimenti un server irraggiungibile lascerebbe
     * l'utente bloccato, loggato localmente ma senza modo di uscire.
     *
     * @param event l'evento generato dal click sul link "Esci"
     */
    @FXML private void handleLogout(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        TaskRunner.run(
        () -> { authService.logout();
                return null;
            },
        _void -> vaiASplash(stage),
        errore -> {
            Toast.avviso("Sessione chiusa, ma il server non era raggiungibile.");
            vaiASplash(stage);
        }
        );
    }

    /**
     * Sostituisce la schermata corrente con Splash.
     *
     * @param stage la finestra su cui sostituire la schermata
     */
    private void vaiASplash(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/splash.fxml"));
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
        }
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
