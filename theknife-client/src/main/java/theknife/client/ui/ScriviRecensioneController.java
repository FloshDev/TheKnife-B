package theknife.client.ui;

import java.io.IOException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import theknife.client.service.RecensioneService;
import theknife.common.dto.AggiungiRecensioneDTO;
import theknife.common.dto.ModificaRecensioneDTO;
import theknife.common.dto.RecensioneDTO;
import theknife.common.dto.RistoranteDTO;

/**
 * Controller della schermata recensione (S07).
 *
 * @author Barlera Marco, 760000, VA
 */

public class ScriviRecensioneController {

    /** La card, la cui larghezza è agganciata alla finestra (grafica responsive). */
    @FXML private VBox card;
    /** Titolo della schermata: "Scrivi una recensione" o, in modifica, "Modifica recensione". */
    @FXML private Label titoloSchermataLabel;
    /** Nome del ristorante che si sta recensendo. */
    @FXML private Label nomeRistoranteLabel;
    /** Campo di testo per il titolo della recensione. */
    @FXML private TextField titoloField;
    /** Contenitore delle 5 stelle cliccabili, costruite in {@link #initialize()}. */
    @FXML private HBox stelleBox;
    /** Area di testo per il corpo della recensione. */
    @FXML private TextArea testoField;
    /** Bottone di conferma: "Pubblica" o, in modifica, "Salva modifiche". */
    @FXML private Button pubblicaButton;
    /** Controller della sidebar inclusa (fx:include), per evidenziare "Ricerca" come voce attiva. */
    @FXML private SidebarController sidebarController;

    /** Invia al server la recensione appena scritta o modificata. */
    private final RecensioneService recensioneService = new RecensioneService();
    /** Identificativo del ristorante recensito, ricevuto dalla schermata chiamante. */
    private long idRistorante;
    /** Lista di ristoranti mostrata da Risultati prima di aprire il dettaglio di provenienza. */
    private List<RistoranteDTO> risultatiPrecedenti;
    /** Numero di stelle scelto dall'utente (0 = nessuna selezione ancora). */
    private int stelleSelezionate = 0;
    /**
     * La recensione da modificare, se la schermata è stata aperta in modalità
     * modifica da {@link #impostaRecensioneDaModificare(RecensioneDTO)}, o
     * {@code null} se si sta scrivendone una nuova.
     */
    private RecensioneDTO recensioneDaModificare;
    
    /**
     * Costruisce le 5 stelle cliccabili in {@code stelleBox}: ognuna, al
     * click, imposta {@link #stelleSelezionate} al proprio voto (1-5) e
     * ridisegna il riempimento di tutte e cinque.
     */
    @FXML private void initialize() {
        Responsive.aggancia(card, 0.45, 380, 560);
        sidebarController.impostaAttivo(SidebarController.Voce.RICERCA);
        for (int i = 1; i <= 5; i++) {
            Label stella = new Label("★");
            stella.getStyleClass().add("stella");
            int voto = i;
            stella.setOnMouseClicked(e -> {
                stelleSelezionate = voto;
                aggiornaStelle();
            });
            stelleBox.getChildren().add(stella);
        }
    }

    /**
     * Pubblica la recensione scritta nel form, o salva le modifiche se la
     * schermata è in modalità modifica ({@link #recensioneDaModificare}), e
     * al successo torna al dettaglio del ristorante con i dati aggiornati.
     *
     * @param event l'evento generato dal click sul bottone "Pubblica"/"Salva modifiche"
     */
    @FXML public void handlePubblica(ActionEvent event) {
        String titolo = titoloField.getText();
        String testo = testoField.getText();
        if (stelleSelezionate == 0) {
            Toast.errore("Seleziona un voto da 1 a 5 stelle");
            return;
        }
        int stelle = stelleSelezionate;

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (recensioneDaModificare != null) {
            ModificaRecensioneDTO modificaDTO = new ModificaRecensioneDTO(recensioneDaModificare.getIdRecensione(), titolo, testo, stelle);
            TaskRunner.run(
                () -> { recensioneService.modificaRecensione(modificaDTO); return null; },
                esito -> tornaAlDettaglio(stage)
            );
        } else {
            AggiungiRecensioneDTO recensioneDTO = new AggiungiRecensioneDTO(idRistorante, titolo, testo, stelle);
            TaskRunner.run(
                () -> { recensioneService.aggiungiRecensione(recensioneDTO); return null; },
                esito -> tornaAlDettaglio(stage)
            );
        }
    }

    /**
     * Torna al dettaglio del ristorante senza salvare.
     *
     * @param event l'evento generato dal click sul bottone "Annulla"
     */
    @FXML public void handleAnnulla(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tornaAlDettaglio(stage);
    }

    /**
     * Riceve l'identificativo del ristorante da recensire e il suo nome, da
     * mostrare nella schermata.
     *
     * @param idRistorante l'identificativo del ristorante da recensire
     * @param nomeRistorante il nome del ristorante, mostrato nella schermata
     */
    public void impostaRistorante(long idRistorante, String nomeRistorante) {
        this.idRistorante = idRistorante;
        nomeRistoranteLabel.setText(nomeRistorante);
    }

    /**
     * Registra la lista di ristoranti da cui si arriva, per ripassarla a
     * Dettaglio quando si pubblica o si annulla.
     *
     * @param risultati i ristoranti mostrati dalla schermata di provenienza
     */
    public void impostaRisultatiPrecedenti(List<RistoranteDTO> risultati) {
        this.risultatiPrecedenti = risultati;
    }

    /**
     * Mette la schermata in modalità modifica: pre-riempie titolo, stelle e
     * testo con i valori della recensione indicata, cambia il titolo della
     * schermata e il testo del bottone di conferma. Da chiamare dopo
     * {@link #impostaRistorante(long, String)}, che resta invariato — id e
     * nome del ristorante servono comunque per tornare al dettaglio giusto.
     *
     * @param recensione la recensione da modificare
     */
    public void impostaRecensioneDaModificare(RecensioneDTO recensione) {
        this.recensioneDaModificare = recensione;
        titoloSchermataLabel.setText("Modifica recensione");
        pubblicaButton.setText("Salva modifiche");
        titoloField.setText(recensione.getTitolo());
        testoField.setText(recensione.getTesto());
        stelleSelezionate = recensione.getStelle();
        aggiornaStelle();
    }

    /**
     * Ridisegna il riempimento delle 5 stelle in base a
     * {@link #stelleSelezionate}: piene fino al voto scelto, vuote dopo.
     */
    private void aggiornaStelle() {
        for (int i = 0; i < stelleBox.getChildren().size(); i++) {
            boolean piena = i < stelleSelezionate;
            stelleBox.getChildren().get(i).getStyleClass().removeAll("stella-piena");
            if (piena) {
                stelleBox.getChildren().get(i).getStyleClass().add("stella-piena");
            }
        }
    }

    /**
     * Torna al dettaglio del ristorante corrente, con la lista di
     * provenienza ripassata come sempre (S40) — usato sia da
     * {@link #handlePubblica(ActionEvent)} al successo sia da
     * {@link #handleAnnulla(ActionEvent)}.
     *
     * @param stage la finestra su cui sostituire la schermata
     */
    private void tornaAlDettaglio(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
            Parent root = loader.load();
            DettaglioController controller = loader.getController();
            controller.impostaRistorante(idRistorante);
            controller.impostaRisultatiPrecedenti(risultatiPrecedenti);
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
        }
    }
}
