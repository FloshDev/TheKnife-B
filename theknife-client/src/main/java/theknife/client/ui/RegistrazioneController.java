package theknife.client.ui;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import theknife.client.service.AuthService;
import theknife.common.dto.RegistrazioneDTO;
import theknife.common.enums.Ruolo;

/**
 * Controller della schermata di registrazione (S03).
 *
 * @author Barlera Marco, 760000, VA
 */
public class RegistrazioneController {
    /**
     * Costruttore vuoto: tutta l'inizializzazione avviene in {@code initialize()},
     * chiamato da FXMLLoader dopo l'injection dei campi {@code @FXML}.
     */
    public RegistrazioneController() {
    }

    /** La card, la cui larghezza è agganciata alla finestra (grafica responsive). */
    @FXML private VBox card;
    /** Campo di testo per il nome. */
    @FXML private TextField nomeField;
    /** Campo di testo per il cognome. */
    @FXML private TextField cognomeField;
    /** Campo di testo per lo username scelto. */
    @FXML private TextField usernameField;
    /** Campo di testo per l'email. */
    @FXML private TextField emailField;
    /** Campo di testo per il domicilio, facoltativo. */
    @FXML private TextField domicilioField;

    /** Campo per la password, mascherato a schermo. */
    @FXML private PasswordField passwordField;
    /** Campo gemello di {@link #passwordField}, in chiaro: visibile solo dopo il click sull'occhiolino. */
    @FXML private TextField passwordFieldVisibile;
    /** Bottone a forma di occhio che alterna {@link #passwordField} e {@link #passwordFieldVisibile}. */
    @FXML private Button passwordToggleButton;
    /** Barra diagonale sull'icona dell'occhio, visibile quando la password è in chiaro. */
    @FXML private Line occhioBarrato;

    /** Campo per la conferma della password, mascherato a schermo. */
    @FXML private PasswordField passwordField2;
    /** Campo gemello di {@link #passwordField2}, in chiaro: visibile solo dopo il click sull'occhiolino. */
    @FXML private TextField password2FieldVisibile;
    /** Bottone a forma di occhio che alterna {@link #passwordField2} e {@link #password2FieldVisibile}. */
    @FXML private Button password2ToggleButton;
    /** Barra diagonale sull'icona dell'occhio, visibile quando la conferma password è in chiaro. */
    @FXML private Line occhioBarrato2;

    /** Selettore della data di nascita, facoltativo. */
    @FXML private DatePicker dataNascitaField;
    /** Selezione del ruolo Cliente, metà sinistra della pillola Cliente/Ristoratore. */
    @FXML private ToggleButton clienteRadio;
    /** Selezione del ruolo Ristoratore, metà destra della pillola Cliente/Ristoratore. */
    @FXML private ToggleButton ristoratoreRadio;

    /** Invia al server i dati del nuovo utente da registrare. */
    private final AuthService authService = new AuthService();

    /**
     * Collega i campi password in chiaro a quelli mascherati e configura il
     * {@link DatePicker}: formato di visualizzazione/parsing fisso
     * {@code dd/MM/yyyy} (non dipendente dal locale di sistema, per il
     * requisito multipiattaforma), testo segnaposto "gg/mm/aaaa" e
     * inserimento automatico degli slash mentre si digita.
     */
    @FXML private void initialize() {
        passwordFieldVisibile.textProperty().bindBidirectional(passwordField.textProperty());
        password2FieldVisibile.textProperty().bindBidirectional(passwordField2.textProperty());
        Responsive.aggancia(card, 0.45, 360, 480);

        dataNascitaField.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate data) {
                return data != null ? formato.format(data) : "";
            }

            @Override
            public LocalDate fromString(String testo) {
                if (testo == null || testo.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(testo, formato);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });
        dataNascitaField.getEditor().setPromptText("gg/mm/aaaa");

        dataNascitaField.getEditor().setTextFormatter(new TextFormatter<>(change -> {
            String testo = change.getControlNewText();
            if (!testo.matches("[0-9/]*") || testo.length() > 10) {
                return null;
            }
            if (change.isAdded() && (testo.length() == 2 || testo.length() == 5) && !testo.endsWith("/")) {
                change.setText(change.getText() + "/");
                change.setCaretPosition(change.getCaretPosition() + 1);
                change.setAnchor(change.getCaretPosition());
            }
            return change;
        }));
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

        if(password.isEmpty() || password2.isEmpty())  {
            Toast.errore("Inserisci una password");
            return;
        }
        if(password.length() < 8) {
            Toast.errore("La password deve avere almeno 8 caratteri");
            return;
        }
        if(!password.equals(password2)) {
            Toast.errore("Le password non coincidono");
            return;
        }
        if(!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            Toast.errore("Inserisci un indirizzo email valido");
            return;
        }
        if(dataNascita == null || dataNascita.isAfter(LocalDate.now())) {
            Toast.errore("Inserisci una data di nascita valida");
            return;
        }
        if(Period.between(dataNascita, LocalDate.now()).getYears() < 18) {
            Toast.errore("Devi avere almeno 18 anni per registrarti");
            return;
        }

        if(ruolo == null) {
            Toast.errore("Seleziona un ruolo");
            return;
        }
        RegistrazioneDTO dati = new RegistrazioneDTO(nome, cognome, username, password, email, ruolo, dataNascita, domicilio);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        TaskRunner.run(
            () -> {authService.registrati(dati); return null;},
            registrazioneResult -> {
                try{
                    Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/login.fxml"));
                    stage.getScene().setRoot(root);
                    Toast.successo("Account creato con successo, ora puoi accedere.");
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
        alternaVisibilitaPassword(passwordField, passwordFieldVisibile, occhioBarrato);
    }

    /**
     * Alterna {@link #passwordField2} e {@link #password2FieldVisibile}, al
     * click sul relativo occhiolino.
     */
    @FXML private void handleTogglePassword2() {
        alternaVisibilitaPassword(passwordField2, password2FieldVisibile, occhioBarrato2);
    }

    /**
     * Naviga alla schermata di login, senza registrare nessun utente.
     *
     * @param event l'evento generato dal click sul link di login
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleLogin(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Non crei una finestra nuova, riusi quella che già esiste
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/login.fxml"));
        stage.getScene().setRoot(root);
    }

    /**
     * Mostra in chiaro un campo password mascherato, o viceversa, tenendo
     * sincronizzato il testo tramite il binding fatto in {@link #initialize()}.
     *
     * @param mascherato il campo mascherato della coppia
     * @param chiaro     il campo in chiaro della coppia, gemello di {@code mascherato}
     * @param barra      la barra diagonale dell'icona a occhio della coppia
     */
    private void alternaVisibilitaPassword(PasswordField mascherato, TextField chiaro, Line barra) {
        boolean mostraChiaro = !chiaro.isVisible();
        mascherato.setVisible(!mostraChiaro);
        mascherato.setManaged(!mostraChiaro);
        chiaro.setVisible(mostraChiaro);
        chiaro.setManaged(mostraChiaro);
        barra.setVisible(mostraChiaro);
    }
}
