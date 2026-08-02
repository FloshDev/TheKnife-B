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
 * Controller della schermata risultati (S05).
 *
 * @author Barlera Marco, 760000, VA
 */

public class RisultatiController {
    @FXML private Label risultatiLabel;
    @FXML private ListView<String> risultatiListView;
    @FXML private Button dettaglioButton;
    @FXML private Button tornaIndietroButton;
    
    @FXML private void handleDettaglio() {
        System.out.println("Dettaglio cliccato");
    }
    
    @FXML private void handleTornaIndietro(ActionEvent event) throws IOException {
        System.out.println("Torna indietro cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
