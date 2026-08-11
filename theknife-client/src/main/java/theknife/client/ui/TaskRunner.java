package theknife.client.ui;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;

/**
 * Esegue un'operazione bloccante (tipicamente una chiamata a un service, che a
 * sua volta parla col server) fuori dall'Application Thread di JavaFX, così che
 * la finestra resti reattiva durante l'attesa. Il risultato viene riconsegnato
 * al chiamante sul thread della UI, dove è sicuro toccare i nodi della
 * schermata; gli errori diventano un Alert, mai uno stack trace a video.
 *
 * <p>Le operazioni sono servite da un <b>unico</b> thread di servizio, quindi
 * una alla volta e in ordine di arrivo. È voluto: {@code ServerConnection}
 * possiede una sola connessione, e due richieste contemporanee sullo stesso
 * socket si intreccerebbero, facendo leggere a ciascuna la risposta dell'altra.
 * Il thread è daemon, così una richiesta ancora appesa non impedisce alla JVM
 * di terminare quando l'utente chiude la finestra.
 *
 * @author Barlera Marco, 760000, VA
 */
public class TaskRunner {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("client-task-runner");
        return t;
    });

    private TaskRunner() {}

    /**
     * Esegue l'operazione in background e, a buon fine, ne consegna il risultato
     * al chiamante sull'Application Thread di JavaFX. In caso di errore mostra
     * un Alert col messaggio dell'eccezione e {@code alSuccesso} non viene
     * eseguito.
     *
     * @param <T> tipo del risultato prodotto dall'operazione
     * @param operazione chiamata bloccante da eseguire fuori dal thread della UI
     * @param alSuccesso cosa fare col risultato; gira sul thread della UI, quindi
     *                   può toccare i nodi della schermata
     */
    public static <T> void run(Callable<T> operazione, Consumer<T> alSuccesso) {
        run(operazione, alSuccesso, eccezione -> {
            String messaggio = eccezione.getMessage();
            if (messaggio == null) {
                messaggio = "Si è verificato un errore sconosciuto.";
            }
            new Alert(Alert.AlertType.ERROR, messaggio).showAndWait();
        });
    }

    /**
     * Come {@link #run(Callable, Consumer)}, ma lascia al chiamante decidere
     * cosa fare in caso di fallimento invece di mostrare sempre un Alert —
     * serve quando l'errore va gestito in silenzio (es. geolocalizzazione
     * iniziale, decisione 18).
     *
     * @param <T> tipo del risultato prodotto dall'operazione
     * @param operazione chiamata bloccante da eseguire fuori dal thread della UI
     * @param alSuccesso cosa fare col risultato, sul thread della UI
     * @param alFallimento cosa fare in caso di errore, sul thread della UI
     */
    public static <T> void run(Callable<T> operazione, Consumer<T> alSuccesso, Consumer<Throwable> alFallimento) {
        Task<T> task = new Task<T>() {
            @Override protected T call() throws Exception {
                return operazione.call();
            }
        };
        task.setOnSucceeded(e -> alSuccesso.accept(task.getValue()));
        task.setOnFailed(e -> alFallimento.accept(task.getException()));
        executor.execute(task);
    }

}
