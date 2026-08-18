package theknife.client.ui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Notifica non bloccante in alto a destra della finestra corrente, al posto
 * di un {@code Alert} nativo (finestra separata con lo stile del sistema
 * operativo, mai coerente con quello dell'app). Appare, resta visibile
 * qualche secondo e sparisce da sola con una dissolvenza — non serve
 * chiuderla a mano, e non blocca l'interazione col resto della schermata.
 *
 * <p>L'app ha un solo {@code Stage} (le schermate si sostituiscono dentro
 * quello, mai uno nuovo): {@link Window#getWindows()} lo trova sempre, senza
 * che il chiamante debba passarlo esplicitamente. Il filtro cerca uno
 * {@code Stage} in particolare, non un {@code Window} qualunque — altrimenti,
 * con due notifiche vicine nel tempo, la seconda rischierebbe di agganciarsi
 * al popup della prima invece che alla finestra dell'app.
 *
 * @author Barlera Marco, 760000, VA
 */
public class Toast {

    /** Quanto resta visibile a piena opacità prima di iniziare a sparire. */
    private static final Duration DURATA_VISIBILE = Duration.seconds(3);
    /** Durata della dissolvenza, sia in apertura che in chiusura. */
    private static final Duration DURATA_DISSOLVENZA = Duration.millis(250);
    /** Distanza dai bordi della finestra. */
    private static final double MARGINE = 20;
    /** Larghezza fissa: serve per calcolare la posizione prima ancora di mostrarlo. */
    private static final double LARGHEZZA = 320;

    private Toast() {}

    /**
     * Mostra una notifica di errore (rossa).
     *
     * @param messaggio il testo da mostrare
     */
    public static void errore(String messaggio) {
        mostra(messaggio, "toast-errore");
    }

    /**
     * Mostra una notifica di avviso (arancione) — richiede attenzione ma non
     * è un errore vero e proprio (es. un campo obbligatorio non compilato).
     *
     * @param messaggio il testo da mostrare
     */
    public static void avviso(String messaggio) {
        mostra(messaggio, "toast-avviso");
    }

    /**
     * Mostra una notifica di successo (verde) — conferma che un'operazione è
     * andata a buon fine.
     *
     * @param messaggio il testo da mostrare
     */
    public static void successo(String messaggio) {
        mostra(messaggio, "toast-successo");
    }

    /**
     * Applica lo stile dell'app a un {@link Alert} nativo, per i pochi casi
     * in cui non può diventare una notifica non bloccante: richiede una
     * scelta dell'utente ({@code CONFIRMATION}), oppure va mostrato prima
     * che la finestra principale esista ({@link theknife.client.app.ClientApplication}).
     *
     * @param alert il dialog da stilizzare, non ancora mostrato
     */
    public static void stilizza(Alert alert) {
        alert.getDialogPane().getStylesheets().add(Toast.class.getResource("/theknife/client/ui/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-app");
    }

    /**
     * Costruisce il popup, lo posiziona in alto a destra della finestra
     * corrente e ne gestisce dissolvenza in apertura, attesa e dissolvenza in
     * chiusura.
     *
     * @param messaggio  il testo da mostrare
     * @param styleClass la classe CSS che decide il colore (errore/avviso/successo)
     */
    private static void mostra(String messaggio, String styleClass) {
        Window finestra = Window.getWindows().stream()
            .filter(w -> w instanceof Stage && w.isShowing())
            .findFirst()
            .orElse(null);
        if (finestra == null) {
            return;
        }

        Label testo = new Label(messaggio);
        testo.setWrapText(true);
        testo.setMaxWidth(LARGHEZZA - 32);

        StackPane contenuto = new StackPane(testo);
        contenuto.getStyleClass().addAll("toast", styleClass);
        contenuto.setPrefWidth(LARGHEZZA);
        contenuto.getStylesheets().add(Toast.class.getResource("/theknife/client/ui/style.css").toExternalForm());
        contenuto.setOpacity(0);

        Popup popup = new Popup();
        popup.setAutoFix(false);
        popup.getContent().add(contenuto);
        popup.show(finestra,
            finestra.getX() + finestra.getWidth() - LARGHEZZA - MARGINE,
            finestra.getY() + MARGINE);

        FadeTransition dissolvenzaIn = new FadeTransition(DURATA_DISSOLVENZA, contenuto);
        dissolvenzaIn.setToValue(1);
        dissolvenzaIn.play();

        PauseTransition attesa = new PauseTransition(DURATA_VISIBILE);
        attesa.setOnFinished(e -> {
            FadeTransition dissolvenzaOut = new FadeTransition(DURATA_DISSOLVENZA, contenuto);
            dissolvenzaOut.setToValue(0);
            dissolvenzaOut.setOnFinished(fine -> popup.hide());
            dissolvenzaOut.play();
        });
        attesa.play();
    }
}
