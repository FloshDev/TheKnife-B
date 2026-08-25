package theknife.client.ui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import theknife.client.service.RistoranteService;
import theknife.common.dto.CercaRistorantiDTO;

/**
 * Controller della schermata Home (S04).
 *
 * @author Barlera Marco, 760000, VA
 */
public class HomeController {

    /** Contenitore radice, usato solo per togliere il focus dal primo campo all'apertura. */
    @FXML private HBox root;
    /** La card, la cui larghezza è agganciata alla finestra (grafica responsive). */
    @FXML private VBox card;
    /** Controller della sidebar inclusa (fx:include), per evidenziare "Ricerca" come voce attiva. */
    @FXML private SidebarController sidebarController;

    /** Campo di testo per filtrare per nome del ristorante. */
    @FXML private TextField nomeField;
    /** Campo di testo per filtrare per città. */
    @FXML private TextField cittaField;
    /** Campo di testo per filtrare per tipo di cucina. */
    @FXML private TextField tipoCucinaField;
    /** Bottoni della fascia di prezzo (1-4 simboli "€"), selezione singola opzionale. */
    @FXML private ToggleButton prezzo1Radio, prezzo2Radio, prezzo3Radio, prezzo4Radio;
    /** Filtro per i ristoranti che offrono prenotazione online. */
    @FXML private CheckBox prenotazioneOnlineCheck;
    /** Filtro per i ristoranti che offrono consegna a domicilio. */
    @FXML private CheckBox consegnaADomicilioCheck;

    /** Invia al server i comandi di ricerca dei ristoranti. */
    private final RistoranteService ristoranteService = new RistoranteService();

    /**
     * Toglie il focus dal primo campo di testo (altrimenti JavaFX lo assegna
     * automaticamente all'apertura della schermata) ed evidenzia "Ricerca"
     * nella sidebar come voce attiva.
     */
    @FXML private void initialize() {
        Platform.runLater(() -> root.requestFocus());
        Responsive.aggancia(card, 0.6, 480, 700);
        sidebarController.impostaAttivo(SidebarController.Voce.RICERCA);
    }

    /**
     * Restituisce la fascia di prezzo selezionata (1-4), o 0 se nessun
     * bottone è selezionato (nessun filtro sul prezzo).
     *
     * @return la fascia di prezzo selezionata, oppure 0
     */
    private int fasciaPrezzoSelezionata() {
        if (prezzo1Radio.isSelected()) return 1;
        if (prezzo2Radio.isSelected()) return 2;
        if (prezzo3Radio.isSelected()) return 3;
        if (prezzo4Radio.isSelected()) return 4;
        return 0;
    }

    /**
     * Cerca i ristoranti che rispettano i filtri del form e mostra i
     * risultati nella schermata dei risultati.
     *
     * @param event l'evento generato dal click sul bottone di ricerca
     */
    @FXML private void handleCerca(ActionEvent event) {
        String nome = nomeField.getText();
        String citta = cittaField.getText();
        String tipoCucina = tipoCucinaField.getText();
        int fasciaPrezzo = fasciaPrezzoSelezionata();
        // Boolean, non boolean: null vuol dire "nessun filtro" per RistoranteDAO.cerca, non
        // "deve essere false". Checkbox non spuntata = non mi interessa, non "deve NON offrirlo".
        Boolean prenotazioneOnline = prenotazioneOnlineCheck.isSelected() ? Boolean.TRUE : null;
        Boolean consegnaADomicilio = consegnaADomicilioCheck.isSelected() ? Boolean.TRUE : null;

        CercaRistorantiDTO filtri = new CercaRistorantiDTO(
            nome, citta, tipoCucina, prenotazioneOnline, fasciaPrezzo, consegnaADomicilio, null
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        TaskRunner.run(
        () -> ristoranteService.cercaRistoranti(filtri),
        cercaResult -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/risultati.fxml"));
                Parent root = loader.load();
                RisultatiController controller = loader.getController();
                controller.impostaRisultati(cercaResult);
                controller.impostaFiltriRicerca(filtri);
                stage.getScene().setRoot(root);
            } catch (IOException e) {
                Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
            }
        }
        );
    }

    /**
     * Pre-riempie il campo città con la località confermata (o rilevata)
     * nello Splash, così la ricerca parte già filtrata su quella zona invece
     * di richiedere di digitarla di nuovo.
     *
     * @param citta la città da precompilare
     */
    public void impostaCitta(String citta) {
        cittaField.setText(citta);
    }

    /**
     * Precompila il form con i filtri usati nell'ultima ricerca "Cerca",
     * così tornando indietro da Risultati non si riparte da un form vuoto.
     *
     * @param filtri i filtri dell'ultima ricerca, o {@code null} (nessuna precompilazione)
     */
    public void precompilaFiltri(CercaRistorantiDTO filtri) {
        if (filtri == null) {
            return;
        }
        nomeField.setText(filtri.getNome());
        cittaField.setText(filtri.getCitta());
        tipoCucinaField.setText(filtri.getTipoCucina());
        switch (filtri.getFasciaPrezzo()) {
            case 1 -> prezzo1Radio.setSelected(true);
            case 2 -> prezzo2Radio.setSelected(true);
            case 3 -> prezzo3Radio.setSelected(true);
            case 4 -> prezzo4Radio.setSelected(true);
            default -> { }
        }
        prenotazioneOnlineCheck.setSelected(Boolean.TRUE.equals(filtri.isPrenotazioneOnline()));
        consegnaADomicilioCheck.setSelected(Boolean.TRUE.equals(filtri.isConsegnaADomicilio()));
    }
}
