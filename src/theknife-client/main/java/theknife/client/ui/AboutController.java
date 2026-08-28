package theknife.client.ui;

import javafx.fxml.FXML;

/**
 * Controller della schermata About (solo testo statico, nessuna logica).
 *
 * @author Barlera Marco, 760000, VA
 */
public class AboutController {
    /**
     * Costruttore vuoto: tutta l'inizializzazione avviene in {@code initialize()},
     * chiamato da FXMLLoader dopo l'injection dei campi {@code @FXML}.
     */
    public AboutController() {
    }


    /** Controller della sidebar inclusa (fx:include), per evidenziare "About" come voce attiva. */
    @FXML private SidebarController sidebarController;

    /**
     * Evidenzia "About" nella sidebar.
     */
    @FXML private void initialize() {
        sidebarController.impostaAttivo(SidebarController.Voce.ABOUT);
    }
}
