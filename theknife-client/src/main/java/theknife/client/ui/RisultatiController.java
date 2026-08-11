package theknife.client.ui;

import java.io.IOException;
import java.util.List;

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
import theknife.common.dto.RistoranteDTO;


/**
 * Controller della schermata risultati (S05).
 *
 * @author Barlera Marco, 760000, VA
 */

public class RisultatiController {
    /** Titolo della schermata dei risultati. */
    @FXML private Label risultatiLabel;
    /** Lista dei ristoranti trovati dalla ricerca. */
    @FXML private ListView<RistoranteDTO> risultatiListView;
    /** Bottone per aprire il dettaglio del ristorante selezionato. */
    @FXML private Button dettaglioButton;
    /** Bottone per tornare alla schermata Home. */
    @FXML private Button tornaIndietroButton;
    
    /**
     * Naviga al dettaglio del ristorante selezionato nella lista. Mostra un
     * avviso se nessun elemento è selezionato.
     *
     * @param event l'evento generato dal click sul bottone "Vedi dettaglio"
     */
    @FXML private void handleDettaglio(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        RistoranteDTO selectedRistorante = risultatiListView.getSelectionModel().getSelectedItem();
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
    
    /**
     * Naviga alla schermata Home.
     *
     * @param event l'evento generato dal click sul bottone "Torna indietro"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleTornaIndietro(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    /**
     * Popola la lista dei risultati con i ristoranti ricevuti e imposta il
     * cell factory per mostrarli in forma leggibile (nome, città, cucina)
     * invece del {@code toString()} grezzo del DTO.
     *
     * @param risultati i ristoranti da mostrare
     */
    public void impostaRisultati(List<RistoranteDTO> risultati) {
        risultatiListView.getItems().setAll(risultati);

        risultatiListView.setCellFactory(lv -> new ListCell<RistoranteDTO>() {
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
    
}
