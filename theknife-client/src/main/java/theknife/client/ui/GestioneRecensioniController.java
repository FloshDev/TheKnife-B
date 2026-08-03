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
 * Controller della schermata di gestione recensione (S12).
 *
 * @author Barlera Marco, 760000, VA
 */

public class GestioneRecensioniController {
    @FXML private Label nomeRistoranteLabel;
    @FXML private ListView<String> recensioniListView;
    @FXML private Button rispondiButton;
    @FXML private Button tornaIndietroButton;

    @FXML private void handleRispondi() {
        System.out.println("Rispondi");
    }

    @FXML private void handleTornaIndietro(ActionEvent event) throws IOException {
        System.out.println("Torna indietro");
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
