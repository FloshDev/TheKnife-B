package theknife.client.ui;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.web.WebView;
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
import theknife.common.enums.Ruolo;

/**
 * Controller della schermata di dettaglio (S06).
 *
 * @author Barlera Marco, 760000, VA
 */

public class DettaglioController {
    /** Il contenuto, la cui larghezza è agganciata alla finestra (grafica responsive). */
    @FXML private VBox card;
    /** Controller della sidebar inclusa (fx:include), per evidenziare la voce attiva coerente con la provenienza. */
    @FXML private SidebarController sidebarController;
    /** Nome del ristorante. */
    @FXML private Label nomeLabel;
    /** Riga informativa: cucina · fascia di prezzo · indirizzo. */
    @FXML private Label infoLabel;
    /** Badge con valutazione media e numero di recensioni. */
    @FXML private Label mediaStelleLabel;
    /** Tag "Prenotazione online", visibile solo se il ristorante la offre. */
    @FXML private Label prenotazioneTag;
    /** Tag "Consegna a domicilio", visibile solo se il ristorante la offre. */
    @FXML private Label consegnaTag;
    /** Bottone per aggiungere o rimuovere il ristorante dai preferiti (RF08/RF09). */
    @FXML private Button preferitiButton;
    /** Icona a cuore su {@code preferitiButton}: piena se nei preferiti, vuota altrimenti. */
    @FXML private SVGPath cuoreIcon;
    /** Bottone per scrivere una recensione al ristorante. */
    @FXML private Button scriviRecensioneButton;
    /** Bottone per eliminare il ristorante, visibile solo al gestore (RF18, S42/S44). */
    @FXML private Button eliminaRistoranteButton;
    /** Bottone icona per tornare alla schermata di provenienza. */
    @FXML private Button tornaIndietroButton;
    /** Lista delle recensioni del ristorante. */
    @FXML private ListView<RecensioneDTO> recensioniListView;
    /** Il nodo radice del componente mappa incluso (fx:include), per impostarne l'altezza. */
    @FXML private WebView mappa;
    /** Controller del componente mappa incluso (fx:include, decisione 34): un solo pin statico. */
    @FXML private MappaController mappaController;

    /** Invia al server i comandi sui ristoranti, incluso l'aggiornamento dei preferiti. */
    private final RistoranteService ristoranteService = new RistoranteService();
    /** Invia al server i comandi sulle recensioni del ristorante mostrato. */
    private final RecensioneService recensioneService = new RecensioneService();
    /** Formato di visualizzazione della data delle recensioni (es. "12 ago 2026"). */
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("d MMM yyyy");
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

    /** Se l'utente corrente è il Cliente autenticato (RF08/RF09/RF10), niente preferiti/recensioni da guest o Ristoratore. */
    private boolean cliente;
    /** Se l'utente corrente è il gestore del ristorante mostrato (RF18, S42). */
    private boolean gestore;

    /**
     * Se il ristorante corrente è già nei preferiti dell'utente loggato
     * (RF08/RF09). Calcolato in {@link #impostaRistorante(long)} interrogando
     * il server, non è un dato che arriva col dettaglio del ristorante.
     */
    private boolean preferito = false;

    /**
     * Aggancia la larghezza del contenuto alla finestra (grafica responsive)
     * ed evidenzia "Ricerca" nella sidebar (provenienza di default).
     */
    @FXML private void initialize() {
        Responsive.aggancia(card, 0.6, 480, 760);
        sidebarController.impostaAttivo(SidebarController.Voce.RICERCA);
        // MappaController azzera l'altezza preferita del WebView (serve per la divisione a
        // metà con una lista sulle altre schermate) — qui non c'è nessuna lista accanto,
        // un pin singolo statico, gli si dà un'altezza fissa invece.
        mappa.setPrefHeight(240);
    }

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
                    Toast.successo("Ristorante rimosso dai preferiti.");
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
                    Toast.successo("Ristorante aggiunto ai preferiti.");
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
            Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
        }

    }

    /**
     * Elimina il ristorante corrente, dopo conferma dell'utente (S44):
     * operazione distruttiva e irreversibile (con il ristorante spariscono
     * anche le sue recensioni, servizi e preferiti, cascata dichiarata nello
     * schema). Al successo torna alla dashboard, dato che il dettaglio non
     * ha più nulla da mostrare.
     *
     * @param event l'evento generato dal click sul bottone "Elimina ristorante"
     */
    @FXML private void handleEliminaRistorante(ActionEvent event) {
        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler eliminare questo ristorante? L'operazione non è reversibile.");
        Toast.stilizza(conferma);
        Optional<ButtonType> result = conferma.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            TaskRunner.run(
                () -> {
                    ristoranteService.eliminaRistorante(new IdRistoranteDTO(idRistorante));
                    return null;
                },
                esito -> {
                    Toast.successo("Ristorante eliminato.");
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
                        stage.getScene().setRoot(root);
                    } catch (IOException e) {
                        Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
                    }
                }
            );
        }
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
        sidebarController.impostaAttivo(SidebarController.Voce.DASHBOARD);
    }

    /**
     * Segnala che si arriva a questa schermata da Preferiti: "Torna Indietro"
     * andrà lì invece che a Risultati (S40).
     */
    public void impostaProvenienzaPreferiti() {
        this.provenienza = Provenienza.PREFERITI;
        sidebarController.impostaAttivo(SidebarController.Voce.PREFERITI);
    }

    /**
     * Carica dal server i dettagli e le recensioni del ristorante indicato e
     * popola la schermata. Le due chiamate sono indipendenti: ognuna
     * aggiorna la propria parte di schermo quando la sua risposta arriva.
     *
     * <p>Nasconde {@code preferitiButton} e {@code scriviRecensioneButton} da
     * guest (RF08/RF09/RF10 richiedono login; filtro cosmetico, controllo
     * vero lato server) — risolve anche S39, l'errore incomprensibile che il
     * guest otteneva provando a scrivere una recensione. Da autenticato,
     * chiede anche la lista dei preferiti dell'utente per determinare se il
     * ristorante corrente ne fa già parte, aggiornando di conseguenza
     * {@link #preferito} e l'icona di {@code preferitiButton} (RF08/RF09).
     *
     * <p>{@code eliminaRistoranteButton} e le azioni Modifica/Elimina/Rispondi
     * inline su ogni recensione sono visibili solo al gestore del ristorante
     * o all'autore della singola recensione (RF12/RF13/RF18, decisione 24) —
     * filtro cosmetico, l'autorizzazione vera è demandata al server.
     *
     * @param idRistorante l'identificativo del ristorante da mostrare
     */
    public void impostaRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
        Label nessunaRecensione = new Label("Nessuna recensione ancora. Scrivi la prima!");
        nessunaRecensione.getStyleClass().add("risultato-info");
        recensioniListView.setPlaceholder(nessunaRecensione);

        UtenteDTO utenteCorrente = ServerConnection.getInstance().getUtenteCorrente();
        cliente = utenteCorrente != null && utenteCorrente.getRuolo() == Ruolo.CLIENTE;
        preferitiButton.setVisible(cliente);
        preferitiButton.setManaged(cliente);
        scriviRecensioneButton.setVisible(cliente);
        scriviRecensioneButton.setManaged(cliente);

        TaskRunner.run(
            () -> ristoranteService.ottieniDettagli(new IdRistoranteDTO(idRistorante)),
            ristorante -> {
                nomeLabel.setText(ristorante.getNome());
                infoLabel.setText(ristorante.getTipoCucina() + " · " + "€".repeat(ristorante.getFasciaPrezzo()) + " · " + ristorante.getIndirizzo());
                mediaStelleLabel.setText(String.format("★ %.1f · %d recensioni", ristorante.getMediaStelle(), ristorante.getNumeroRecensioni()));
                prenotazioneTag.setVisible(ristorante.isPrenotazioneOnline());
                prenotazioneTag.setManaged(ristorante.isPrenotazioneOnline());
                consegnaTag.setVisible(ristorante.isConsegnaADomicilio());
                consegnaTag.setManaged(ristorante.isConsegnaADomicilio());

                UtenteDTO utente = ServerConnection.getInstance().getUtenteCorrente();
                gestore = utente != null && ristorante.getIdGestore() != null && ristorante.getIdGestore() == utente.getIdUtente();
                eliminaRistoranteButton.setVisible(gestore);
                eliminaRistoranteButton.setManaged(gestore);
                recensioniListView.refresh();

                mappaController.impostaPinSingolo(ristorante);
            }
        );

        if(cliente)
            TaskRunner.run(
                () -> ristoranteService.ottieniPreferiti(),
                preferiti -> {
                    for (RistoranteDTO r : preferiti) {
                        if (r.getIdRistorante() == idRistorante) {
                            this.preferito = true;
                            break;
                        }
                    }
                    aggiornaPreferitiButton();
                }
            );

        recensioniListView.setCellFactory(lv -> new RecensioneCell());
        caricaRecensioni();
    }

    /** Carica dal server le recensioni del ristorante corrente e ripopola la lista. */
    private void caricaRecensioni() {
        TaskRunner.run(
            () -> recensioneService.leggiRecensioni(new IdRistoranteDTO(idRistorante)),
            recensioni -> recensioniListView.getItems().setAll(recensioni)
        );
    }

    /**
     * Aggiorna l'icona di {@code preferitiButton} in base a
     * {@link #preferito}, così che rifletta sempre lo stato reale (RF08/RF09):
     * cuore pieno quando il ristorante è già nei preferiti, vuoto altrimenti.
     */
    private void aggiornaPreferitiButton() {
        cuoreIcon.setFill(preferito ? Color.web("#FAB12F") : Color.TRANSPARENT);
    }

    /**
     * Cella della lista recensioni: username dell'autore, data, titolo,
     * stelle e testo (decisione 33), con "Rispondi" (solo gestore, solo se
     * non ha già risposto) o "Modifica"/"Elimina" (solo autore della
     * recensione) inline sulla card invece di bottoni condivisi legati alla
     * selezione della lista.
     */
    private class RecensioneCell extends ListCell<RecensioneDTO> {
        private final Label usernameLabel = new Label();
        private final Label dataLabel = new Label();
        private final HBox intestazione = new HBox(usernameLabel, dataLabel);
        private final Label titoloLabel = new Label();
        private final Label stelleLabel = new Label();
        private final Label testoLabel = new Label();
        private final Label rispostaLabel = new Label();
        private final Button rispondiButton = new Button("Rispondi");
        private final Button modificaButton = new Button("Modifica");
        private final Button eliminaButton = new Button("Elimina");
        private final HBox azioniRow = new HBox(8, rispondiButton, modificaButton, eliminaButton);
        private final VBox contenuto = new VBox(intestazione, titoloLabel, stelleLabel, testoLabel, rispostaLabel, azioniRow);

        {
            contenuto.getStyleClass().add("recensione-riga");
            usernameLabel.getStyleClass().add("risultato-nome");
            usernameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(usernameLabel, javafx.scene.layout.Priority.ALWAYS);
            dataLabel.getStyleClass().add("recensione-data");
            titoloLabel.getStyleClass().add("dettaglio-info");
            stelleLabel.getStyleClass().add("recensione-stelle");
            testoLabel.getStyleClass().add("risultato-info");
            testoLabel.setWrapText(true);
            rispostaLabel.getStyleClass().add("recensione-risposta");
            rispostaLabel.setWrapText(true);
            rispondiButton.getStyleClass().add("bottone-piccolo");
            modificaButton.getStyleClass().add("bottone-piccolo");
            eliminaButton.getStyleClass().add("bottone-piccolo");

            rispondiButton.setOnAction(e -> rispondi(getItem()));
            modificaButton.setOnAction(e -> modifica(getItem()));
            eliminaButton.setOnAction(e -> elimina(getItem()));
        }

        @Override
        protected void updateItem(RecensioneDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                usernameLabel.setText(item.getUsername());
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

                UtenteDTO utente = ServerConnection.getInstance().getUtenteCorrente();
                boolean isProprietario = utente != null && item.getIdUtente() == utente.getIdUtente();
                boolean mostraRispondi = gestore && !haRisposta;
                rispondiButton.setVisible(mostraRispondi);
                rispondiButton.setManaged(mostraRispondi);
                modificaButton.setVisible(isProprietario);
                modificaButton.setManaged(isProprietario);
                eliminaButton.setVisible(isProprietario);
                eliminaButton.setManaged(isProprietario);
                azioniRow.setVisible(mostraRispondi || isProprietario);
                azioniRow.setManaged(mostraRispondi || isProprietario);

                setGraphic(contenuto);
            }
        }
    }

    /**
     * Chiede il testo della risposta alla recensione indicata e la invia al
     * server.
     *
     * @param recensione la recensione a cui rispondere
     */
    private void rispondi(RecensioneDTO recensione) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Scrivi la risposta");
        Optional<String> risposta = dialog.showAndWait();
        risposta.ifPresent(testo -> TaskRunner.run(
            () -> {
                recensioneService.rispondiRecensione(new RispondiRecensioneDTO(recensione.getIdRecensione(), testo));
                return null;
            },
            esito -> caricaRecensioni()
        ));
    }

    /**
     * Naviga alla schermata di ScriviRecensione in modalità modifica,
     * pre-riempita con i valori della recensione indicata.
     *
     * @param recensione la recensione da modificare
     */
    private void modifica(RecensioneDTO recensione) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/scriviRecensione.fxml"));
            Parent root = loader.load();
            ScriviRecensioneController controller = loader.getController();
            controller.impostaRistorante(idRistorante, nomeLabel.getText());
            controller.impostaRisultatiPrecedenti(risultatiPrecedenti);
            controller.impostaRecensioneDaModificare(recensione);
            Stage stage = (Stage) card.getScene().getWindow();
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
}
