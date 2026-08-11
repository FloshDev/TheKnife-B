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
    @FXML private TextField ricercaField;
    @FXML private ListView<RistoranteDTO> risultatiListView;
    @FXML private Button cercaButton;
    @FXML private Button associaButton;
    @FXML private Button annullaButton;

    private final RistoranteService ristoranteService = new RistoranteService();

    @FXML private void handleCerca() {
        System.out.println("Cerca cliccato");

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
    @FXML private void handleAssocia(ActionEvent event) throws IOException {
        System.out.println("Associa cliccato");

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
                        stage.setScene(new Scene(root, 800, 600));
                    } catch (IOException e) {
                        new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
                    }
                }
            );
        } else {
            new Alert(Alert.AlertType.WARNING, "Seleziona un ristorante dalla lista").showAndWait();
        }
    }
    @FXML private void handleAnnulla(ActionEvent event) throws IOException {
        System.out.println("Annulla cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

}
