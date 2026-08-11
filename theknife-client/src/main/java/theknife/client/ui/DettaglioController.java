package theknife.client.ui;

import java.io.IOException;
import java.util.Optional;

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
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import theknife.client.service.RecensioneService;
import theknife.client.service.RistoranteService;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.RispondiRecensioneDTO;

/**
 * Controller della schermata di dettaglio (S06).
 *
 * @author Barlera Marco, 760000, VA
 */

public class DettaglioController {
    /** Nome del ristorante. */
    @FXML private Label nomeLabel;
    /** Tipo di cucina del ristorante. */
    @FXML private Label tipoCucinaLabel;
    /** Indirizzo del ristorante. */
    @FXML private Label indirizzoLabel;
    /** Fascia di prezzo del ristorante. */
    @FXML private Label fasciaPrezzoLabel;
    /** Media delle stelle delle recensioni del ristorante. */
    @FXML private Label mediaStelleLabel;

    /** Bottone per aggiungere il ristorante ai preferiti. */
    @FXML private Button preferitiButton;
    /** Bottone per scrivere una recensione al ristorante. */
    @FXML private Button scriviRecensioneButton;
    /** Bottone per rispondere alla recensione selezionata nella lista. */
    @FXML private Button rispondiRecensioneButton;
    /** Bottone per tornare alla schermata dei risultati. */
    @FXML private Button tornaIndietroButton;
    /** Lista delle recensioni del ristorante. */
    @FXML private ListView<RecensioneDTO> recensioniListView;

    private final RistoranteService ristoranteService = new RistoranteService();
    private final RecensioneService recensioneService = new RecensioneService();
    private long idRistorante;

    /**
     * Aggiunge il ristorante corrente ai preferiti dell'utente.
     */
    @FXML private void handlePreferiti() {
        TaskRunner.run(
            () -> {
                ristoranteService.aggiungiPreferito(new IdRistoranteDTO(idRistorante));
                return null;
            },
            esito -> {
                new Alert(Alert.AlertType.INFORMATION, "Ristorante aggiunto ai preferiti.").showAndWait();
            }
        );
    }

    /**
     * Naviga alla schermata di scrittura recensione, passando id e nome del
     * ristorante corrente.
     *
     * @param event l'evento generato dal click sul bottone "Scrivi recensione"
     */
    @FXML private void handleScriviRecensione(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/recensione.fxml"));
            Parent root = loader.load();
            ScriviRecensioneController controller = loader.getController();
            controller.impostaRistorante(idRistorante, nomeLabel.getText());
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
        }

    }

    /**
     * Chiede il testo della risposta alla recensione selezionata nella lista
     * e la invia al server. Mostra un avviso se nessun elemento è
     * selezionato; se l'utente annulla il dialog, non invia nulla.
     */
    @FXML private void handleRispondiRecensione() {
        RecensioneDTO selezionata = recensioniListView.getSelectionModel().getSelectedItem();
        if(selezionata == null) {
            new Alert(Alert.AlertType.WARNING, "Seleziona una recensione a cui rispondere.").showAndWait();
        }
        else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Scrivi la risposta");
            Optional<String> risposta = dialog.showAndWait();

            risposta.ifPresent(testo -> {
                TaskRunner.run(
                    () -> {
                        recensioneService.rispondiRecensione(new RispondiRecensioneDTO(selezionata.getIdRecensione(), testo));
                        return null;
                    },
                    esito -> caricaRecensioni()
                );
            });
        }
    }

    /**
     * Naviga alla schermata dei risultati di ricerca.
     *
     * @param event l'evento generato dal click sul bottone "Torna indietro"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleTornaIndietro(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/risultati.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    /**
     * Carica dal server i dettagli e le recensioni del ristorante indicato e
     * popola la schermata. Le due chiamate sono indipendenti: ognuna
     * aggiorna la propria parte di schermo quando la sua risposta arriva.
     *
     * @param idRistorante l'identificativo del ristorante da mostrare
     */
    public void impostaRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
        TaskRunner.run(
            () -> ristoranteService.ottieniDettagli(new IdRistoranteDTO(idRistorante)),
            ristorante -> {
                nomeLabel.setText(ristorante.getNome());
                tipoCucinaLabel.setText(ristorante.getTipoCucina());
                indirizzoLabel.setText(ristorante.getIndirizzo());
                fasciaPrezzoLabel.setText(String.valueOf(ristorante.getFasciaPrezzo()));
                mediaStelleLabel.setText(String.valueOf(ristorante.getMediaStelle()));
            }
        );

        caricaRecensioni();
    }

    private void caricaRecensioni() {
    TaskRunner.run(
        () -> recensioneService.leggiRecensioni(new IdRistoranteDTO(idRistorante)),
        recensioni -> {
            recensioniListView.getItems().setAll(recensioni);
            recensioniListView.setCellFactory(lv -> new ListCell<RecensioneDTO>() {
                @Override
                protected void updateItem(RecensioneDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String base = item.getStelle() + "★ - " + item.getTesto();
                        setText(item.getRisposta() == null ? base : base + " [risposto]");
                    }
                }
            });
        }
    );
}
}
