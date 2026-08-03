package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    
    @FXML public void handlePubblica(ActionEvent event) throws IOException {
        System.out.println("Pubblica cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
    
    @FXML public void handleAnnulla(ActionEvent event) throws IOException {
        System.out.println("Annulla cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
