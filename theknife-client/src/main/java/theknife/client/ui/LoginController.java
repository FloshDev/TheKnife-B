package theknife.client.ui;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

/**
 * Controller della schermata di login (S02).
 *
 * @author Barlera Marco, 760000, VA
 */
public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private void handleLogin() {
        System.out.println("Login cliccato, username: " + usernameField.getText());
    }

    @FXML
    private void handleRegistrazione() {
        System.out.println("Registrazione cliccato");
    }

    @FXML
    private void handleGuest() {
        System.out.println("Continua come ospite cliccato");
    }
}
