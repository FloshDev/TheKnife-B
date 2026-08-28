package theknife.client.ui;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.binding.DoubleBinding;
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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import theknife.client.network.ServerConnection;
import theknife.client.service.RistoranteService;
import theknife.common.dto.CercaRistorantiDTO;
import theknife.common.dto.CercaVicinoDTO;
import theknife.common.dto.IdRistoranteDTO;
import theknife.common.dto.RistoranteDTO;
import theknife.common.dto.UtenteDTO;
import theknife.common.enums.Ruolo;

/**
 * Controller della schermata risultati (S05).
 *
 * @author Barlera Marco, 760000, VA
 */

public class RisultatiController {
    /**
     * Costruttore vuoto: tutta l'inizializzazione avviene in {@code initialize()},
     * chiamato da FXMLLoader dopo l'injection dei campi {@code @FXML}.
     */
    public RisultatiController() {
    }

    /** Titolo con il conteggio dei ristoranti trovati ("Risultati (N)"). */
    @FXML private Label risultatiLabel;
    /** Lista dei ristoranti trovati dalla ricerca, una card per risultato. */
    @FXML private ListView<RistoranteDTO> risultatiListView;
    /** Bottone icona per tornare alla schermata Home. */
    @FXML private Button tornaIndietroButton;
    /** Controller della sidebar inclusa (fx:include), per evidenziare "Ricerca" come voce attiva. */
    @FXML private SidebarController sidebarController;
    /** La riga con lista e mappa affiancate, per vincolarne la larghezza esattamente a metà ciascuna. */
    @FXML private HBox rigaContenuto;
    /** Il nodo radice del componente mappa incluso (fx:include), per agganciarne la larghezza. */
    @FXML private Region mappa;
    /** Controller del componente mappa incluso (fx:include, decisione 34). */
    @FXML private MappaController mappaController;

    /** Invia al server i comandi sui preferiti, usato dal cuore su ogni card. */
    private final RistoranteService ristoranteService = new RistoranteService();
    /**
     * Filtri di "Cerca" usati per ottenere questa lista, se arrivata da lì — {@code null} se
     * arrivata da "Vicino a me". Ripassati a Home da {@link #handleTornaIndietro(ActionEvent)}
     * così il form non riparte vuoto.
     */
    private CercaRistorantiDTO filtriRicerca;
    /**
     * Filtri di "Vicino a me" usati per ottenere questa lista, se arrivata da lì — {@code null}
     * se arrivata da "Cerca". Stesso scopo di {@link #filtriRicerca}.
     */
    private CercaVicinoDTO filtriVicino;
    /** Se l'utente corrente è un Cliente: solo lui vede/usa il cuore preferiti (RF08/RF09). */
    private boolean cliente;
    /** Gli id dei ristoranti già nei preferiti dell'utente corrente, per pre-colorare i cuori. */
    private final Set<Long> preferitiIds = new HashSet<>();

    /**
     * Evidenzia "Ricerca" nella sidebar (i risultati fanno parte del flusso
     * di ricerca) e registra il doppio click come scorciatoia per aprire il
     * dettaglio, in aggiunta al bottone "chevron" su ogni card.
     */
    @FXML private void initialize() {
        sidebarController.impostaAttivo(SidebarController.Voce.RICERCA);
        // Vincolo esplicito a metà larghezza ciascuno, invece di affidarsi a hgrow +
        // dimensioni preferite dei due nodi (che non coincidono mai da sole).
        DoubleBinding metaLarghezza = rigaContenuto.widthProperty().subtract(24).divide(2);
        risultatiListView.prefWidthProperty().bind(metaLarghezza);
        mappa.prefWidthProperty().bind(metaLarghezza);
        risultatiListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                apriDettaglio((Stage) risultatiListView.getScene().getWindow(), risultatiListView.getSelectionModel().getSelectedItem());
            }
        });
    }

    /**
     * Naviga alla schermata da cui è arrivata la ricerca corrente — Home se
     * da "Cerca", "Vicino a me" se da lì (schermate separate, S52).
     *
     * @param event l'evento generato dal click sul bottone "Torna indietro"
     */
    @FXML private void handleTornaIndietro(ActionEvent event) {
        tornaIndietro((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * Naviga alla schermata da cui è arrivata la ricerca corrente,
     * ripassando i filtri usati così il form non riparte vuoto — stessa
     * logica sia per il bottone dedicato che per i link "Ricerca"/"Vicino a
     * me" della sidebar quando cliccati da qui (altrimenti resettavano i
     * filtri, S62).
     *
     * @param stage la finestra su cui sostituire la schermata
     */
    private void tornaIndietro(Stage stage) {
        try {
            if (filtriVicino != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/vicinoAme.fxml"));
                Parent root = loader.load();
                VicinoAMeController controller = loader.getController();
                controller.precompilaFiltri(filtriVicino);
                stage.getScene().setRoot(root);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/home.fxml"));
                Parent root = loader.load();
                HomeController controller = loader.getController();
                controller.precompilaFiltri(filtriRicerca);
                stage.getScene().setRoot(root);
            }
        } catch (IOException e) {
            Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
        }
    }

    /**
     * Registra i filtri di "Cerca" usati per la lista corrente, per poterli
     * ripassare a Home se si torna indietro (invece di far ripartire il form
     * vuoto), ed evidenzia "Ricerca" nella sidebar.
     *
     * @param filtri i filtri usati, o {@code null}
     */
    public void impostaFiltriRicerca(CercaRistorantiDTO filtri) {
        this.filtriRicerca = filtri;
        this.filtriVicino = null;
        sidebarController.impostaAttivo(SidebarController.Voce.RICERCA);
        sidebarController.impostaAzioneRicerca(() -> tornaIndietro((Stage) risultatiListView.getScene().getWindow()));
    }

    /**
     * Registra i filtri di "Vicino a me" usati per la lista corrente, stesso
     * scopo di {@link #impostaFiltriRicerca(CercaRistorantiDTO)}, ed
     * evidenzia "Vicino a me" nella sidebar invece di "Ricerca".
     *
     * @param filtri i filtri usati, o {@code null}
     */
    public void impostaFiltriVicino(CercaVicinoDTO filtri) {
        this.filtriVicino = filtri;
        this.filtriRicerca = null;
        sidebarController.impostaAttivo(SidebarController.Voce.VICINO_A_ME);
        sidebarController.impostaAzioneVicinoAMe(() -> tornaIndietro((Stage) risultatiListView.getScene().getWindow()));
    }

    /**
     * Popola la lista dei risultati con i ristoranti ricevuti e imposta il
     * cell factory che mostra ogni ristorante come card (nome, fascia di
     * prezzo, cucina/città, valutazione, servizi offerti) con cuore
     * preferiti e freccia per il dettaglio. Se l'utente corrente è un
     * Cliente, carica anche i suoi preferiti per pre-colorare i cuori.
     *
     * @param risultati i ristoranti da mostrare, o {@code null} per svuotare
     *                   la lista (es. si arriva a Dettaglio da una schermata
     *                   senza una ricerca precedente, S40)
     */
    public void impostaRisultati(List<RistoranteDTO> risultati) {
        Label nessunRisultato = new Label("Nessun ristorante trovato con questi filtri.");
        nessunRisultato.getStyleClass().add("risultato-info");
        nessunRisultato.setWrapText(true);
        nessunRisultato.prefWidthProperty().bind(risultatiListView.widthProperty().subtract(40));
        risultatiListView.setPlaceholder(nessunRisultato);

        risultatiListView.getItems().setAll(risultati == null ? List.of() : risultati);
        risultatiLabel.setText("Risultati (" + risultatiListView.getItems().size() + ")");

        mappaController.impostaRistoranti(risultatiListView.getItems());
        mappaController.setOnMarkerClick(this::evidenziaCard);

        UtenteDTO utente = ServerConnection.getInstance().getUtenteCorrente();
        cliente = utente != null && utente.getRuolo() == Ruolo.CLIENTE;
        preferitiIds.clear();
        if (cliente) {
            TaskRunner.run(
                () -> ristoranteService.ottieniPreferiti(),
                preferiti -> {
                    preferiti.forEach(r -> preferitiIds.add(r.getIdRistorante()));
                    risultatiListView.refresh();
                }
            );
        }

        risultatiListView.setCellFactory(lv -> new RisultatoCell());
    }

    /**
     * Aggiunge o rimuove un ristorante dai preferiti dell'utente corrente
     * (RF08/RF09), aggiornando {@link #preferitiIds} e ridisegnando la
     * lista per riflettere subito il nuovo stato del cuore.
     *
     * @param ristorante il ristorante da aggiungere o rimuovere dai preferiti
     */
    private void toggleFavorito(RistoranteDTO ristorante) {
        boolean giaPreferito = preferitiIds.contains(ristorante.getIdRistorante());
        TaskRunner.run(
            () -> {
                if (giaPreferito) {
                    ristoranteService.rimuoviPreferito(new IdRistoranteDTO(ristorante.getIdRistorante()));
                } else {
                    ristoranteService.aggiungiPreferito(new IdRistoranteDTO(ristorante.getIdRistorante()));
                }
                return null;
            },
            esito -> {
                if (giaPreferito) {
                    preferitiIds.remove(ristorante.getIdRistorante());
                    Toast.avviso("Ristorante rimosso dai preferiti.");
                } else {
                    preferitiIds.add(ristorante.getIdRistorante());
                    Toast.successo("Ristorante aggiunto ai preferiti.");
                }
                risultatiListView.refresh();
            }
        );
    }

    /**
     * Cerca nella lista corrente il ristorante con l'id indicato, ricevuto
     * grezzo dal click su un marker della mappa (il componente mappa non
     * conosce i DTO, solo id/lat/long che gli passiamo).
     *
     * @param id l'identificativo del ristorante cliccato sulla mappa
     * @return il ristorante corrispondente, o {@code null} se non trovato
     */
    private RistoranteDTO trovaRistorante(long id) {
        return risultatiListView.getItems().stream()
            .filter(r -> r.getIdRistorante() == id)
            .findFirst()
            .orElse(null);
    }

    /**
     * Seleziona e mostra nella lista il ristorante corrispondente al marker
     * cliccato sulla mappa — collega il click su un marker alla sua card,
     * senza navigare altrove (il click sulla freccia resta l'unico modo per
     * aprire il dettaglio da qui).
     *
     * @param idRistorante l'identificativo del ristorante il cui marker è stato cliccato
     */
    private void evidenziaCard(long idRistorante) {
        RistoranteDTO ristorante = trovaRistorante(idRistorante);
        if (ristorante != null) {
            risultatiListView.getSelectionModel().select(ristorante);
            risultatiListView.scrollTo(ristorante);
        }
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
            controller.impostaRisultatiPrecedenti(risultatiListView.getItems());
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
        }
    }

    /**
     * Cella della lista risultati: mostra un ristorante come card (nome +
     * fascia di prezzo, cucina/città, valutazione, tag dei servizi offerti,
     * cuore preferiti e freccia per il dettaglio).
     */
    private class RisultatoCell extends ListCell<RistoranteDTO> {
        /** Costruttore vuoto: i nodi grafici della cella sono creati inline nei campi, la logica sta in {@code updateItem}. */
        RisultatoCell() {
        }

        /** Nome del ristorante. */
        private final Label nomeLabel = new Label();
        /** Fascia di prezzo. */
        private final Label prezzoLabel = new Label();
        /** Città e tipo di cucina. */
        private final Label infoLabel = new Label();
        /** Valutazione media in stelle. */
        private final Label ratingLabel = new Label();
        /** Etichetta mostrata solo se il ristorante offre la prenotazione online. */
        private final Label prenotazioneTag = new Label("Prenotazione online");
        /** Etichetta mostrata solo se il ristorante offre la consegna a domicilio. */
        private final Label consegnaTag = new Label("Consegna a domicilio");
        /** Riga con le etichette dei servizi offerti. */
        private final HBox tagRow = new HBox(8, prenotazioneTag, consegnaTag);
        /** Icona a forma di cuore, aggiunta/rimossa dai preferiti. */
        private final SVGPath cuoreIcon = new SVGPath();
        /** Bottone per aggiungere/rimuovere il ristorante dai preferiti. */
        private final Button cuoreButton = new Button();
        /** Icona a forma di freccia, apre il dettaglio. */
        private final SVGPath frecciaIcon = new SVGPath();
        /** Bottone per aprire il dettaglio del ristorante. */
        private final Button frecciaButton = new Button();
        /** Riga con i bottoni cuore e freccia. */
        private final HBox azioniRow = new HBox(8, cuoreButton, frecciaButton);
        /** Contenitore radice della cella. */
        private final VBox contenuto = new VBox(8,
            new HBox(8, nomeLabel, prezzoLabel), infoLabel, ratingLabel, tagRow, azioniRow);

        {
            contenuto.getStyleClass().add("risultato-card");
            nomeLabel.getStyleClass().add("risultato-nome");
            prezzoLabel.getStyleClass().add("badge-prezzo");
            infoLabel.getStyleClass().add("risultato-info");
            infoLabel.setWrapText(true);
            ratingLabel.getStyleClass().add("badge-rating");
            prenotazioneTag.getStyleClass().add("tag-feature");
            consegnaTag.getStyleClass().add("tag-feature");
            azioniRow.setAlignment(Pos.CENTER);

            cuoreIcon.setContent("M2 9.5a5.5 5.5 0 0 1 9.591-3.676.56.56 0 0 0 .818 0A5.49 5.49 0 0 1 22 9.5c0 2.29-1.5 4-3 5.5l-5.492 5.313a2 2 0 0 1-3 .019L5 15c-1.5-1.5-3-3.2-3-5.5");
            cuoreIcon.setStroke(Color.web("#FA812F"));
            cuoreIcon.setStrokeWidth(1.5);
            cuoreIcon.setScaleX(0.6);
            cuoreIcon.setScaleY(0.6);
            cuoreButton.setGraphic(cuoreIcon);
            cuoreButton.getStyleClass().add("bottone-icona");
            cuoreButton.setOnAction(e -> toggleFavorito(getItem()));

            frecciaIcon.setContent("M9 18l6-6-6-6");
            frecciaIcon.setFill(Color.TRANSPARENT);
            frecciaIcon.setStroke(Color.web("#FA812F"));
            frecciaIcon.setStrokeWidth(2);
            frecciaButton.setGraphic(frecciaIcon);
            frecciaButton.getStyleClass().add("bottone-icona");
            frecciaButton.setOnAction(e -> apriDettaglio((Stage) frecciaButton.getScene().getWindow(), getItem()));

            contenuto.setOnMouseClicked(e -> {
                if (getItem() != null) {
                    mappaController.evidenziaMarker(getItem().getIdRistorante());
                }
            });
        }

        @Override
        protected void updateItem(RistoranteDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                nomeLabel.setText(item.getNome());
                prezzoLabel.setText("€".repeat(item.getFasciaPrezzo()));
                infoLabel.setText(CucinaFormatter.italiano(item.getTipoCucina()) + " · " + item.getCitta());
                ratingLabel.setText(String.format("★%.1f · %d recensioni", item.getMediaStelle(), item.getNumeroRecensioni()));

                prenotazioneTag.setVisible(item.isPrenotazioneOnline());
                prenotazioneTag.setManaged(item.isPrenotazioneOnline());
                consegnaTag.setVisible(item.isConsegnaADomicilio());
                consegnaTag.setManaged(item.isConsegnaADomicilio());
                tagRow.setVisible(item.isPrenotazioneOnline() || item.isConsegnaADomicilio());
                tagRow.setManaged(item.isPrenotazioneOnline() || item.isConsegnaADomicilio());

                cuoreButton.setVisible(cliente);
                cuoreButton.setManaged(cliente);
                boolean preferito = preferitiIds.contains(item.getIdRistorante());
                cuoreIcon.setFill(preferito ? Color.web("#FAB12F") : Color.TRANSPARENT);

                setGraphic(contenuto);
            }
        }
    }
}
