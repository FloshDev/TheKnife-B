package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
    /** Lista dei ristoranti trovati dalla ricerca, una card orizzontale per ristorante. */
    @FXML private ListView<RistoranteDTO> risultatiListView;
    /** Bottone per avviare la ricerca. */
    @FXML private Button cercaButton;
    /** Bottone icona per tornare alla dashboard senza associare nulla. */
    @FXML private Button annullaButton;
    /** Controller della sidebar inclusa (fx:include), per evidenziare "Associati a un ristorante" come voce attiva. */
    @FXML private SidebarController sidebarController;

    /** Invia al server i comandi sui ristoranti, incluso l'associazione al gestore. */
    private final RistoranteService ristoranteService = new RistoranteService();

    /**
     * Evidenzia "Associati a un ristorante" nella sidebar.
     */
    @FXML private void initialize() {
        sidebarController.impostaAttivo(SidebarController.Voce.ASSOCIA);
    }

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
                Label nessunRisultato = new Label("Nessun ristorante trovato con questo nome.");
                nessunRisultato.getStyleClass().add("risultato-info");
                risultatiListView.setPlaceholder(nessunRisultato);
                risultatiListView.getItems().setAll(ristoranti);
                risultatiListView.setCellFactory(lv -> new RistoranteTrovatoCell());
            }
        );
    }

    /**
     * Associa l'utente come gestore del ristorante indicato e torna alla
     * dashboard.
     *
     * @param ristorante il ristorante a cui associarsi
     */
    private void associa(RistoranteDTO ristorante) {
        TaskRunner.run(
            () -> { ristoranteService.associaRistorante(new IdRistoranteDTO(ristorante.getIdRistorante()));
                    return null;
                },
            result -> {
                Toast.successo("Ristorante associato con successo");
                try {
                    Stage stage = (Stage) risultatiListView.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
                    stage.getScene().setRoot(root);
                } catch (IOException e) {
                    Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
                }
            }
        );
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

    /**
     * Cella della lista risultati: card orizzontale con nome e città a
     * sinistra, bottone "Associati" a destra.
     */
    private class RistoranteTrovatoCell extends ListCell<RistoranteDTO> {
        private final Label nomeLabel = new Label();
        private final Label infoLabel = new Label();
        private final VBox testi = new VBox(4, nomeLabel, infoLabel);
        private final Button associatiButton = new Button("Associati");
        private final HBox contenuto = new HBox(12, testi, associatiButton);

        {
            contenuto.getStyleClass().add("risultato-card");
            contenuto.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(testi, Priority.ALWAYS);
            nomeLabel.getStyleClass().add("risultato-nome");
            infoLabel.getStyleClass().add("risultato-info");
            infoLabel.setWrapText(true);
            associatiButton.getStyleClass().add("bottone-secondario");
            associatiButton.setOnAction(e -> associa(getItem()));
        }

        @Override
        protected void updateItem(RistoranteDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                nomeLabel.setText(item.getNome());
                infoLabel.setText(item.getCitta());
                setGraphic(contenuto);
            }
        }
    }
}
