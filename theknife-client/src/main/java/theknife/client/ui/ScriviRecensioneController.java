package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    @FXML private Label nomeRistoranteLabel;
    @FXML private TextField titoloField;
    @FXML private TextField stelleField;
    @FXML private TextArea testoField;

    private final RecensioneService recensioneService = new RecensioneService();
    private long idRistorante;
    
    @FXML public void handlePubblica(ActionEvent event) {
        System.out.println("Pubblica cliccato");

        String titolo = titoloField.getText();
        String testo = testoField.getText();
        int stelle = Integer.parseInt(stelleField.getText());

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
                stage.setScene(new Scene(root, 800, 600));
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
            }
        }
    );
    }
    
    @FXML public void handleAnnulla(ActionEvent event) throws IOException {
        System.out.println("Annulla cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
        Parent root = loader.load();
        DettaglioController controller = loader.getController();
        controller.impostaRistorante(idRistorante);
        stage.setScene(new Scene(root, 800, 600));
    }

    public void impostaRistorante(long idRistorante, String nomeRistorante) {
        this.idRistorante = idRistorante;
        nomeRistoranteLabel.setText(nomeRistorante);
    }
}
