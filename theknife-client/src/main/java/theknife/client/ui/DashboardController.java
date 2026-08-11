package theknife.client.ui;

import java.io.IOException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import theknife.client.service.AuthService;
import theknife.client.service.RistoranteService;


/**
 * Controller della schermata dashboard ristoratore (S09).
 *
 * @author Barlera Marco, 760000, VA
 */
public class DashboardController {
    @FXML private Label dashboardLabel;
    @FXML private ListView<String> ristorantiGestiti;
    @FXML private Button aggiungiNuovoRistoranteButton;
    @FXML private Button associaRistoranteButton;
    @FXML private Button gestisciRecensioniButton;
    @FXML private Button tornaHomeButton;
    @FXML private Button logoutButton;

    private final RistoranteService ristoranteService = new RistoranteService();
    private final AuthService authService = new AuthService();
    
    /**
     * Carica i ristoranti gestiti dall'utente ristoratore all'apertura
     * della schermata.
     */
    @FXML private void initialize() {
        TaskRunner.run(
            () -> ristoranteService.vediRistorantiGestiti(),
            ristoranti -> {
                List<String> nomi = ristoranti.stream()
                    .map(r -> r.getNome() + " - " + r.getCitta())
                    .toList();
                ristorantiGestiti.getItems().setAll(nomi);
            }
        );
    }

    /**
     * Naviga alla schermata Home.
     *
     * @param event l'evento generato dal click sul bottone "Torna Home"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleTornaHome(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/home.fxml"));
        stage.setScene(new Scene(root, 800, 600)); 
    }

    /**
     * Invalida la sessione sul server e naviga alla schermata iniziale.
     *
     * @param event l'evento generato dal click sul bottone "Logout"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleLogout(ActionEvent event) throws IOException {
        TaskRunner.run(
            () -> { authService.logout();
                    return null;
                },
            _void -> {
                try{
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/splash.fxml"));
                    stage.setScene(new Scene(root, 800, 600));
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
                }
            }
        );
    }

    /**
     * Naviga alla schermata di inserimento di un nuovo ristorante.
     *
     * @param event l'evento generato dal click sul bottone corrispondente
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleAggiungiNuovoRistorante(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/aggiungiRistorante.fxml"));
        stage.setScene(new Scene(root, 800, 600));  
    }

    /**
     * Naviga alla schermata per associarsi a un ristorante esistente.
     *
     * @param event l'evento generato dal click sul bottone corrispondente
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleAssociaRistorante(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/associaRistorante.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    /**
     * Naviga alla schermata di gestione delle recensioni dei ristoranti
     * gestiti.
     *
     * @param event l'evento generato dal click sul bottone corrispondente
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleGestisciRecensioni(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/gestioneRecensione.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
