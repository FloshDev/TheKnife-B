package theknife.client.ui;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

/**
 * Controller della schermata di login (S02).
 *
 * @author Barlera Marco, 760000, VA
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML private void handleLogin() {
        System.out.println("Login cliccato, username: " + usernameField.getText());
    }

    @FXML private void handleRegistrazione(ActionEvent event) throws IOException {
        System.out.println("Registrazione cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Non crei una finestra nuova, riusi quella che già esiste
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/registrazione.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    @FXML private void handleGuest() {
        System.out.println("Continua come ospite cliccato");
    }
}
