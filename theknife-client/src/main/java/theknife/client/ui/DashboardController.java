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
 * Controller della schermata dashboard ristoratore (S09).
 *
 * @author Barlera Marco, 760000, VA
 */
public class DashboardController {
    @FXML private Label dashboardLabel;
    @FXML private ListView<String> ristorantiGestiti;
    @FXML private Button aggiungiNuovoRistoranteButton;
    @FXML private Button associaRistoranteButton;
    @FXML private Button gestisciRecensioniButton;
    @FXML private Button tornaHomeButton;
    @FXML private Button logoutButton;
    
    @FXML private void handleTornaHome(ActionEvent event) throws IOException {
        System.out.println("Torna Home cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
        stage.setScene(new Scene(root, 800, 600)); 
    }

    @FXML private void handleLogout(ActionEvent event) throws IOException {
        System.out.println("Logout cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/splash.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    @FXML private void handleAggiungiNuovoRistorante(ActionEvent event) throws IOException {
        System.out.println("Aggiungi nuovo ristorante cliccato");
    }

    @FXML private void handleAssociaRistorante(ActionEvent event) throws IOException {
        System.out.println("Associa ristorante cliccato");
    }

    @FXML private void handleGestisciRecensioni(ActionEvent event) throws IOException {
        System.out.println("Gestisci recensioni cliccato");
    }
}
