package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * Controller della schermata aggiungi nuovo ristorante (S10).
 *
 * @author Barlera Marco, 760000, VA
 */

public class AggiungiRistoranteController {
    
    @FXML private TextField nomeField;
    @FXML private TextField indirizzoField;
    @FXML private TextField cittaField;
    @FXML private TextField nazioneField;
    @FXML private TextField fasciaPrezzoField;
    @FXML private TextField tipoCucinaField;
    @FXML private TextField webSiteField;
    @FXML private TextField telefonoField;
    @FXML private TextField premiField;
    @FXML private CheckBox prenotazioneOnlineCheck;
    @FXML private CheckBox consegnaADomicilioCheck;

    @FXML private void handleSalva(ActionEvent event) throws IOException {
        System.out.println("Salva cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    @FXML private void handleAnnulla(ActionEvent event) throws IOException {
        System.out.println("Torna indietro cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
