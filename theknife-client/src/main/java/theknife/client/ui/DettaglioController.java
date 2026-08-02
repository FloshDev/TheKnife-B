package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * Controller della schermata di dettaglio (S06).
 *
 * @author Barlera Marco, 760000, VA
 */

public class DettaglioController {
    @FXML private Label nomeLabel;
    @FXML private Label tipoCucinaLabel;
    @FXML private Label indirizzoLabel;
    @FXML private Label fasciaPrezzoLabel;
    @FXML private Label mediaStelleLabel;

    @FXML private Button preferitiButton;
    @FXML private Button scriviRecensioneButton;
    @FXML private Button rispondiRecensioneButton;
    @FXML private Button tornaIndietroButton;
    @FXML private ListView<String> recensioniListView;

    @FXML private void handlePreferiti() {
        System.out.println("Preferiti cliccato");
    }

    @FXML private void handleScriviRecensione() {
        System.out.println("Scrivi recensione cliccato");
    }

    @FXML private void handleRispondiRecensione() {
        System.out.println("Rispondi recensione cliccato");
    }

    @FXML private void handleTornaIndietro(ActionEvent event) throws IOException {
        System.out.println("Torna indietro cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/risultati.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
