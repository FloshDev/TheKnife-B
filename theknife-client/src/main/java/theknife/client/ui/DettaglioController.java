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
import theknife.client.service.RecensioneService;
import theknife.client.service.RistoranteService;
import theknife.common.dto.IdRistoranteDTO;

/**
 * Controller della schermata di dettaglio (S06).
 *
 * @author Barlera Marco, 760000, VA
 */

public class DettaglioController {
    @FXML private Label nomeLabel;
    @FXML private Label tipoCucinaLabel;
    @FXML private Label indirizzoLabel;
    @FXML private Label fasciaPrezzoLabel;
    @FXML private Label mediaStelleLabel;

    @FXML private Button preferitiButton;
    @FXML private Button scriviRecensioneButton;
    @FXML private Button rispondiRecensioneButton;
    @FXML private Button tornaIndietroButton;
    @FXML private ListView<String> recensioniListView;

    private final RistoranteService ristoranteService = new RistoranteService();
    private final RecensioneService recensioneService = new RecensioneService();
    private long idRistorante;

    /**
     * Aggiunge il ristorante corrente ai preferiti dell'utente.
     */
    @FXML private void handlePreferiti() {
        TaskRunner.run(
            () -> {
                ristoranteService.aggiungiPreferito(new IdRistoranteDTO(idRistorante));
                return null;
            },
            esito -> {
                new Alert(Alert.AlertType.INFORMATION, "Ristorante aggiunto ai preferiti.").showAndWait();
            }
        );
    }

    /**
     * Naviga alla schermata di scrittura recensione, passando id e nome del
     * ristorante corrente.
     *
     * @param event l'evento generato dal click sul bottone "Scrivi recensione"
     */
    @FXML private void handleScriviRecensione(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/recensione.fxml"));
            Parent root = loader.load();
            ScriviRecensioneController controller = loader.getController();
            controller.impostaRistorante(idRistorante, nomeLabel.getText());
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
        }

    }

    /**
     * Placeholder per la risposta a una recensione, non ancora implementato.
     */
    @FXML private void handleRispondiRecensione() {
    }

    /**
     * Naviga alla schermata dei risultati di ricerca.
     *
     * @param event l'evento generato dal click sul bottone "Torna indietro"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleTornaIndietro(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/risultati.fxml"));
        stage.setScene(new Scene(root, 800, 600));
    }

    /**
     * Carica dal server i dettagli e le recensioni del ristorante indicato e
     * popola la schermata. Le due chiamate sono indipendenti: ognuna
     * aggiorna la propria parte di schermo quando la sua risposta arriva.
     *
     * @param idRistorante l'identificativo del ristorante da mostrare
     */
    public void impostaRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
        TaskRunner.run(
            () -> ristoranteService.ottieniDettagli(new IdRistoranteDTO(idRistorante)),
            ristorante -> {
                nomeLabel.setText(ristorante.getNome());
                tipoCucinaLabel.setText(ristorante.getTipoCucina());
                indirizzoLabel.setText(ristorante.getIndirizzo());
                fasciaPrezzoLabel.setText(String.valueOf(ristorante.getFasciaPrezzo()));
                mediaStelleLabel.setText(String.valueOf(ristorante.getMediaStelle()));
            }
        );

        TaskRunner.run(
            () -> recensioneService.leggiRecensioni(new IdRistoranteDTO(idRistorante)),
            recensioni -> {
                List<String> testi = recensioni.stream()
                    .map(r -> r.getStelle() + "★ - " + r.getTesto()) // Trasformo ogni recensione in una stringa leggibile
                    .toList(); // Chiude lo stream 
                recensioniListView.getItems().setAll(testi);
            }
        );
    }
}
