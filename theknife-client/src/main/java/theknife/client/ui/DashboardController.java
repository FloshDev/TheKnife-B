package theknife.client.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import theknife.client.service.RistoranteService;
import theknife.common.dto.RistoranteDTO;

/**
 * Controller della schermata dashboard ristoratore (S09).
 *
 * @author Barlera Marco, 760000, VA
 */
public class DashboardController {
    /** Lista dei ristoranti gestiti dall'utente ristoratore, una card orizzontale per ristorante. */
    @FXML private ListView<RistoranteDTO> ristorantiGestiti;
    /** Controller della sidebar inclusa (fx:include), per evidenziare "Dashboard" come voce attiva. */
    @FXML private SidebarController sidebarController;

    /** Invia al server i comandi sui ristoranti gestiti dall'utente. */
    private final RistoranteService ristoranteService = new RistoranteService();

    /**
     * Evidenzia "Dashboard" nella sidebar e carica i ristoranti gestiti
     * dall'utente ristoratore all'apertura della schermata.
     */
    @FXML private void initialize() {
        sidebarController.impostaAttivo(SidebarController.Voce.DASHBOARD);
        Label nessunRistorante = new Label("Non gestisci ancora nessun ristorante.");
        nessunRistorante.getStyleClass().add("risultato-info");
        ristorantiGestiti.setPlaceholder(nessunRistorante);
        ristorantiGestiti.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                apriDettaglio((Stage) ristorantiGestiti.getScene().getWindow(), ristorantiGestiti.getSelectionModel().getSelectedItem());
            }
        });
        TaskRunner.run(
            () -> ristoranteService.vediRistorantiGestiti(),
            ristoranti -> {
                ristorantiGestiti.getItems().setAll(ristoranti);
                ristorantiGestiti.setCellFactory(lv -> new RistoranteGestitoCell());
            }
        );
    }

    /**
     * Apre il dettaglio del ristorante indicato, chiamato sia dalla freccia
     * su una card che dal doppio click su una riga. Mostra un avviso se
     * nessun ristorante è indicato (lista vuota, doppio click a vuoto).
     *
     * @param stage la finestra su cui sostituire la schermata
     * @param ristorante il ristorante di cui aprire il dettaglio
     */
    private void apriDettaglio(Stage stage, RistoranteDTO ristorante) {
        if (ristorante == null) {
            Toast.errore("Nessun ristorante selezionato");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
            Parent root = loader.load();
            DettaglioController controller = loader.getController();
            controller.impostaRistorante(ristorante.getIdRistorante());
            controller.impostaProvenienzaDashboard();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
        }
    }

    /**
     * Cella della lista ristoranti gestiti: card orizzontale con nome,
     * indirizzo e valutazione a sinistra, freccia per il dettaglio a destra.
     */
    private class RistoranteGestitoCell extends ListCell<RistoranteDTO> {
        private final Label nomeLabel = new Label();
        private final Label indirizzoLabel = new Label();
        private final Label ratingLabel = new Label();
        private final VBox testi = new VBox(4, nomeLabel, indirizzoLabel, ratingLabel);
        private final SVGPath frecciaIcon = new SVGPath();
        private final Button frecciaButton = new Button();
        private final HBox contenuto = new HBox(12, testi, frecciaButton);

        {
            contenuto.getStyleClass().add("risultato-card");
            contenuto.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(testi, Priority.ALWAYS);
            nomeLabel.getStyleClass().add("risultato-nome");
            indirizzoLabel.getStyleClass().add("risultato-info");
            ratingLabel.getStyleClass().add("badge-rating");

            frecciaIcon.setContent("M9 18l6-6-6-6");
            frecciaIcon.setFill(Color.TRANSPARENT);
            frecciaIcon.setStroke(Color.web("#FA812F"));
            frecciaIcon.setStrokeWidth(2);
            frecciaButton.setGraphic(frecciaIcon);
            frecciaButton.getStyleClass().add("bottone-icona");
            frecciaButton.setOnAction(e -> apriDettaglio((Stage) frecciaButton.getScene().getWindow(), getItem()));
        }

        @Override
        protected void updateItem(RistoranteDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                nomeLabel.setText(item.getNome());
                indirizzoLabel.setText(item.getIndirizzo() + ", " + item.getCitta());
                ratingLabel.setText(String.format("★%.1f · %d recensioni", item.getMediaStelle(), item.getNumeroRecensioni()));
                setGraphic(contenuto);
            }
        }
    }
}
