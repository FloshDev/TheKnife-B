package theknife.client.ui;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.Line;
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
    /** Campo gemello di {@link #passwordField}, in chiaro: visibile solo dopo il click sull'occhiolino. */
    @FXML private TextField passwordFieldVisibile;
    /** Bottone a forma di occhio che alterna {@link #passwordField} e {@link #passwordFieldVisibile}. */
    @FXML private Button passwordToggleButton;
    /** Barra diagonale sull'icona dell'occhio, visibile quando la password è in chiaro. */
    @FXML private Line occhioBarrato;

    /** Invia al server le credenziali e riceve l'esito dell'autenticazione. */
    private final AuthService authService = new AuthService();

    /**
     * Collega il campo password in chiaro a quello mascherato, tenendoli
     * sincronizzati per l'occhiolino di visibilità.
     */
    @FXML private void initialize() {
        passwordFieldVisibile.textProperty().bindBidirectional(passwordField.textProperty());
    }

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
                        stage.getScene().setRoot(root);
                    }
                    else if (loginResult.getUtente().getRuolo() == Ruolo.RISTORATORE) {
                        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
                        stage.getScene().setRoot(root);
                    }
                }catch (IOException e) {
                    Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
                }
            }
        );
    }

    /**
     * Alterna {@link #passwordField} e {@link #passwordFieldVisibile}, al
     * click sul relativo occhiolino.
     */
    @FXML private void handleTogglePassword() {
        boolean mostraChiaro = !passwordFieldVisibile.isVisible();
        passwordField.setVisible(!mostraChiaro);
        passwordField.setManaged(!mostraChiaro);
        passwordFieldVisibile.setVisible(mostraChiaro);
        passwordFieldVisibile.setManaged(mostraChiaro);
        occhioBarrato.setVisible(mostraChiaro);
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
        stage.getScene().setRoot(root);
    }

    /**
    * Naviga alla Home senza autenticarsi.
    *
    * @param event l'evento generato dal click sul link "Continua come ospite"
     * @throws IOException se il caricamento della schermata fallisce
    */
    @FXML private void handleGuest(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
        stage.getScene().setRoot(root);
    }

}
