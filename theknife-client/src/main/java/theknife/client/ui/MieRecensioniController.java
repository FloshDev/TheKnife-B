package theknife.client.ui;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import theknife.client.service.RecensioneService;
import theknife.common.dto.IdRecensioneDTO;
import theknife.common.dto.RecensioneDTO;

/**
 * Controller della schermata "Le mie recensioni": elenca tutte le recensioni
 * scritte dal Cliente autenticato, su qualsiasi ristorante, con Modifica/
 * Elimina inline su ogni card — richiesta esplicita dal docente (slide
 * "Specifiche di Progetto", pag. 15) non coperta da nessun'altra schermata,
 * dato che RF12/RF13 erano raggiungibili solo dal Dettaglio del singolo
 * ristorante.
 *
 * @author Barlera Marco, 760000, VA
 */

public class MieRecensioniController {
    /**
     * Costruttore vuoto: tutta l'inizializzazione avviene in {@code initialize()},
     * chiamato da FXMLLoader dopo l'injection dei campi {@code @FXML}.
     */
    public MieRecensioniController() {
    }

    /** Lista delle recensioni scritte dall'utente, con azioni inline per card. */
    @FXML private ListView<RecensioneDTO> recensioniListView;
    /** Bottone icona per tornare alla Home. */
    @FXML private Button tornaIndietroButton;
    /** Controller della sidebar inclusa (fx:include). */
    @FXML private SidebarController sidebarController;

    /** Invia al server i comandi sulle recensioni dell'utente. */
    private final RecensioneService recensioneService = new RecensioneService();
    /** Formato di visualizzazione della data delle recensioni (es. "12 ago 2026"). */
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ITALIAN);

    /**
     * Carica le recensioni all'apertura della schermata.
     */
    @FXML private void initialize() {
        sidebarController.impostaAttivo(SidebarController.Voce.MIE_RECENSIONI);
        Label nessunaRecensione = new Label("Non hai ancora scritto nessuna recensione.");
        nessunaRecensione.getStyleClass().add("risultato-info");
        nessunaRecensione.setWrapText(true);
        nessunaRecensione.prefWidthProperty().bind(recensioniListView.widthProperty().subtract(40));
        recensioniListView.setPlaceholder(nessunaRecensione);
        caricaRecensioni();
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
        stage.getScene().setRoot(root);
    }

    /**
     * Carica dal server tutte le recensioni scritte dall'utente e le mostra,
     * sostituendo il contenuto attuale della lista.
     */
    private void caricaRecensioni() {
        TaskRunner.run(
            () -> recensioneService.leggiRecensioniCliente(),
            recensioni -> {
                recensioniListView.getItems().setAll(recensioni);
                recensioniListView.setCellFactory(lv -> new RecensioneScrittaCell());
            }
        );
    }

    /**
     * Naviga alla schermata di ScriviRecensione in modalità modifica,
     * pre-riempita con i valori della recensione indicata, segnalando di
     * tornare qui al termine invece che al Dettaglio del ristorante.
     *
     * @param recensione la recensione da modificare
     */
    private void modifica(RecensioneDTO recensione) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/scriviRecensione.fxml"));
            Parent root = loader.load();
            ScriviRecensioneController controller = loader.getController();
            controller.impostaRistorante(recensione.getIdRistorante(), recensione.getNomeRistorante());
            controller.impostaRecensioneDaModificare(recensione);
            controller.impostaProvenienzaMieRecensioni();
            Stage stage = (Stage) recensioniListView.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
        }
    }

    /**
     * Elimina la recensione indicata, dopo conferma dell'utente (RF13).
     *
     * @param recensione la recensione da eliminare
     */
    private void elimina(RecensioneDTO recensione) {
        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler eliminare la recensione selezionata?");
        Toast.stilizza(conferma);
        Optional<ButtonType> result = conferma.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TaskRunner.run(
                () -> {
                    recensioneService.eliminaRecensione(new IdRecensioneDTO(recensione.getIdRecensione()));
                    return null;
                },
                esito -> caricaRecensioni()
            );
        }
    }

    /**
     * Cella della lista recensioni: nome del ristorante, data, titolo, stelle
     * e testo, con l'eventuale risposta del gestore e i bottoni Modifica/
     * Elimina — sempre visibili, dato che la lista contiene solo recensioni
     * dell'utente corrente.
     */
    private class RecensioneScrittaCell extends ListCell<RecensioneDTO> {
        /** Costruttore vuoto: i nodi grafici della cella sono creati inline nei campi, la logica sta in {@code updateItem}. */
        RecensioneScrittaCell() {
        }

        /** Nome del ristorante recensito. */
        private final Label ristoranteLabel = new Label();
        /** Data della recensione. */
        private final Label dataLabel = new Label();
        /** Riga con ristorante e data affiancati. */
        private final HBox intestazione = new HBox(ristoranteLabel, dataLabel);
        /** Titolo della recensione. */
        private final Label titoloLabel = new Label();
        /** Valutazione in stelle. */
        private final Label stelleLabel = new Label();
        /** Testo della recensione. */
        private final Label testoLabel = new Label();
        /** Risposta del gestore, se presente. */
        private final Label rispostaLabel = new Label();
        /** Bottone "Modifica" per aprire la recensione in modifica. */
        private final Button modificaButton = new Button("Modifica");
        /** Bottone "Elimina" per rimuovere la recensione. */
        private final Button eliminaButton = new Button("Elimina");
        /** Riga con i bottoni Modifica/Elimina. */
        private final HBox azioniRow = new HBox(8, modificaButton, eliminaButton);
        /** Contenitore radice della cella. */
        private final VBox contenuto = new VBox(6,
            intestazione, titoloLabel, stelleLabel, testoLabel, rispostaLabel, azioniRow);

        {
            contenuto.getStyleClass().add("recensione-riga");
            ristoranteLabel.getStyleClass().add("risultato-nome");
            ristoranteLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(ristoranteLabel, Priority.ALWAYS);
            dataLabel.getStyleClass().add("recensione-data");
            titoloLabel.getStyleClass().add("dettaglio-info");
            stelleLabel.getStyleClass().add("recensione-stelle");
            testoLabel.getStyleClass().add("risultato-info");
            testoLabel.setWrapText(true);
            testoLabel.prefWidthProperty().bind(recensioniListView.widthProperty().subtract(60));
            rispostaLabel.getStyleClass().add("recensione-risposta");
            rispostaLabel.setWrapText(true);
            rispostaLabel.prefWidthProperty().bind(recensioniListView.widthProperty().subtract(60));
            azioniRow.setAlignment(Pos.CENTER_RIGHT);
            modificaButton.getStyleClass().add("bottone-piccolo");
            eliminaButton.getStyleClass().add("bottone-piccolo");

            modificaButton.setOnAction(e -> modifica(getItem()));
            eliminaButton.setOnAction(e -> elimina(getItem()));
        }

        @Override
        protected void updateItem(RecensioneDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                ristoranteLabel.setText(item.getNomeRistorante());
                dataLabel.setText(item.getDataPubblicazione() == null ? "" : item.getDataPubblicazione().format(FORMATO_DATA));
                titoloLabel.setText(item.getTitolo());
                stelleLabel.setText("★".repeat(item.getStelle()) + "☆".repeat(5 - item.getStelle()));
                testoLabel.setText(item.getTesto());

                boolean haRisposta = item.getRisposta() != null;
                rispostaLabel.setVisible(haRisposta);
                rispostaLabel.setManaged(haRisposta);
                if (haRisposta) {
                    rispostaLabel.setText("Risposta del gestore: " + item.getRisposta());
                }

                setGraphic(contenuto);
            }
        }
    }
}
