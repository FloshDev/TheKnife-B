package theknife.client.ui;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * Lega la larghezza massima di una card a una frazione della larghezza della
 * finestra, invece di tenerla fissa come nel CSS (`.card`/`.card-lista`).
 * Resta comunque contenuta fra un minimo e un massimo, così non diventa né
 * troppo stretta su una finestra piccola né enorme su uno schermo grande.
 *
 * @author Barlera Marco, 760000, VA
 */
public class Responsive {

    private Responsive() {}

    /**
     * Aggancia la larghezza massima della card alla larghezza della scena,
     * appena questa è disponibile (non lo è ancora quando {@code initialize()}
     * gira: la scena si attacca dopo, quando la schermata sostituisce quella
     * corrente). Il binding sovrascrive il {@code -fx-max-width} del CSS,
     * di proposito: da qui in avanti è il codice a decidere la larghezza.
     *
     * @param card     la card la cui larghezza massima va agganciata
     * @param frazione la frazione della larghezza della finestra da usare (es. 0.5)
     * @param minimo   la larghezza minima della card, indipendentemente dalla finestra
     * @param massimo  la larghezza massima della card, indipendentemente dalla finestra
     */
    public static void aggancia(Region card, double frazione, double minimo, double massimo) {
        card.sceneProperty().addListener((obs, vecchia, nuova) -> {
            if (nuova != null) {
                card.maxWidthProperty().bind(
                    Bindings.max(minimo, Bindings.min(massimo, nuova.widthProperty().multiply(frazione)))
                );
            }
        });
    }

    /**
     * Aggancia il numero di righe visibili di un {@link TextArea} alla
     * lunghezza del testo digitato, così il campo cresce in altezza invece
     * di scorrere internamente su testi lunghi — stima approssimata (a capo
     * espliciti + un rigo ogni ~50 caratteri), non un conteggio esatto delle
     * righe effettivamente disegnate.
     *
     * @param area il campo la cui altezza va adattata al contenuto
     */
    public static void adattaAltezzaAlTesto(TextArea area) {
        area.textProperty().addListener((obs, vecchio, nuovo) -> {
            int righe = 1 + (int) nuovo.chars().filter(c -> c == '\n').count() + nuovo.length() / 50;
            area.setPrefRowCount(Math.max(1, Math.min(6, righe)));
        });
    }
}
