package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * Controller della schermata associa ristorante (S11).
 *
 * @author Barlera Marco, 760000, VA
 */

public class AssociaRistoranteController {
    @FXML private TextField ricercaField;
    @FXML private ListView<String> risultatiListView;
    @FXML private Button cercaButton;
    @FXML private Button associaButton;
    @FXML private Button annullaButton;

    @FXML private void handleCerca() {
        System.out.println("Cerca cliccato");
    }
    @FXML private void handleAssocia(ActionEvent event) throws IOException {
        System.out.println("Associa cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
    @FXML private void handleAnnulla(ActionEvent event) throws IOException {
        System.out.println("Annulla cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

}
