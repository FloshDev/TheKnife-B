package theknife.client.ui;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import theknife.client.service.RecensioneService;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.RispondiRecensioneDTO;

/**
 * Controller della schermata di gestione recensione (S12).
 *
 * @author Barlera Marco, 760000, VA
 */

public class GestioneRecensioniController {
    /** Lista delle recensioni di tutti i ristoranti gestiti, con risposta inline per card. */
    @FXML private ListView<RecensioneDTO> recensioniListView;
    /** Bottone icona per tornare alla dashboard. */
    @FXML private Button tornaIndietroButton;
    /** Controller della sidebar inclusa (fx:include). */
    @FXML private SidebarController sidebarController;

    /** Invia al server i comandi sulle recensioni dei ristoranti gestiti. */
    private final RecensioneService recensioneService = new RecensioneService();
    /** Formato di visualizzazione della data delle recensioni (es. "12 ago 2026"). */
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("d MMM yyyy");

    /**
     * Carica le recensioni all'apertura della schermata.
     */
    @FXML private void initialize() {
        sidebarController.impostaAttivo(SidebarController.Voce.GESTIONE_RECENSIONI);
        Label nessunaRecensione = new Label("Nessuna recensione da gestire per ora.");
        nessunaRecensione.getStyleClass().add("risultato-info");
        recensioniListView.setPlaceholder(nessunaRecensione);
        caricaRecensioni();
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
     * Invia la risposta scritta a una recensione e ricarica l'elenco.
     *
     * @param recensione la recensione a cui rispondere
     * @param risposta il testo della risposta
     */
    private void rispondi(RecensioneDTO recensione, String risposta) {
        if (risposta == null || risposta.isBlank()) {
            Toast.avviso("Scrivi una risposta prima di inviare.");
            return;
        }
        TaskRunner.run(
            () -> { recensioneService.rispondiRecensione(new RispondiRecensioneDTO(recensione.getIdRecensione(), risposta));
                    return null;
                },
            result -> {
                Toast.successo("Risposta inviata con successo");
                caricaRecensioni();
            }
        );
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
                recensioniListView.setCellFactory(lv -> new RecensioneGestitaCell());
            }
        );
    }

    /**
     * Cella della lista recensioni: username dell'autore, data, ristorante,
     * titolo, stelle e testo (decisione 33), con un campo di risposta
     * inline al posto di una recensione selezionata + bottone condiviso —
     * mostrato solo se non c'è già una risposta.
     */
    private class RecensioneGestitaCell extends ListCell<RecensioneDTO> {
        private final Label usernameLabel = new Label();
        private final Label dataLabel = new Label();
        private final HBox intestazione = new HBox(usernameLabel, dataLabel);
        private final Label ristoranteLabel = new Label();
        private final Label titoloLabel = new Label();
        private final Label stelleLabel = new Label();
        private final Label testoLabel = new Label();
        private final Label rispostaLabel = new Label();
        private final TextField rispostaField = new TextField();
        private final Button inviaButton = new Button("Invia");
        private final HBox rispondiRow = new HBox(8, rispostaField, inviaButton);
        private final VBox contenuto = new VBox(6,
            intestazione, ristoranteLabel, titoloLabel, stelleLabel, testoLabel, rispostaLabel, rispondiRow);

        {
            contenuto.getStyleClass().add("recensione-riga");
            usernameLabel.getStyleClass().add("risultato-nome");
            usernameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(usernameLabel, Priority.ALWAYS);
            dataLabel.getStyleClass().add("recensione-data");
            ristoranteLabel.getStyleClass().add("risultato-info");
            titoloLabel.getStyleClass().add("dettaglio-info");
            stelleLabel.getStyleClass().add("recensione-stelle");
            testoLabel.getStyleClass().add("risultato-info");
            testoLabel.setWrapText(true);
            rispostaLabel.getStyleClass().add("recensione-risposta");
            rispostaLabel.setWrapText(true);
            rispostaField.getStyleClass().add("campo-testo");
            rispostaField.setPromptText("Scrivi una risposta…");
            HBox.setHgrow(rispostaField, Priority.ALWAYS);
            inviaButton.getStyleClass().add("bottone-piccolo");
            inviaButton.setOnAction(e -> rispondi(getItem(), rispostaField.getText()));
        }

        @Override
        protected void updateItem(RecensioneDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                usernameLabel.setText(item.getUsername());
                dataLabel.setText(item.getDataPubblicazione() == null ? "" : item.getDataPubblicazione().format(FORMATO_DATA));
                ristoranteLabel.setText("Ristorante #" + item.getIdRistorante());
                titoloLabel.setText(item.getTitolo());
                stelleLabel.setText("★".repeat(item.getStelle()) + "☆".repeat(5 - item.getStelle()));
                testoLabel.setText(item.getTesto());

                boolean haRisposta = item.getRisposta() != null;
                rispostaLabel.setVisible(haRisposta);
                rispostaLabel.setManaged(haRisposta);
                if (haRisposta) {
                    rispostaLabel.setText("Risposta: " + item.getRisposta());
                }
                rispostaField.clear();
                rispondiRow.setVisible(!haRisposta);
                rispondiRow.setManaged(!haRisposta);

                setGraphic(contenuto);
            }
        }
    }
}
