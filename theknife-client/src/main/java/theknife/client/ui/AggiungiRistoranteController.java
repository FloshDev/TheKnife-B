package theknife.client.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
    
    /** Campo di testo per il nome del ristorante. */
    @FXML private TextField nomeField;
    /** Campo di testo per l'indirizzo. */
    @FXML private TextField indirizzoField;
    /** Campo di testo per la città. */
    @FXML private TextField cittaField;
    /** Campo di testo per la provincia. */
    @FXML private TextField provinciaField;
    /** Campo di testo per la nazione. */
    @FXML private TextField nazioneField;
    /** Campo di testo per la fascia di prezzo (1-4). */
    @FXML private TextField fasciaPrezzoField;
    /** Campo di testo per il tipo di cucina. */
    @FXML private TextField tipoCucinaField;
    /** Campo di testo per il sito web, facoltativo. */
    @FXML private TextField webSiteField;
    /** Campo di testo per il telefono, facoltativo. */
    @FXML private TextField telefonoField;
    /** Campo di testo per eventuali premi ricevuti, facoltativo. */
    @FXML private TextField premiField;
    /** Indica se il ristorante offre prenotazione online. */
    @FXML private CheckBox prenotazioneOnlineCheck;
    /** Indica se il ristorante offre consegna a domicilio. */
    @FXML private CheckBox consegnaADomicilioCheck;

    /** Invia al server i comandi sui ristoranti, incluso l'inserimento. */
    private final RistoranteService ristoranteService = new RistoranteService();

    /**
     * Crea un nuovo ristorante con i dati del form e, al successo, naviga
     * direttamente al suo dettaglio usando l'id restituito dal server
     * (decisione 15: su questo comando il payload di ritorno esiste
     * apposta per evitare un giro di rete in più).
     *
     * @param event l'evento generato dal click sul bottone "Salva"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleSalva(ActionEvent event) throws IOException {
        String nome = nomeField.getText();
        String indirizzo = indirizzoField.getText();
        String citta = cittaField.getText();
        String provincia = provinciaField.getText();
        String nazione = nazioneField.getText();
        int fasciaPrezzo;
        try {
            fasciaPrezzo = Integer.parseInt(fasciaPrezzoField.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Fascia di prezzo non valida, inserisci un numero valido").showAndWait();
            return;
        }
        String tipoCucina = tipoCucinaField.getText();
        String webSite = webSiteField.getText();
        String telefono = telefonoField.getText();
        String premi = premiField.getText();
        boolean prenotazioneOnline = prenotazioneOnlineCheck.isSelected();
        boolean consegnaADomicilio = consegnaADomicilioCheck.isSelected();

        AggiungiRistoranteDTO ristoranteDTO = new AggiungiRistoranteDTO(nome, indirizzo, citta, provincia, nazione, fasciaPrezzo, prenotazioneOnline, consegnaADomicilio, tipoCucina, webSite, telefono, premi, null);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        TaskRunner.run(
            () -> ristoranteService.aggiungiRistorante(ristoranteDTO),
            result -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/dettaglio.fxml"));
                    Parent root = loader.load();
                    DettaglioController controller = loader.getController();
                    controller.impostaRistorante(result.getIdRistorante());
                    controller.impostaProvenienzaDashboard();
                    stage.getScene().setRoot(root);
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Errore nel caricamento della schermata: " + e.getMessage()).showAndWait();
                }
            }
        );
    }

    /**
     * Torna alla dashboard senza salvare.
     *
     * @param event l'evento generato dal click sul bottone "Annulla"
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML private void handleAnnulla(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/theknife/client/ui/dashboard.fxml"));
        stage.getScene().setRoot(root);
    }
}
