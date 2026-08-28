package theknife.client.app;

/**
 * Punto di ingresso del client TheKnife.
 *
 * @author Barlera Marco, 760000, VA
 */
public class TheKnifeClient {

    /** Classe di solo avvio, mai istanziata: espone solo {@link #main(String[])}. */
    public TheKnifeClient() {
    }

    /**
     * Avvia l'applicazione client, delegando a {@link ClientApplication#main(String[])}.
     *
     * @param args argomenti da riga di comando, inoltrati inalterati a JavaFX
     */
    public static void main(String[] args) {
        ClientApplication.main(args);
    }
}
