package theknife.client.ui;

import java.io.IOException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import theknife.client.service.RecensioneService;
import theknife.common.dto.AggiungiRecensioneDTO;
import theknife.common.dto.RistoranteDTO;

/**
 * Controller della schermata recensione (S07).
 *
 * @author Barlera Marco, 760000, VA
 */

public class ScriviRecensioneController {

    /** Nome del ristorante che si sta recensendo. */
    @FXML private Label nomeRistoranteLabel;
    /** Campo di testo per il titolo della recensione. */
    @FXML private TextField titoloField;
    /** Contenitore delle 5 stelle cliccabili, costruite in {@link #initialize()}. */
    @FXML private HBox stelleBox;
    /** Area di testo per il corpo della recensione. */
    @FXML private TextArea testoField;

    /** Invia al server la recensione appena scritta. */
    private final RecensioneService recensioneService = new RecensioneService();
    /** Identificativo del ristorante recensito, ricevuto dalla schermata chiamante. */
    private long idRistorante;
    /** Lista di ristoranti mostrata da Risultati prima di aprire il dettaglio di provenienza. */
    private List<RistoranteDTO> risultatiPrecedenti;
    /** Numero di stelle scelto dall'utente (0 = nessuna selezione ancora). */
    private int stelleSelezionate = 0;
    
    /**
     * Costruisce le 5 stelle cliccabili in {@code stelleBox}: ognuna, al
     * click, imposta {@link #stelleSelezionate} al proprio voto (1-5) e
     * ridisegna il riempimento di tutte e cinque.
     */
    @FXML private void initialize() {
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
     * Pubblica la recensione scritta nel form e, al successo, torna al
     * dettaglio del ristorante con i dati aggiornati.
     *
     * @param event l'evento generato dal click sul bottone "Pubblica"
     */
    @FXML public void handlePubblica(ActionEvent event) {
        String titolo = titoloField.getText();
        String testo = testoField.getText();
        if (stelleSelezionate == 0) {
            new Alert(Alert.AlertType.ERROR, "Seleziona un voto da 1 a 5 stelle").showAndWait();
            return;
        }
        int stelle = stelleSelezionate;

        AggiungiRecensioneDTO recensioneDTO = new AggiungiRecensioneDTO(idRistorante, titolo, testo, stelle);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        
        TaskRunner.run(
        () -> {recensioneService.aggiungiRecensione(recensioneDTO); return null;},
        recensionePubblicata -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
                Parent root = loader.load();
                DettaglioController controller = loader.getController();
                controller.impostaRistorante(idRistorante);
                controller.impostaRisultatiPrecedenti(risultatiPrecedenti);
                stage.getScene().setRoot(root);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
            }
        }
    );
    }
    
    /**
     * Torna al dettaglio del ristorante senza pubblicare la recensione.
     *
     * @param event l'evento generato dal click sul bottone "Annulla"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML public void handleAnnulla(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
        Parent root = loader.load();
        DettaglioController controller = loader.getController();
        controller.impostaRistorante(idRistorante);
        controller.impostaRisultatiPrecedenti(risultatiPrecedenti);
        stage.getScene().setRoot(root);
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
}
