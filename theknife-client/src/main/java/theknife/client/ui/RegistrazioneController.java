package theknife.client.ui;
import java.io.IOException;
import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import theknife.client.service.AuthService;
import theknife.common.dto.RegistrazioneDTO;
import theknife.common.enums.Ruolo;

/**
 * Controller della schermata di registrazione (S03).
 *
 * @author Barlera Marco, 760000, VA
 */
public class RegistrazioneController {
    @FXML private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField domicilioField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField passwordField2;
    @FXML private DatePicker dataNascitaField;
    @FXML private RadioButton clienteRadio;
    @FXML private RadioButton ristoratoreRadio;

    private final AuthService authService = new AuthService();

    /**
     * Naviga alla schermata di login, senza registrare nessun utente.
     *
     * @param event l'evento generato dal click sul link di login
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleLogin(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Non crei una finestra nuova, riusi quella che già esiste
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/login.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    /**
     * Valida i dati del form (password coincidenti e non vuote, ruolo
     * selezionato) e, se validi, registra il nuovo utente. Al successo
     * naviga alla schermata di login: la decisione 15 esclude l'auto-login
     * dopo la registrazione.
     *
     * @param event l'evento generato dal click sul bottone di registrazione
     */
    @FXML private void handleRegistrazione(ActionEvent event) {
        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String domicilio = domicilioField.getText();
        String password = passwordField.getText();
        String password2 = passwordField2.getText();
        LocalDate dataNascita = dataNascitaField.getValue();
        Ruolo ruolo = clienteRadio.isSelected() ? Ruolo.CLIENTE : (ristoratoreRadio.isSelected() ? Ruolo.RISTORATORE : null);

        if(!password.equals(password2) ) {
            if(password.isEmpty() || password2.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Inserisci una password").showAndWait();
                return;
            }
            new Alert(Alert.AlertType.ERROR, "Le password non coincidono").showAndWait();
            return;
        }

        if(ruolo == null) {
            new Alert(Alert.AlertType.ERROR, "Seleziona un ruolo").showAndWait();
            return;
        }
        RegistrazioneDTO dati = new RegistrazioneDTO(nome, cognome, username, password, email, ruolo, dataNascita, domicilio);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        TaskRunner.run(
            () -> {authService.registrati(dati); return null;},
            registrazioneResult -> {
                try{
                    Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/login.fxml"));
                    stage.setScene(new Scene(root, 800, 600));
                }catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
                }
            }
        );
    }
}
