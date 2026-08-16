package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import theknife.client.service.RecensioneService;
import theknife.common.dto.AggiungiRecensioneDTO;

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
    /** Campo di testo per il numero di stelle (1-5). */
    @FXML private TextField stelleField;
    /** Area di testo per il corpo della recensione. */
    @FXML private TextArea testoField;

    /** Invia al server la recensione appena scritta. */
    private final RecensioneService recensioneService = new RecensioneService();
    /** Identificativo del ristorante recensito, ricevuto dalla schermata chiamante. */
    private long idRistorante;
    
    /**
     * Pubblica la recensione scritta nel form e, al successo, torna al
     * dettaglio del ristorante con i dati aggiornati.
     *
     * @param event l'evento generato dal click sul bottone "Pubblica"
     */
    @FXML public void handlePubblica(ActionEvent event) {
        String titolo = titoloField.getText();
        String testo = testoField.getText();
        int stelle;
        try {
            stelle = Integer.parseInt(stelleField.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Numero di stelle non valido, inserisci un numero da 1 a 5").showAndWait();
            return;
        }

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
}
