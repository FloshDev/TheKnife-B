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
 * Controller della schermata Home (S04).
 *
 * @author Barlera Marco, 760000, VA
 */

public class HomeController {
    
    @FXML private TextField nomeField;
    @FXML private TextField cittaField;
    @FXML private TextField tipoCucinaField;
    @FXML private TextField fasciaPrezzoField;
    @FXML private TextField raggioKmField;
    @FXML private CheckBox prenotazioneOnlineCheck;
    @FXML private CheckBox consegnaADomicilioCheck;

    @FXML private void handleCerca() {
        System.out.println("Cerca cliccato");
    }

    @FXML private void handleVicinoAMe() {
        System.out.println("Vicino a me cliccato");
    }

    @FXML private void handlePreferiti() {
        System.out.println("Preferiti cliccato");
    }

    @FXML private void handleDashboard() {
        System.out.println("Dashboard cliccato");
    }

    @FXML private void handleLogout(ActionEvent event) throws IOException {
        System.out.println("Logout cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/splash.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    @FXML private void handleLogin(ActionEvent event) throws IOException {
        System.out.println("Login cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/login.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
