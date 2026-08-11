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
import javafx.stage.Stage;
import theknife.client.service.RistoranteService;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.RistoranteDTO;


/**
 * Controller della schermata Preferti (S08).
 *
 * @author Barlera Marco, 760000, VA
 */

public class PreferitiController {

    @FXML private Label preferitiLabel;
    @FXML private ListView<RistoranteDTO> preferitiListView;
    @FXML private Button tornaIndietroButton;
    @FXML private Button rimuoviPreferitoButton;
    @FXML private Button vediDettaglioButton;

    private final RistoranteService ristoranteService = new RistoranteService();

    private void caricaPreferiti() {
        TaskRunner.run(
            () -> ristoranteService.ottieniPreferiti(),
            ristoranti -> {
                preferitiListView.getItems().setAll(ristoranti);
                preferitiListView.setCellFactory(lv -> new ListCell<RistoranteDTO>() {
                    @Override
                    protected void updateItem(RistoranteDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getNome() + " - " + item.getCitta() + " - " + item.getTipoCucina());
                        }
                    }
                });
            }
        );
    }

    @FXML private void initialize() {
        caricaPreferiti();
    }

    @FXML private void handleTornaIndietro(ActionEvent event) throws IOException {
        System.out.println("Torna indietro cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    @FXML private void handleRimuoviPreferito(ActionEvent event) throws IOException {
        System.out.println("Rimuovi preferito cliccato");

        RistoranteDTO selectedRistorante = preferitiListView.getSelectionModel().getSelectedItem();
        if (selectedRistorante != null) {
            TaskRunner.run(
                () -> { ristoranteService.rimuoviPreferito(new IdRistoranteDTO(selectedRistorante.getIdRistorante()));
                        return null;
                    },
                result -> {
                    caricaPreferiti();
                    new Alert(Alert.AlertType.INFORMATION, "Ristorante rimosso dai preferiti").showAndWait();
                }
            );
        }
        else {
            new Alert(Alert.AlertType.ERROR, "Nessun ristorante selezionato").showAndWait();
        }
    }

    @FXML private void handleVediDettaglio(ActionEvent event) {
        System.out.println("Dettaglio cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        RistoranteDTO selectedRistorante = preferitiListView.getSelectionModel().getSelectedItem();
        if (selectedRistorante != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
                Parent root = loader.load();
                DettaglioController controller = loader.getController();
                controller.impostaRistorante(selectedRistorante.getIdRistorante());
                stage.setScene(new Scene(root, 800, 600));
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
            }
        }
        else {
            new Alert(Alert.AlertType.ERROR, "Nessun ristorante selezionato").showAndWait();
        }
    }
}
