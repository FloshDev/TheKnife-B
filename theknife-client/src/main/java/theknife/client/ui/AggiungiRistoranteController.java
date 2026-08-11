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
import theknife.common.dto.AggiungiRistoranteDTO; 


/**
 * Controller della schermata aggiungi nuovo ristorante (S10).
 *
 * @author Barlera Marco, 760000, VA
 */

public class AggiungiRistoranteController {
    
    @FXML private TextField nomeField;
    @FXML private TextField indirizzoField;
    @FXML private TextField cittaField;
    @FXML private TextField nazioneField;
    @FXML private TextField fasciaPrezzoField;
    @FXML private TextField tipoCucinaField;
    @FXML private TextField webSiteField;
    @FXML private TextField telefonoField;
    @FXML private TextField premiField;
    @FXML private CheckBox prenotazioneOnlineCheck;
    @FXML private CheckBox consegnaADomicilioCheck;

    private final RistoranteService ristoranteService = new RistoranteService();

    @FXML private void handleSalva(ActionEvent event) throws IOException {
        System.out.println("Salva cliccato");

        String nome = nomeField.getText();
        String indirizzo = indirizzoField.getText();
        String citta = cittaField.getText();
        String nazione = nazioneField.getText();
        int fasciaPrezzo = Integer.parseInt(fasciaPrezzoField.getText());
        String tipoCucina = tipoCucinaField.getText();
        String webSite = webSiteField.getText();
        String telefono = telefonoField.getText();
        String premi = premiField.getText();
        boolean prenotazioneOnline = prenotazioneOnlineCheck.isSelected();
        boolean consegnaADomicilio = consegnaADomicilioCheck.isSelected();

        AggiungiRistoranteDTO ristoranteDTO = new AggiungiRistoranteDTO(nome, indirizzo, citta, nazione, fasciaPrezzo, prenotazioneOnline, consegnaADomicilio, tipoCucina, webSite, telefono, premi, null);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        TaskRunner.run(
            () -> ristoranteService.aggiungiRistorante(ristoranteDTO),
            result -> {
                System.out.println("Ristorante aggiunto con successo");
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
                    Parent root = loader.load();
                    DettaglioController controller = loader.getController();
                    controller.impostaRistorante(result.getIdRistorante());
                    stage.setScene(new Scene(root, 800, 600));
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
                }
            }
        );
    }

    @FXML private void handleAnnulla(ActionEvent event) throws IOException {
        System.out.println("Torna indietro cliccato");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }
}
