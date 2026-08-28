package theknife.client.ui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import theknife.client.network.ServerConnection;
import theknife.client.service.RistoranteService;
import theknife.common.dto.CercaVicinoDTO;
import theknife.common.dto.UtenteDTO;

/**
 * Controller della schermata "Vicino a me" (S04b, RF-03/RF-07) — schermata a
 * parte da "Cerca" (S52: le due ricerche condividevano un form e si
 * ignoravano a vicenda in silenzio), raggiungibile da Ospite e Cliente
 * (non da Ristoratore). Per il Cliente autenticato la città è precompilata
 * col domicilio registrato (RF-07); per l'Ospite il campo resta vuoto e va
 * digitato a mano (RF-03, DIRETTIVE_PROGETTO.md: schermata minima richiesta
 * "ristoranti vicini al luogo indicato dall'utente guest").
 *
 * @author Barlera Marco, 760000, VA
 */
public class VicinoAMeController {

    /** Contenitore radice, usato solo per togliere il focus dal primo campo all'apertura. */
    @FXML private HBox root;
    /** La card, la cui larghezza è agganciata alla finestra (grafica responsive). */
    @FXML private VBox card;
    /** Controller della sidebar inclusa (fx:include), per evidenziare "Vicino a me" come voce attiva. */
    @FXML private SidebarController sidebarController;

    /** Campo di testo per la città di riferimento, precompilata col domicilio dell'utente. */
    @FXML private TextField cittaField;
    /** Campo di testo per il raggio, in km. */
    @FXML private TextField raggioKmField;

    /** Invia al server i comandi di ricerca dei ristoranti. */
    private final RistoranteService ristoranteService = new RistoranteService();

    /**
     * Toglie il focus dal primo campo di testo, evidenzia "Vicino a me" nella
     * sidebar e precompila la città col domicilio dell'utente autenticato
     * (decisione 14, stesso dato già usato da Splash/Login).
     */
    @FXML private void initialize() {
        Platform.runLater(() -> root.requestFocus());
        Responsive.aggancia(card, 0.5, 360, 560);
        sidebarController.impostaAttivo(SidebarController.Voce.VICINO_A_ME);

        UtenteDTO utente = ServerConnection.getInstance().getUtenteCorrente();
        if (utente != null) {
            cittaField.setText(utente.getDomicilio());
        }
    }

    /**
     * Cerca i ristoranti entro il raggio indicato dalla città (precompilata
     * col domicilio, correggibile a mano — l'utente ha l'ultima parola sul
     * luogo, decisione 18) e mostra i risultati.
     *
     * @param event l'evento generato dal click sul bottone di ricerca
     */
    @FXML private void handleCerca(ActionEvent event) {
        double raggioKm;
        try {
            raggioKm = Double.parseDouble(raggioKmField.getText());
        } catch (NumberFormatException e) {
            Toast.errore("Raggio non valido, inserisci un numero valido");
            return;
        }

        String luogo = cittaField.getText();
        if (luogo == null || luogo.isBlank()) {
            Toast.errore("Inserisci una città");
            return;
        }

        CercaVicinoDTO filtri = new CercaVicinoDTO(raggioKm, luogo);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        TaskRunner.run(
            () -> ristoranteService.cercaVicino(filtri),
            cercaResult -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/theknife/client/ui/risultati.fxml"));
                    Parent root = loader.load();
                    RisultatiController controller = loader.getController();
                    controller.impostaRisultati(cercaResult);
                    controller.impostaFiltriVicino(filtri);
                    stage.getScene().setRoot(root);
                } catch (IOException e) {
                    Toast.errore("Errore nel caricamento della schermata: " + e.getMessage());
                }
            }
        );
    }

    /**
     * Precompila città e raggio con i valori usati nell'ultima ricerca, così
     * tornando indietro da Risultati non si riparte da un form vuoto.
     *
     * @param filtri i filtri dell'ultima ricerca, o {@code null} (nessuna precompilazione)
     */
    public void precompilaFiltri(CercaVicinoDTO filtri) {
        if (filtri == null) {
            return;
        }
        cittaField.setText(filtri.getLuogo());
        raggioKmField.setText(String.valueOf(filtri.getRaggioKm()));
    }
}
