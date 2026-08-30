package theknife.client.ui;

import java.io.IOException;

import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import theknife.client.service.RistoranteService;
import theknife.common.dto.RistoranteDTO;

/**
 * Controller della schermata dashboard ristoratore (S09).
 *
 * @author Barlera Marco, 760000, VA
 */
public class DashboardController {
    /**
     * Costruttore vuoto: tutta l'inizializzazione avviene in {@code initialize()},
     * chiamato da FXMLLoader dopo l'injection dei campi {@code @FXML}.
     */
    public DashboardController() {
    }

    /** Lista dei ristoranti gestiti dall'utente ristoratore, una card verticale per ristorante. */
    @FXML private ListView<RistoranteDTO> ristorantiGestiti;
    /** Controller della sidebar inclusa (fx:include), per evidenziare "Dashboard" come voce attiva. */
    @FXML private SidebarController sidebarController;
    /** La riga con lista e mappa affiancate, per vincolarne la larghezza esattamente a metà ciascuna. */
    @FXML private HBox rigaContenuto;
    /** Il nodo radice del componente mappa incluso (fx:include), per agganciarne la larghezza. */
    @FXML private Region mappa;
    /** Controller del componente mappa incluso (fx:include, decisione 34). */
    @FXML private MappaController mappaController;

    /** Invia al server i comandi sui ristoranti gestiti dall'utente. */
    private final RistoranteService ristoranteService = new RistoranteService();

    /**
     * Evidenzia "Dashboard" nella sidebar e carica i ristoranti gestiti
     * dall'utente ristoratore all'apertura della schermata.
     */
    @FXML private void initialize() {
        sidebarController.impostaAttivo(SidebarController.Voce.DASHBOARD);
        // Vincolo esplicito a metà larghezza ciascuno, invece di affidarsi a hgrow +
        // dimensioni preferite dei due nodi (che non coincidono mai da sole).
        DoubleBinding metaLarghezza = rigaContenuto.widthProperty().subtract(24).divide(2);
        ristorantiGestiti.prefWidthProperty().bind(metaLarghezza);
        mappa.prefWidthProperty().bind(metaLarghezza);
        Label nessunRistorante = new Label("Non gestisci ancora nessun ristorante.");
        nessunRistorante.getStyleClass().add("risultato-info");
        nessunRistorante.setWrapText(true);
        nessunRistorante.setAlignment(Pos.CENTER);
        nessunRistorante.setTextAlignment(TextAlignment.CENTER);
        nessunRistorante.prefWidthProperty().bind(ristorantiGestiti.widthProperty().subtract(40));
        ristorantiGestiti.setPlaceholder(nessunRistorante);
        ristorantiGestiti.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                apriDettaglio((Stage) ristorantiGestiti.getScene().getWindow(), ristorantiGestiti.getSelectionModel().getSelectedItem());
            }
        });
        TaskRunner.run(
            () -> ristoranteService.vediRistorantiGestiti(),
            ristoranti -> {
                ristorantiGestiti.getItems().setAll(ristoranti);
                ristorantiGestiti.setCellFactory(lv -> new RistoranteGestitoCell());

                mappaController.impostaRistoranti(ristorantiGestiti.getItems());
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
        return ristorantiGestiti.getItems().stream()
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
            ristorantiGestiti.getSelectionModel().select(ristorante);
            ristorantiGestiti.scrollTo(ristorante);
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
            controller.impostaProvenienzaDashboard();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
        }
    }

    /**
     * Cella della lista ristoranti gestiti: card verticale (nome+prezzo,
     * indirizzo/città, valutazione, freccia per il dettaglio) — stesso stile
     * di {@code RisultatiController.RisultatoCell}, senza cuore preferiti
     * (qui i ristoranti sono quelli gestiti, non salvabili come preferiti).
     */
    private class RistoranteGestitoCell extends ListCell<RistoranteDTO> {
        /** Costruttore vuoto: i nodi grafici della cella sono creati inline nei campi, la logica sta in {@code updateItem}. */
        RistoranteGestitoCell() {
        }

        /** Nome del ristorante. */
        private final Label nomeLabel = new Label();
        /** Fascia di prezzo. */
        private final Label prezzoLabel = new Label();
        /** Città e tipo di cucina. */
        private final Label infoLabel = new Label();
        /** Valutazione media in stelle. */
        private final Label ratingLabel = new Label();
        /** Icona a forma di freccia, apre il dettaglio. */
        private final SVGPath frecciaIcon = new SVGPath();
        /** Bottone per aprire il dettaglio del ristorante. */
        private final Button frecciaButton = new Button();
        /** Riga col solo bottone freccia. */
        private final HBox azioniRow = new HBox(frecciaButton);
        /** Contenitore radice della cella. */
        private final VBox contenuto = new VBox(8,
            new HBox(8, nomeLabel, prezzoLabel), infoLabel, ratingLabel, azioniRow);

        {
            contenuto.getStyleClass().add("risultato-card");
            nomeLabel.getStyleClass().add("risultato-nome");
            prezzoLabel.getStyleClass().add("badge-prezzo");
            infoLabel.getStyleClass().add("risultato-info");
            infoLabel.setWrapText(true);
            infoLabel.prefWidthProperty().bind(ristorantiGestiti.widthProperty().subtract(60));
            ratingLabel.getStyleClass().add("badge-rating");
            azioniRow.setAlignment(Pos.CENTER_RIGHT);

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
                infoLabel.setText(item.getIndirizzo() + ", " + item.getCitta());
                ratingLabel.setText(String.format("★%.1f · %d recensioni", item.getMediaStelle(), item.getNumeroRecensioni()));
                setGraphic(contenuto);
            }
        }
    }
}
