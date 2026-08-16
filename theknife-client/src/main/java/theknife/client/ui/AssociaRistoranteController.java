package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import theknife.client.service.RistoranteService;
import theknife.common.dto.CercaRistorantiDTO;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.RistoranteDTO;


/**
 * Controller della schermata associa ristorante (S11).
 *
 * @author Barlera Marco, 760000, VA
 */

public class AssociaRistoranteController {
    /** Campo di testo per la ricerca del ristorante per nome. */
    @FXML private TextField ricercaField;
    /** Lista dei ristoranti trovati dalla ricerca. */
    @FXML private ListView<RistoranteDTO> risultatiListView;
    /** Bottone per avviare la ricerca. */
    @FXML private Button cercaButton;
    /** Bottone per associarsi al ristorante selezionato. */
    @FXML private Button associaButton;
    /** Bottone per tornare alla dashboard senza associare nulla. */
    @FXML private Button annullaButton;

    /** Invia al server i comandi sui ristoranti, incluso l'associazione al gestore. */
    private final RistoranteService ristoranteService = new RistoranteService();

    /**
     * Cerca i ristoranti per nome e mostra i risultati nella lista. Solo il
     * nome è supportato: nessun comando del protocollo consente la ricerca
     * per indirizzo, benché fosse promessa dal placeholder originale del
     * campo di ricerca.
     */
    @FXML private void handleCerca() {
        CercaRistorantiDTO cercaRistoranteDTO = new CercaRistorantiDTO(ricercaField.getText(), null, null, null, 0, null, null);

        TaskRunner.run(
            () -> ristoranteService.cercaRistoranti(cercaRistoranteDTO),
            ristoranti -> {
                risultatiListView.getItems().setAll(ristoranti);
                risultatiListView.setCellFactory(lv -> new ListCell<RistoranteDTO>() {
                    @Override
                    protected void updateItem(RistoranteDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getNome() + " - " + item.getCitta());
                        }
                    }
                });
            }
        );
    }
    /**
     * Associa l'utente come gestore del ristorante selezionato nella lista e
     * torna alla dashboard. Mostra un avviso se nessun elemento è
     * selezionato.
     *
     * @param event l'evento generato dal click sul bottone "Associa"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleAssocia(ActionEvent event) throws IOException {
        RistoranteDTO selectedRistorante = risultatiListView.getSelectionModel().getSelectedItem();
        if (selectedRistorante != null) {
            TaskRunner.run(
                () -> { ristoranteService.associaRistorante(new IdRistoranteDTO(selectedRistorante.getIdRistorante()));
                        return null;
                    },
                result -> {
                    new Alert(Alert.AlertType.INFORMATION, "Ristorante associato con successo").showAndWait();
                    try {
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
                        stage.getScene().setRoot(root);
                    } catch (IOException e) {
                        new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
                    }
                }
            );
        } else {
            new Alert(Alert.AlertType.WARNING, "Seleziona un ristorante dalla lista").showAndWait();
        }
    }
    /**
     * Torna alla dashboard senza associare nulla.
     *
     * @param event l'evento generato dal click sul bottone "Annulla"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleAnnulla(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.getScene().setRoot(root);
    }

}
