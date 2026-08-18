package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
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
    /** La card, la cui larghezza è agganciata alla finestra (grafica responsive). */
    @FXML private VBox card;
    /** Lista delle recensioni di tutti i ristoranti gestiti. */
    @FXML private ListView<RecensioneDTO> recensioniListView;
    /** Area di testo per la risposta alla recensione selezionata. */
    @FXML private TextArea rispostaField;
    /** Bottone per inviare la risposta. */
    @FXML private Button rispondiButton;
    /** Bottone per tornare alla dashboard. */
    @FXML private Button tornaIndietroButton;

    /** Invia al server i comandi sulle recensioni dei ristoranti gestiti. */
    private final RecensioneService recensioneService = new RecensioneService();

    /**
     * Carica le recensioni all'apertura della schermata.
     */
    @FXML private void initialize() {
        Responsive.aggancia(card, 0.6, 480, 700);
        Label nessunaRecensione = new Label("Nessuna recensione da gestire per ora.");
        nessunaRecensione.getStyleClass().add("risultato-info");
        recensioniListView.setPlaceholder(nessunaRecensione);
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
                    Toast.successo("Risposta inviata con successo");
                    rispostaField.clear();
                    caricaRecensioni();
                }
            );
        }
        else {
            Toast.errore("Nessuna recensione selezionata");
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
        stage.getScene().setRoot(root);
    }

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
                    private final Label titoloLabel = new Label();
                    private final Label infoLabel = new Label();
                    private final VBox contenuto = new VBox(titoloLabel, infoLabel);
                    {
                        titoloLabel.getStyleClass().add("risultato-nome");
                        infoLabel.getStyleClass().add("risultato-info");
                    }

                    @Override
                    protected void updateItem(RecensioneDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            titoloLabel.setText(item.getStelle() + "★ " + item.getTitolo());
                            infoLabel.setText("Ristorante #" + item.getIdRistorante()
                                + (item.getRisposta() != null ? " · risposto" : " · da rispondere"));
                            setGraphic(contenuto);
                        }
                    }
                });
            }
        );
    }
}
