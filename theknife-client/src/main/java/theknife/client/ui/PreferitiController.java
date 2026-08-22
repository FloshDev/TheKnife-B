package theknife.client.ui;

import java.io.IOException;

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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.web.WebView;
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

    /** Titolo con il conteggio dei preferiti ("Preferiti (N)"). */
    @FXML private Label preferitiLabel;
    /** Lista dei ristoranti preferiti, una card per ristorante (stesso stile di Risultati). */
    @FXML private ListView<RistoranteDTO> preferitiListView;
    /** Bottone icona per tornare alla schermata Home. */
    @FXML private Button tornaIndietroButton;
    /** Controller della sidebar inclusa (fx:include), per evidenziare "Preferiti" come voce attiva. */
    @FXML private SidebarController sidebarController;
    /** La riga con lista e mappa affiancate, per vincolarne la larghezza esattamente a metà ciascuna. */
    @FXML private HBox rigaContenuto;
    /** Il nodo radice del componente mappa incluso (fx:include), per agganciarne la larghezza. */
    @FXML private WebView mappa;
    /** Controller del componente mappa incluso (fx:include, decisione 34). */
    @FXML private MappaController mappaController;

    /** Invia al server i comandi sui ristoranti preferiti dell'utente. */
    private final RistoranteService ristoranteService = new RistoranteService();

    /**
     * Evidenzia "Preferiti" nella sidebar, registra il doppio click come
     * scorciatoia per aprire il dettaglio e carica i preferiti all'apertura
     * della schermata.
     */
    @FXML private void initialize() {
        sidebarController.impostaAttivo(SidebarController.Voce.PREFERITI);
        // Vincolo esplicito a metà larghezza ciascuno, invece di affidarsi a hgrow +
        // dimensioni preferite di ListView/WebView (che non coincidono mai da sole:
        // WebView parte da un default enorme, ListView da uno piccolo).
        DoubleBinding metaLarghezza = rigaContenuto.widthProperty().subtract(24).divide(2);
        preferitiListView.prefWidthProperty().bind(metaLarghezza);
        mappa.prefWidthProperty().bind(metaLarghezza);
        preferitiListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                apriDettaglio((Stage) preferitiListView.getScene().getWindow(), preferitiListView.getSelectionModel().getSelectedItem());
            }
        });
        caricaPreferiti();
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
        Label nessunPreferito = new Label("Nessun preferito ancora. Vai alla ricerca e salva un ristorante che ti piace.");
        nessunPreferito.getStyleClass().add("risultato-info");
        preferitiListView.setPlaceholder(nessunPreferito);

        TaskRunner.run(
            () -> ristoranteService.ottieniPreferiti(),
            ristoranti -> {
                preferitiListView.getItems().setAll(ristoranti);
                preferitiLabel.setText("Preferiti (" + ristoranti.size() + ")");
                preferitiListView.setCellFactory(lv -> new PreferitoCell());

                mappaController.impostaRistoranti(preferitiListView.getItems());
                mappaController.setOnMarkerClick(this::evidenziaCard);
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
        return preferitiListView.getItems().stream()
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
            preferitiListView.getSelectionModel().select(ristorante);
            preferitiListView.scrollTo(ristorante);
        }
    }

    /**
     * Rimuove un ristorante dai preferiti e ricarica la lista.
     *
     * @param ristorante il ristorante da rimuovere dai preferiti
     */
    private void rimuoviPreferito(RistoranteDTO ristorante) {
        TaskRunner.run(
            () -> { ristoranteService.rimuoviPreferito(new IdRistoranteDTO(ristorante.getIdRistorante()));
                    return null;
                },
            esito -> {
                caricaPreferiti();
                Toast.avviso("Ristorante rimosso dai preferiti");
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
            controller.impostaProvenienzaPreferiti();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
        }
    }

    /**
     * Cella della lista preferiti: mostra un ristorante come card (nome,
     * fascia di prezzo, cucina/città, valutazione, servizi offerti) con
     * cuore sempre pieno (rimuove dai preferiti al click) e freccia per il
     * dettaglio — stesso stile di {@code RisultatiController.RisultatoCell}.
     */
    private class PreferitoCell extends ListCell<RistoranteDTO> {
        private final Label nomeLabel = new Label();
        private final Label prezzoLabel = new Label();
        private final Label infoLabel = new Label();
        private final Label ratingLabel = new Label();
        private final Label prenotazioneTag = new Label("Prenotazione online");
        private final Label consegnaTag = new Label("Consegna a domicilio");
        private final HBox tagRow = new HBox(8, prenotazioneTag, consegnaTag);
        private final SVGPath cuoreIcon = new SVGPath();
        private final Button cuoreButton = new Button();
        private final SVGPath frecciaIcon = new SVGPath();
        private final Button frecciaButton = new Button();
        private final HBox azioniRow = new HBox(8, cuoreButton, frecciaButton);
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
            cuoreIcon.setFill(Color.web("#FAB12F"));
            cuoreIcon.setScaleX(0.6);
            cuoreIcon.setScaleY(0.6);
            cuoreButton.setGraphic(cuoreIcon);
            cuoreButton.getStyleClass().add("bottone-icona");
            cuoreButton.setOnAction(e -> rimuoviPreferito(getItem()));

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
                infoLabel.setText(item.getTipoCucina() + " · " + item.getCitta());
                ratingLabel.setText(String.format("★%.1f · %d recensioni", item.getMediaStelle(), item.getNumeroRecensioni()));

                prenotazioneTag.setVisible(item.isPrenotazioneOnline());
                prenotazioneTag.setManaged(item.isPrenotazioneOnline());
                consegnaTag.setVisible(item.isConsegnaADomicilio());
                consegnaTag.setManaged(item.isConsegnaADomicilio());
                tagRow.setVisible(item.isPrenotazioneOnline() || item.isConsegnaADomicilio());
                tagRow.setManaged(item.isPrenotazioneOnline() || item.isConsegnaADomicilio());

                setGraphic(contenuto);
            }
        }
    }
}
