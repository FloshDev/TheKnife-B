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
    @FXML private Label nomeRistoranteLabel;
    @FXML private ListView<RecensioneDTO> recensioniListView;
    @FXML private TextArea rispostaField;
    @FXML private Button rispondiButton;
    @FXML private Button tornaIndietroButton;

    private final RecensioneService recensioneService = new RecensioneService();

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
                            setText(item.getTitolo() + " - " + item.getStelle() + "★ (ristorante #" + item.getIdRistorante() + ")");
                        }
                    }
                });
            }
        );
    }

    @FXML private void initialize() {
        caricaRecensioni();
    }

    @FXML private void handleRispondi() {
        System.out.println("Rispondi");

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

    @FXML private void handleTornaIndietro(ActionEvent event) throws IOException {
        System.out.println("Torna indietro");
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
