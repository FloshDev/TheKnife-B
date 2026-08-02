package theknife.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

/**
 * Controller della schermata iniziale (S01).
 *
 * @author Barlera Marco, 760000, VA
 */
public class SplashController {
    @FXML private TextField modificaLocalitaField;

    @FXML private void handleLogin(ActionEvent event) throws IOException {
        System.out.println("Login cliccato: ");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Non crei una finestra nuova, riusi quella che già esiste
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/login.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    @FXML private void handleGuest() {
        System.out.println("Continua come ospite cliccato");
    }

    @FXML private void handleConferma() {
        System.out.println("Conferma cliccato");
    }
}
