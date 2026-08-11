package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import theknife.client.service.RistoranteService;

/**
 * Controller della schermata iniziale (S01).
 *
 * @author Barlera Marco, 760000, VA
 */
public class SplashController {
    @FXML private TextField modificaLocalitaField;
    @FXML private Label localitaLabel;

    private final RistoranteService ristoranteService = new RistoranteService();

    @FXML private void initialize() {
        TaskRunner.run(
            () -> ristoranteService.ottieniLocalitaIniziale(),
            posizione -> localitaLabel.setText(posizione.getLatitudine() + ", " + posizione.getLongitudine()),
            eccezione -> { /* silenzioso: fallisce sempre su IP locale/LAN, decisione 18 */ }
        );
    }

    @FXML private void handleLogin(ActionEvent event) throws IOException {
        System.out.println("Login cliccato: ");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Non crei una finestra nuova, riusi quella che già esiste
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/login.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    @FXML private void handleGuest(ActionEvent event) throws IOException {
        System.out.println("Continua come ospite cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    @FXML private void handleConferma(ActionEvent event) throws IOException {
        System.out.println("Conferma cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
