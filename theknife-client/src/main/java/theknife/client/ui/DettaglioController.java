package theknife.client.ui;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import theknife.client.network.ServerConnection;
import theknife.client.service.RecensioneService;
import theknife.client.service.RistoranteService;
import theknife.common.dto.IdRecensioneDTO;
import theknife.common.dto.IdRistoranteDTO;
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
    /** Icona a cuore su {@code preferitiButton}: piena se nei preferiti, vuota altrimenti. */
    @FXML private SVGPath cuoreIcon;
    /** Bottone per scrivere una recensione al ristorante. */
    @FXML private Button scriviRecensioneButton;
    /** Bottone per rispondere alla recensione selezionata nella lista, visibile solo al gestore del ristorante (RF18). */
    @FXML private Button rispondiRecensioneButton;
    /**
     * Bottone per eliminare il ristorante, visibile solo al gestore (S42).
     * Non ancora collegato al server: il comando {@code ELIMINA_RISTORANTE}
     * non esiste ancora lato server, solo il pulsante è pronto.
     */
    @FXML private Button eliminaRistoranteButton;
    /** Bottone per tornare alla schermata dei risultati. */
    @FXML private Button tornaIndietroButton;
    /** Lista delle recensioni del ristorante. */
    @FXML private ListView<RecensioneDTO> recensioniListView;
    /** Bottone per modificare la recensione selezionata nella lista. */
    @FXML private Button modificaRecensioneButton;
    /** Bottone per eliminare la recensione selezionata nella lista. */
    @FXML private Button eliminaRecensioneButton;

    /** Invia al server i comandi sui ristoranti, incluso l'aggiornamento dei preferiti. */
    private final RistoranteService ristoranteService = new RistoranteService();
    /** Invia al server i comandi sulle recensioni del ristorante mostrato. */
    private final RecensioneService recensioneService = new RecensioneService();
    /** Identificativo del ristorante mostrato, valorizzato da {@link #impostaRistorante(long)}. */
    private long idRistorante;
    /**
     * Lista di ristoranti mostrata da Risultati prima di aprire questo
     * dettaglio, valorizzata da {@link #impostaRisultatiPrecedenti(List)}.
     * Ripassata a Risultati da {@link #handleTornaIndietro(ActionEvent)},
     * così "Torna Indietro" non riparte da una lista vuota.
     */
    private List<RistoranteDTO> risultatiPrecedenti;

    /** Le schermate da cui si può arrivare a questo dettaglio. */
    private enum Provenienza { RISULTATI, DASHBOARD, PREFERITI }

    /**
     * Da dove si è arrivati a questa schermata, valorizzata da
     * {@link #impostaProvenienzaDashboard()}/{@link #impostaProvenienzaPreferiti()}
     * — default {@code RISULTATI}, l'unico caso con una lista di ricerca da
     * riproporre. {@link #handleTornaIndietro(ActionEvent)} si biforca di
     * conseguenza (S40).
     */
    private Provenienza provenienza = Provenienza.RISULTATI;

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
                    aggiornaPreferitiButton();
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
                    aggiornaPreferitiButton();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/scriviRecensione.fxml"));
            Parent root = loader.load();
            ScriviRecensioneController controller = loader.getController();
            controller.impostaRistorante(idRistorante, nomeLabel.getText());
            controller.impostaRisultatiPrecedenti(risultatiPrecedenti);
            stage.getScene().setRoot(root);
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
     * Elimina il ristorante corrente. Non ancora collegato: il comando
     * {@code ELIMINA_RISTORANTE} non esiste ancora lato server (S42), quindi
     * per ora mostra solo un avviso invece di fingere di funzionare.
     */
    @FXML private void handleEliminaRistorante() {
        new Alert(Alert.AlertType.INFORMATION, "Funzionalità in arrivo").showAndWait();
    }

    /**
     * Naviga alla schermata di provenienza (S40, {@link #provenienza}):
     * Risultati con la lista che l'aveva mostrata, oppure Dashboard/Preferiti
     * se non c'era una ricerca dietro.
     *
     * @param event l'evento generato dal click sul bottone "Torna indietro"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleTornaIndietro(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        switch (provenienza) {
            case DASHBOARD -> {
                Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
                stage.getScene().setRoot(root);
            }
            case PREFERITI -> {
                Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/preferiti.fxml"));
                stage.getScene().setRoot(root);
            }
            case RISULTATI -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/risultati.fxml"));
                Parent root = loader.load();
                RisultatiController controller = loader.getController();
                controller.impostaRisultati(risultatiPrecedenti);
                stage.getScene().setRoot(root);
            }
        }
    }

    /**
     * Naviga alla schermata di ScriviRecensione in modalità modifica,
     * pre-riempita con i valori della recensione selezionata nella lista —
     * stessa card, stesse stelle cliccabili, invece di dialog nativi in
     * sequenza. Mostra un avviso se nessun elemento è selezionato.
     *
     * @param event l'evento generato dal click sul bottone "Modifica Recensione"
     */
    @FXML private void handleModificaRecensione(ActionEvent event) {
        RecensioneDTO selezionata = recensioniListView.getSelectionModel().getSelectedItem();
        if (selezionata == null) {
            new Alert(Alert.AlertType.WARNING, "Seleziona una recensione da modificare.").showAndWait();
            return;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/scriviRecensione.fxml"));
            Parent root = loader.load();
            ScriviRecensioneController controller = loader.getController();
            controller.impostaRistorante(idRistorante, nomeLabel.getText());
            controller.impostaRisultatiPrecedenti(risultatiPrecedenti);
            controller.impostaRecensioneDaModificare(selezionata);
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
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
            Optional<ButtonType> result = conferma.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
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
     * Registra la lista di ristoranti da cui si arriva, così
     * {@link #handleTornaIndietro(ActionEvent)} può riproporla a Risultati
     * invece di ricaricare la schermata vuota.
     *
     * @param risultati i ristoranti mostrati dalla schermata di provenienza
     */
    public void impostaRisultatiPrecedenti(List<RistoranteDTO> risultati) {
        this.risultatiPrecedenti = risultati;
    }

    /**
     * Segnala che si arriva a questa schermata da Aggiungi Ristorante: "Torna
     * Indietro" andrà alla Dashboard invece che a Risultati (S40).
     */
    public void impostaProvenienzaDashboard() {
        this.provenienza = Provenienza.DASHBOARD;
    }

    /**
     * Segnala che si arriva a questa schermata da Preferiti: "Torna Indietro"
     * andrà lì invece che a Risultati (S40).
     */
    public void impostaProvenienzaPreferiti() {
        this.provenienza = Provenienza.PREFERITI;
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
     * <p>Nasconde {@code preferitiButton} e {@code scriviRecensioneButton} da
     * guest (RF08/RF09/RF10 richiedono login; filtro cosmetico, controllo
     * vero lato server) — risolve anche S39, l'errore incomprensibile che il
     * guest otteneva provando a scrivere una recensione. Da autenticato,
     * chiede anche la lista dei preferiti dell'utente per determinare se il
     * ristorante corrente ne fa già parte, aggiornando di conseguenza
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
        modificaRecensioneButton.setManaged(false);
        eliminaRecensioneButton.setVisible(false);
        eliminaRecensioneButton.setManaged(false);
        rispondiRecensioneButton.setVisible(false);
        rispondiRecensioneButton.setManaged(false);
        eliminaRistoranteButton.setVisible(false);
        eliminaRistoranteButton.setManaged(false);
        boolean ospite = ServerConnection.getInstance().getUtenteCorrente() == null;
        preferitiButton.setVisible(!ospite);
        preferitiButton.setManaged(!ospite);
        scriviRecensioneButton.setVisible(!ospite);
        scriviRecensioneButton.setManaged(!ospite);
        recensioniListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                UtenteDTO utente = ServerConnection.getInstance().getUtenteCorrente();
                boolean isProprietario = utente != null && newSelection.getIdUtente() == utente.getIdUtente();
                modificaRecensioneButton.setVisible(isProprietario);
                modificaRecensioneButton.setManaged(isProprietario);
                eliminaRecensioneButton.setVisible(isProprietario);
                eliminaRecensioneButton.setManaged(isProprietario);
            } else {
                modificaRecensioneButton.setVisible(false);
                modificaRecensioneButton.setManaged(false);
                eliminaRecensioneButton.setVisible(false);
                eliminaRecensioneButton.setManaged(false);
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
                rispondiRecensioneButton.setManaged(isGestore);
                eliminaRistoranteButton.setVisible(isGestore);
                eliminaRistoranteButton.setManaged(isGestore);
            }
        );

        if(ServerConnection.getInstance().getUtenteCorrente() != null)
            TaskRunner.run(
                () -> ristoranteService.ottieniPreferiti(),
                preferito -> {
                    for (RistoranteDTO r : preferito) {
                        if (r.getIdRistorante() == idRistorante) {
                            this.preferito = true;
                            break;
                        }
                    }
                    aggiornaPreferitiButton();
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
                private final Label titoloLabel = new Label();
                private final Label testoLabel = new Label();
                private final Label rispostaLabel = new Label();
                private final VBox contenuto = new VBox(titoloLabel, testoLabel, rispostaLabel);
                {
                    titoloLabel.getStyleClass().add("risultato-nome");
                    testoLabel.getStyleClass().add("risultato-info");
                    rispostaLabel.getStyleClass().add("recensione-risposta");
                }
                @Override
                protected void updateItem(RecensioneDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        titoloLabel.setText(item.getStelle() + "★ " + item.getTitolo());
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
            });
        }
    );
    }

    /**
     * Aggiorna testo e icona di {@code preferitiButton} in base a
     * {@link #preferito}, così che riflettano sempre lo stato reale
     * (RF08/RF09) invece di restare fissi su "Aggiungi ai preferiti": cuore
     * pieno quando il ristorante è già nei preferiti, vuoto altrimenti.
     */
    private void aggiornaPreferitiButton() {
        preferitiButton.setText(preferito ? "Rimuovi dai preferiti" : "Aggiungi ai preferiti");
        cuoreIcon.setFill(preferito ? Color.web("#FAB12F") : Color.TRANSPARENT);
    }
}
