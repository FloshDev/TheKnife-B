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
 * Controller della schermata Preferti (S08).
 *
 * @author Barlera Marco, 760000, VA
 */

public class PreferitiController {

    @FXML private Label preferitiLabel;
    @FXML private ListView<String> preferitiListView;
    @FXML private Button tornaIndietroButton;
    @FXML private Button rimuoviPreferitoButton;
    @FXML private Button vediDettaglioButton;

    @FXML private void handleTornaIndietro(ActionEvent event) throws IOException {
        System.out.println("Torna indietro cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    @FXML private void handleRimuoviPreferito(ActionEvent event) throws IOException {
        System.out.println("Rimuovi preferito cliccato");
    }

    @FXML private void handleVediDettaglio(ActionEvent event) throws IOException {
        System.out.println("Vedi dettaglio cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
