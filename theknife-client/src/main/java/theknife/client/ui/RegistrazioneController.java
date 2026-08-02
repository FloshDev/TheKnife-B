package theknife.client.ui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;

/**
 * Controller della schermata di registrazione (S03).
 *
 * @author Barlera Marco, 760000, VA
 */
public class RegistrazioneController {
    @FXML
    private TextField nomeField;
    @FXML
    private TextField cognomeField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField domicilioField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordField2;
    @FXML
    private DatePicker dataNascitaField;
    @FXML
    private RadioButton clienteRadio;
    @FXML
    private RadioButton ristoratoreRadio;
    
    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        System.out.println("Login cliccato: " + usernameField.getText());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Non crei una finestra nuova, riusi quella che già esiste
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/login.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
    
    @FXML
    private void handleRegistrazione() {
        System.out.println("Registrazione cliccato: " + usernameField.getText());
    }
}
