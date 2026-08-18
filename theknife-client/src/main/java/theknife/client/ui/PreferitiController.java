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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import theknife.client.service.RistoranteService;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.RistoranteDTO;


/**
 * Controller della schermata Preferiti (S08).
 *
 * @author Barlera Marco, 760000, VA
 */

public class PreferitiController {

    /** Titolo della schermata dei preferiti. */
    @FXML private Label preferitiLabel;
    /** Lista dei ristoranti preferiti dell'utente. */
    @FXML private ListView<RistoranteDTO> preferitiListView;
    /** Bottone per aprire il dettaglio del ristorante selezionato. */
    @FXML private Button vediDettaglioButton;
    /** Bottone per rimuovere il ristorante selezionato dai preferiti. */
    @FXML private Button rimuoviPreferitoButton;
    /** Bottone per tornare alla schermata Home. */
    @FXML private Button tornaIndietroButton;

    /** Invia al server i comandi sui ristoranti preferiti dell'utente. */
    private final RistoranteService ristoranteService = new RistoranteService();

    /**
     * Carica i preferiti all'apertura della schermata.
     */
    @FXML private void initialize() {
        caricaPreferiti();
    }

    /**
     * Naviga al dettaglio del ristorante selezionato nella lista. Mostra un
     * avviso se nessun elemento è selezionato.
     *
     * @param event l'evento generato dal click sul bottone "Vedi dettaglio"
     */
    @FXML private void handleVediDettaglio(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        RistoranteDTO selectedRistorante = preferitiListView.getSelectionModel().getSelectedItem();
        if (selectedRistorante != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
                Parent root = loader.load();
                DettaglioController controller = loader.getController();
                controller.impostaRistorante(selectedRistorante.getIdRistorante());
                controller.impostaProvenienzaPreferiti();
                stage.getScene().setRoot(root);
            } catch (IOException e) {
                Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
            }
        }
        else {
            Toast.errore("Nessun ristorante selezionato");
        }
    }

    /**
     * Rimuove dai preferiti il ristorante selezionato nella lista e ricarica
     * l'elenco. Mostra un avviso se nessun elemento è selezionato.
     *
     * @param event l'evento generato dal click sul bottone "Rimuovi preferito"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleRimuoviPreferito(ActionEvent event) throws IOException {
        RistoranteDTO selectedRistorante = preferitiListView.getSelectionModel().getSelectedItem();
        if (selectedRistorante != null) {
            TaskRunner.run(
                () -> { ristoranteService.rimuoviPreferito(new IdRistoranteDTO(selectedRistorante.getIdRistorante()));
                        return null;
                    },
                result -> {
                    caricaPreferiti();
                    Toast.successo("Ristorante rimosso dai preferiti");
                }
            );
        }
        else {
            Toast.errore("Nessun ristorante selezionato");
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
        stage.getScene().setRoot(root);
    }

    /**
     * Carica dal server la lista dei ristoranti preferiti dell'utente e la
     * mostra, sostituendo il contenuto attuale della lista.
     */
    private void caricaPreferiti() {
        TaskRunner.run(
            () -> ristoranteService.ottieniPreferiti(),
            ristoranti -> {
                preferitiListView.getItems().setAll(ristoranti);
                preferitiListView.setCellFactory(lv -> new ListCell<RistoranteDTO>() {
                    private final Label nomeLabel = new Label();
                    private final Label infoLabel = new Label();
                    private final VBox contenuto = new VBox(nomeLabel, infoLabel);
                    {
                        nomeLabel.getStyleClass().add("risultato-nome");
                        infoLabel.getStyleClass().add("risultato-info");
                    }

                    @Override
                    protected void updateItem(RistoranteDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            nomeLabel.setText(item.getNome());
                            infoLabel.setText(item.getCitta() + " · " + item.getTipoCucina());
                            setGraphic(contenuto);
                        }
                    }
                });
            }
        );
    }
}
