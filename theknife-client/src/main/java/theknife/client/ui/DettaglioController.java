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
import theknife.client.network.ServerConnection;
import theknife.client.service.RecensioneService;
import theknife.client.service.RistoranteService;
import theknife.common.dto.IdRecensioneDTO;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.ModificaRecensioneDTO;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.RispondiRecensioneDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.UtenteDTO;

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

    /** Bottone per aggiungere o rimuovere il ristorante dai preferiti (RF08/RF09). */
    @FXML private Button preferitiButton;
    /** Bottone per scrivere una recensione al ristorante. */
    @FXML private Button scriviRecensioneButton;
    /** Bottone per rispondere alla recensione selezionata nella lista, visibile solo al gestore del ristorante (RF18). */
    @FXML private Button rispondiRecensioneButton;
    /** Bottone per tornare alla schermata dei risultati. */
    @FXML private Button tornaIndietroButton;
    /** Lista delle recensioni del ristorante. */
    @FXML private ListView<RecensioneDTO> recensioniListView;
    /** Bottone per modificare la recensione selezionata nella lista. */
    @FXML private Button modificaRecensioneButton;
    /** Bottone per eliminare la recensione selezionata nella lista. */
    @FXML private Button eliminaRecensioneButton;

    private final RistoranteService ristoranteService = new RistoranteService();
    private final RecensioneService recensioneService = new RecensioneService();
    private long idRistorante;

    /**
     * Se il ristorante corrente è già nei preferiti dell'utente loggato
     * (RF08/RF09). Calcolato in {@link #impostaRistorante(long)} interrogando
     * il server, non è un dato che arriva col dettaglio del ristorante.
     */
    private boolean preferito = false;

    /**
     * Aggiunge o rimuove il ristorante corrente dai preferiti dell'utente, a
     * seconda dello stato attuale di {@link #preferito} (RF08/RF09): un solo
     * bottone che alterna comportamento invece di due bottoni separati.
     */
    @FXML private void handlePreferiti() {
        if(preferito) {
            TaskRunner.run(
                () -> {
                    ristoranteService.rimuoviPreferito(new IdRistoranteDTO(idRistorante));
                    return null;
                },
                esito -> {
                    preferito = false;
                    aggiornaTestoPreferiti();
                    new Alert(Alert.AlertType.INFORMATION, "Ristorante rimosso dai preferiti.").showAndWait();
                }
            );
        }
        else{
            TaskRunner.run(
                () -> {
                    ristoranteService.aggiungiPreferito(new IdRistoranteDTO(idRistorante));
                    return null;
                },
                esito -> {
                    preferito = true;
                    aggiornaTestoPreferiti();
                    new Alert(Alert.AlertType.INFORMATION, "Ristorante aggiunto ai preferiti.").showAndWait();
                }
            );
        }
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
     * Chiede titolo, stelle e testo aggiornati per la recensione selezionata
     * nella lista e li invia al server. Ogni campo è pre-riempito col valore
     * attuale nel rispettivo dialog; se l'utente annulla il dialog del
     * titolo non invia nulla, mentre gli altri due, se annullati, mantengono
     * il valore attuale (stelle) o il testo attuale (RF12).
     */
    @FXML private void handleModificaRecensione() {
        RecensioneDTO selezionata = recensioniListView.getSelectionModel().getSelectedItem();
        if(selezionata == null) {
            new Alert(Alert.AlertType.WARNING, "Seleziona una recensione da modificare.").showAndWait();
        }
        else {
            TextInputDialog dialog = new TextInputDialog(selezionata.getTitolo());
            dialog.setHeaderText("Modifica il titolo");
            Optional<String> titoloModificato = dialog.showAndWait();

            TextInputDialog dialog2 = new TextInputDialog(String.valueOf(selezionata.getStelle()));
            dialog2.setHeaderText("Modifica le stelle");
            Optional<String> stelleModificate = dialog2.showAndWait();

            TextInputDialog dialog3 = new TextInputDialog(selezionata.getTesto());
            dialog3.setHeaderText("Modifica il testo");
            Optional<String> testoModificato = dialog3.showAndWait();

            titoloModificato.ifPresent(nuovoTitolo -> {
                int nuoveStelle = stelleModificate.isPresent() ? Integer.parseInt(stelleModificate.get()) : selezionata.getStelle();

                TaskRunner.run(
                    () -> {
                        recensioneService.modificaRecensione(new ModificaRecensioneDTO(selezionata.getIdRecensione(), nuovoTitolo, testoModificato.orElse(selezionata.getTesto()), nuoveStelle));
                        return null;
                    },
                    esito -> caricaRecensioni()
                );
            });
        }
    }

    /**
     * Elimina la recensione selezionata nella lista, dopo conferma
     * dell'utente (RF13). Mostra un avviso se nessun elemento è
     * selezionato; se l'utente annulla la conferma, non elimina nulla.
     */
    @FXML private void handleEliminaRecensione() {
        RecensioneDTO selezionata = recensioniListView.getSelectionModel().getSelectedItem();
        if(selezionata == null) {
            new Alert(Alert.AlertType.WARNING, "Seleziona una recensione da eliminare.").showAndWait();
        }
        else {
            Alert conferma = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler eliminare la recensione selezionata?");
            Optional<javafx.scene.control.ButtonType> result = conferma.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                TaskRunner.run(
                    () -> {
                        recensioneService.eliminaRecensione(new IdRecensioneDTO(selezionata.getIdRecensione()));
                        return null;
                    },
                    esito -> caricaRecensioni()
                );
            }
        }
    }

    /**
     * Carica dal server i dettagli e le recensioni del ristorante indicato e
     * popola la schermata. Le due chiamate sono indipendenti: ognuna
     * aggiorna la propria parte di schermo quando la sua risposta arriva.
     *
     * <p>Registra anche, una sola volta, il listener sulla selezione della
     * lista recensioni che mostra {@code modificaRecensioneButton} ed
     * {@code eliminaRecensioneButton} solo quando la recensione selezionata
     * appartiene all'utente corrente (RF12/RF13); entrambi restano nascosti
     * finché non è selezionata una recensione propria. Il filtro è solo
     * cosmetico: l'autorizzazione vera è demandata al server.
     *
     * <p>Chiede anche la lista dei preferiti dell'utente per determinare se
     * il ristorante corrente ne fa già parte, aggiornando di conseguenza
     * {@link #preferito} e il testo di {@code preferitiButton} (RF08/RF09).
     *
     * <p>Mostra {@code rispondiRecensioneButton} solo se l'utente corrente è
     * il gestore del ristorante (RF18, decisione 24): a differenza di
     * modifica/elimina, qui il controllo è unico per l'intera schermata, non
     * per singola recensione selezionata, perché dipende dal ristorante e
     * non da chi ha scritto la recensione.
     *
     * @param idRistorante l'identificativo del ristorante da mostrare
     */
    public void impostaRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
        modificaRecensioneButton.setVisible(false);
        eliminaRecensioneButton.setVisible(false);
        rispondiRecensioneButton.setVisible(false);
        recensioniListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                UtenteDTO utente = ServerConnection.getInstance().getUtenteCorrente();
                boolean isProprietario = utente != null && newSelection.getIdUtente() == utente.getIdUtente();
                modificaRecensioneButton.setVisible(isProprietario);
                eliminaRecensioneButton.setVisible(isProprietario);
            } else {
                modificaRecensioneButton.setVisible(false);
                eliminaRecensioneButton.setVisible(false);
            }
        });
        TaskRunner.run(
            () -> ristoranteService.ottieniDettagli(new IdRistoranteDTO(idRistorante)),
            ristorante -> {
                nomeLabel.setText(ristorante.getNome());
                tipoCucinaLabel.setText(ristorante.getTipoCucina());
                indirizzoLabel.setText(ristorante.getIndirizzo());
                fasciaPrezzoLabel.setText(String.valueOf(ristorante.getFasciaPrezzo()));
                mediaStelleLabel.setText(String.valueOf(ristorante.getMediaStelle()));
                UtenteDTO utente = ServerConnection.getInstance().getUtenteCorrente();
                boolean isGestore = utente != null && ristorante.getIdGestore() != null && ristorante.getIdGestore() == utente.getIdUtente();
                rispondiRecensioneButton.setVisible(isGestore);
            }
        );

        TaskRunner.run(
            () -> ristoranteService.ottieniPreferiti(),
            preferito -> {
                for (RistoranteDTO r : preferito) {
                    if (r.getIdRistorante() == idRistorante) {
                        this.preferito = true;
                        break;
                    }
                }
                aggiornaTestoPreferiti();
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

    /**
     * Aggiorna il testo di {@code preferitiButton} in base a
     * {@link #preferito}, così che rifletta sempre lo stato reale (RF08/RF09)
     * invece di restare fisso su "Aggiungi ai preferiti".
     */
    private void aggiornaTestoPreferiti() {
        preferitiButton.setText(preferito ? "Nei preferiti" : "Aggiungi ai preferiti");
    }
}
