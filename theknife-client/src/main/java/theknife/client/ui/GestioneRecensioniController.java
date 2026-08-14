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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import theknife.client.service.RecensioneService;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.RispondiRecensioneDTO;

/**
 * Controller della schermata di gestione recensione (S12).
 *
 * @author Barlera Marco, 760000, VA
 */

public class GestioneRecensioniController {
    /** Nome del ristorante a cui appartiene la recensione selezionata. */
    @FXML private Label nomeRistoranteLabel;
    /** Lista delle recensioni di tutti i ristoranti gestiti. */
    @FXML private ListView<RecensioneDTO> recensioniListView;
    /** Area di testo per la risposta alla recensione selezionata. */
    @FXML private TextArea rispostaField;
    /** Bottone per inviare la risposta. */
    @FXML private Button rispondiButton;
    /** Bottone per tornare alla dashboard. */
    @FXML private Button tornaIndietroButton;

    private final RecensioneService recensioneService = new RecensioneService();

    /**
     * Carica dal server le recensioni di tutti i ristoranti gestiti
     * dall'utente e le mostra, sostituendo il contenuto attuale della lista.
     */
    private void caricaRecensioni() {
        TaskRunner.run(
            () -> recensioneService.leggiRecensioniRistorantiGestiti(),
            recensioni -> {
                recensioniListView.getItems().setAll(recensioni);
                recensioniListView.setCellFactory(lv -> new ListCell<RecensioneDTO>() {
                    @Override
                    protected void updateItem(RecensioneDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            String base = item.getTitolo() + " - " + item.getStelle() + "★ (ristorante #" + item.getIdRistorante() + ")";
                            setText(item.getRisposta() == null ? base : base + " [risposto]");
                        }
                    }
                });
            }
        );
    }

    /**
     * Carica le recensioni all'apertura della schermata.
     */
    @FXML private void initialize() {
        caricaRecensioni();
    }

    /**
     * Invia la risposta scritta alla recensione selezionata nella lista e
     * ricarica l'elenco. Mostra un avviso se nessun elemento è selezionato.
     */
    @FXML private void handleRispondi() {
        RecensioneDTO selectedRecensione = recensioniListView.getSelectionModel().getSelectedItem();
        if (selectedRecensione != null) {
            String risposta = rispostaField.getText();
            TaskRunner.run(
                () -> { RispondiRecensioneDTO dto = new RispondiRecensioneDTO(selectedRecensione.getIdRecensione(), risposta);
                        recensioneService.rispondiRecensione(dto);
                        return null;
                    },
                result -> {
                    new Alert(Alert.AlertType.INFORMATION, "Risposta inviata con successo").showAndWait();
                    rispostaField.clear();
                    caricaRecensioni();
                }
            );
        }
        else {
            new Alert(Alert.AlertType.ERROR, "Nessuna recensione selezionata").showAndWait();
        }
    }

    /**
     * Naviga alla dashboard.
     *
     * @param event l'evento generato dal click sul bottone "Torna indietro"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleTornaIndietro(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
