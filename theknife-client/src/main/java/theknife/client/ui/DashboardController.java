package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import theknife.client.service.AuthService;
import theknife.client.service.RistoranteService;
import theknife.common.dto.RistoranteDTO;


/**
 * Controller della schermata dashboard ristoratore (S09).
 *
 * @author Barlera Marco, 760000, VA
 */
public class DashboardController {
    /** Titolo della schermata dashboard. */
    @FXML private Label dashboardLabel;
    /** Lista dei ristoranti gestiti dall'utente ristoratore. */
    @FXML private ListView<RistoranteDTO> ristorantiGestiti;
    /** Bottone per aprire il dettaglio del ristorante selezionato. */
    @FXML private Button vediDettaglioButton;
    /** Bottone per aggiungere un nuovo ristorante. */
    @FXML private Button aggiungiNuovoRistoranteButton;
    /** Bottone per associarsi a un ristorante esistente. */
    @FXML private Button associaRistoranteButton;
    /** Bottone per aprire la gestione delle recensioni. */
    @FXML private Button gestisciRecensioniButton;
    /** Bottone per effettuare il logout. */
    @FXML private Button logoutButton;

    /** Invia al server i comandi sui ristoranti gestiti dall'utente. */
    private final RistoranteService ristoranteService = new RistoranteService();
    /** Invia al server i comandi di autenticazione, usato qui per il logout. */
    private final AuthService authService = new AuthService();

    /**
     * Carica i ristoranti gestiti dall'utente ristoratore all'apertura della
     * schermata e imposta la cella custom della lista (nome in grassetto,
     * città/cucina sotto, invece del {@code toString()} grezzo del DTO).
     */
    @FXML private void initialize() {
        TaskRunner.run(
            () -> ristoranteService.vediRistorantiGestiti(),
            ristoranti -> {
                ristorantiGestiti.getItems().setAll(ristoranti);
                ristorantiGestiti.setCellFactory(lv -> new ListCell<RistoranteDTO>() {
                    private final Label nomeLabel = new Label();
                    private final Label infoLabel = new Label();
                    private final VBox contenuto = new VBox(nomeLabel, infoLabel);
                    {
                        nomeLabel.getStyleClass().add("risultato-nome");
                        infoLabel.getStyleClass().add("risultato-info");
                    }

                    @Override
                    protected void updateItem(RistoranteDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            nomeLabel.setText(item.getNome());
                            infoLabel.setText(item.getCitta() + " · " + item.getTipoCucina());
                            setGraphic(contenuto);
                        }
                    }
                });
            }
        );
    }

    /**
     * Naviga al dettaglio del ristorante selezionato nella lista. Mostra un
     * avviso se nessun elemento è selezionato.
     *
     * @param event l'evento generato dal click sul bottone "Vedi dettaglio"
     */
    @FXML private void handleVediDettaglio(ActionEvent event) {
        RistoranteDTO selezionato = ristorantiGestiti.getSelectionModel().getSelectedItem();
        if (selezionato == null) {
            new Alert(Alert.AlertType.ERROR, "Nessun ristorante selezionato").showAndWait();
            return;
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
            Parent root = loader.load();
            DettaglioController controller = loader.getController();
            controller.impostaRistorante(selezionato.getIdRistorante());
            controller.impostaProvenienzaDashboard();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Naviga alla schermata di inserimento di un nuovo ristorante.
     *
     * @param event l'evento generato dal click sul bottone corrispondente
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleAggiungiNuovoRistorante(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/aggiungiRistorante.fxml"));
        stage.getScene().setRoot(root);
    }

    /**
     * Naviga alla schermata per associarsi a un ristorante esistente.
     *
     * @param event l'evento generato dal click sul bottone corrispondente
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleAssociaRistorante(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/associaRistorante.fxml"));
        stage.getScene().setRoot(root);
    }

    /**
     * Naviga alla schermata di gestione delle recensioni dei ristoranti
     * gestiti.
     *
     * @param event l'evento generato dal click sul bottone corrispondente
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleGestisciRecensioni(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/gestioneRecensione.fxml"));
        stage.getScene().setRoot(root);
    }

    /**
     * Invalida la sessione sul server e naviga alla schermata iniziale.
     *
     * @param event l'evento generato dal click sul bottone "Logout"
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
}
