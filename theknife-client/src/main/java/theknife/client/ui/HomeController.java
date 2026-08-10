package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import theknife.client.service.RistoranteService;
import theknife.common.dto.CercaRistorantiDTO;
import theknife.common.dto.CercaVicinoDTO;

/**
 * Controller della schermata Home (S04).
 *
 * @author Barlera Marco, 760000, VA
 */

public class HomeController {
    
    @FXML private TextField nomeField;
    @FXML private TextField cittaField;
    @FXML private TextField tipoCucinaField;
    @FXML private TextField fasciaPrezzoField;
    @FXML private TextField raggioKmField;
    @FXML private CheckBox prenotazioneOnlineCheck;
    @FXML private CheckBox consegnaADomicilioCheck;
    private final RistoranteService ristoranteService = new RistoranteService();

    @FXML private void handleCerca(ActionEvent event) {
        System.out.println("Cerca cliccato");

        String nome = nomeField.getText();
        String citta = cittaField.getText();
        String tipoCucina = tipoCucinaField.getText();
        int fasciaPrezzo = Integer.parseInt(fasciaPrezzoField.getText());
        boolean prenotazioneOnline = prenotazioneOnlineCheck.isSelected();
        boolean consegnaADomicilio = consegnaADomicilioCheck.isSelected();

        CercaRistorantiDTO filtri = new CercaRistorantiDTO(
            nome, citta, tipoCucina, prenotazioneOnline, fasciaPrezzo, consegnaADomicilio, null
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 

        TaskRunner.run(
        () -> ristoranteService.cercaRistoranti(filtri),
        cercaResult -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/risultati.fxml"));
                Parent root = loader.load();
                RisultatiController controller = loader.getController();
                controller.impostaRisultati(cercaResult);
                stage.setScene(new Scene(root, 800, 600));
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
            }
        }
        );
    }

    @FXML private void handleVicinoAMe(ActionEvent event) {
        System.out.println("Vicino a me cliccato");

        double raggioKm = Double.parseDouble(raggioKmField.getText());
        CercaVicinoDTO filtri = new CercaVicinoDTO(raggioKm, null);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        TaskRunner.run(
        () -> ristoranteService.cercaVicino(filtri),
        cercaResult -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/risultati.fxml"));
                Parent root = loader.load();
                RisultatiController controller = loader.getController();
                controller.impostaRisultati(cercaResult);
                stage.setScene(new Scene(root, 800, 600));
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
            }
        }
        );
    }

    @FXML private void handlePreferiti() {
        System.out.println("Preferiti cliccato");
    }

    @FXML private void handleDashboard() {
        System.out.println("Dashboard cliccato");
    }

    @FXML private void handleLogout(ActionEvent event) throws IOException {
        System.out.println("Logout cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/splash.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    @FXML private void handleLogin(ActionEvent event) throws IOException {
        System.out.println("Login cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); 
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/login.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
