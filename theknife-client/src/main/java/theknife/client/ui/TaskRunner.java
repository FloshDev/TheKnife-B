package theknife.client.ui;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javafx.concurrent.Task;
import theknife.client.service.ErroreServerException;

/**
 * Esegue un'operazione bloccante (tipicamente una chiamata a un service, che a
 * sua volta parla col server) fuori dall'Application Thread di JavaFX, così che
 * la finestra resti reattiva durante l'attesa. Il risultato viene riconsegnato
 * al chiamante sul thread della UI, dove è sicuro toccare i nodi della
 * schermata; gli errori diventano un {@link Toast}, mai uno stack trace a video.
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

    /**
     * Esecutore a thread singolo, condiviso da tutta l'applicazione: le
     * richieste si accodano ed escono una alla volta, mai due in volo.
     * È una garanzia di protocollo (decisione 25), non un dettaglio
     * implementativo — {@code ServerConnection} usa un solo socket e un
     * solo {@code ObjectOutputStream}; due scritture in parallelo lo
     * corromperebbero, con sintomo una {@code ClassCastException} casuale
     * in lettura anziché un errore leggibile. Chi allarga questo executor
     * rompe il protocollo.
     */
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
     * un {@link Toast} col messaggio dell'eccezione e {@code alSuccesso} non
     * viene eseguito.
     *
     * @param <T> tipo del risultato prodotto dall'operazione
     * @param operazione chiamata bloccante da eseguire fuori dal thread della UI
     * @param alSuccesso cosa fare col risultato; gira sul thread della UI, quindi
     *                   può toccare i nodi della schermata
     */
    public static <T> void run(Callable<T> operazione, Consumer<T> alSuccesso) {
        run(operazione, alSuccesso, eccezione -> {
            String messaggio;
            if (eccezione instanceof ErroreServerException) {
                // Il server ha risposto (non è un problema di connessione): il messaggio è
                // già pensato per l'utente ("Username o password non validi.", ecc.), va
                // mostrato così com'è — controllato PRIMA di IOException, di cui è sottoclasse.
                messaggio = eccezione.getMessage();
            } else if (eccezione instanceof IOException || eccezione instanceof ClassNotFoundException) {
                // Un socket caduto (server spento, rete interrotta) risale come IOException
                // grezza — "Connection reset"/"Broken pipe"/messaggio nullo, testo tecnico che
                // non dice all'utente cosa è successo. ClassNotFoundException (deserializzazione
                // fallita, stream corrotto o versioni client/server disallineate) ha lo stesso
                // problema: il messaggio è il nome di una classe, non testo per l'utente.
                // Stesso messaggio per entrambe: non è un errore applicativo, è la comunicazione
                // col server che non è andata a buon fine.
                messaggio = "Impossibile contattare il server. Controlla la connessione e riprova.";
            } else if (eccezione.getMessage() != null) {
                messaggio = eccezione.getMessage();
            } else {
                messaggio = "Si è verificato un errore sconosciuto.";
            }
            Toast.errore(messaggio);
        });
    }

    /**
     * Come {@link #run(Callable, Consumer)}, ma lascia al chiamante decidere
     * cosa fare in caso di fallimento invece di mostrare sempre un
     * {@link Toast} — serve quando l'errore va gestito in silenzio (es.
     * geolocalizzazione iniziale, decisione 18).
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
