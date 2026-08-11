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
import theknife.common.enums.Ruolo;

/**
 * Controller della schermata di login (S02).
 *
 * @author Barlera Marco, 760000, VA
 */
public class LoginController {
    /** Campo di testo per lo username. */
    @FXML private TextField usernameField;
    /** Campo per la password, mascherato a schermo. */
    @FXML private PasswordField passwordField;
    private final AuthService authService = new AuthService();

    /**
     * Autentica l'utente con le credenziali inserite e, al successo, naviga
     * alla schermata corretta in base al ruolo: Cliente verso Home,
     * Ristoratore verso Dashboard.
     *
     * @param event l'evento generato dal click sul bottone di accesso
     */
    @FXML private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        
        TaskRunner.run(
            () -> authService.login(username, password),
            loginResult -> {
                try{
                    if(loginResult.getUtente().getRuolo() == Ruolo.CLIENTE) {
                        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
                        stage.setScene(new Scene(root, 800, 600));
                    }
                    else if (loginResult.getUtente().getRuolo() == Ruolo.RISTORATORE) {
                        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
                        stage.setScene(new Scene(root, 800, 600));
                    }
                }catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
                }
            }
        );
    }

    /**
     * Naviga alla schermata di registrazione.
     *
     * @param event l'evento generato dal click sul link di registrazione
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleRegistrazione(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Non crei una finestra nuova, riusi quella che già esiste
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/registrazione.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

}
