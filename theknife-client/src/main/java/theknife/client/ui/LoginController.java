package theknife.client.ui;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import theknife.client.service.AuthService;

/**
 * Controller della schermata di login (S02).
 *
 * @author Barlera Marco, 760000, VA
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    private final AuthService authService = new AuthService();

    @FXML private void handleLogin(ActionEvent event) {
        System.out.println("Login cliccato, username: " + usernameField.getText());

        String username = usernameField.getText();
        String password = passwordField.getText();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        
        TaskRunner.run(
            () -> authService.login(username, password),
            loginResult -> {
                try{
                    Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
                    stage.setScene(new Scene(root, 800, 600));
                }catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
                }
            }
        );
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
