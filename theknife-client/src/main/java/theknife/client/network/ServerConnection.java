package theknife.client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.concurrent.Task;
import theknife.common.dto.UtenteDTO;
import theknife.common.protocol.Request;
import theknife.common.protocol.Response;

/**
 * Gestisce la connessione socket verso il server: apre lo stream, invia le
 * Request e riceve le Response, in versione sincrona ({@link #inviaRichiesta})
 * o asincrona ({@link #inviaRichiestaAsync}, per non bloccare l'Application
 * Thread di JavaFX). Singleton: un'unica istanza condivisa per tutta la
 * durata dell'applicazione client, creata eagerly al caricamento della
 * classe (thread-safe per garanzia della JVM) e mai chiusa durante logout
 * (solo <code>sessionToken</code> e <code>utenteCorrente</code> vengono svuotati).
 *
 * @author Barlera Marco, 760000, VA
 */

public class ServerConnection {
    /** Unica istanza dell'applicazione, creata eagerly al caricamento della classe. */
    private static final ServerConnection instance = new ServerConnection();

    /** Il socket verso il server, aperto da {@link #connect(String, int)}. */
    private Socket socket;
    /** Stream di scrittura delle Request, condiviso da {@link #inviaRichiesta(Request)}. */
    private ObjectOutputStream out;
    /** Stream di lettura delle Response, condiviso da {@link #inviaRichiesta(Request)}. */
    private ObjectInputStream in;
    /** Token della sessione corrente, valorizzato al login e azzerato al logout. */
    private String sessionToken;

    /**
     * L'utente autenticato nella sessione corrente (decisione 23). Ha lo
     * stesso ciclo di vita di {@link #sessionToken}: valorizzato al login,
     * azzerato da {@link #clearSessionToken()}.
     */
    private UtenteDTO utenteCorrente;

    private ServerConnection() {}

    public static ServerConnection getInstance() {
        return instance;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    /**
     * Azzera lo stato della sessione corrente: token e utente autenticato.
     * Da chiamare al logout; non chiude la connessione.
     */
    public void clearSessionToken() {
        this.sessionToken = null;
        this.utenteCorrente = null;
    }

    /**
     * Restituisce l'utente autenticato nella sessione corrente.
     *
     * @return l'utente loggato, oppure <code>null</code> se nessuno ha
     *         effettuato il login
     */
    public UtenteDTO getUtenteCorrente() {
        return utenteCorrente;
    }

    /**
     * Imposta l'utente autenticato nella sessione corrente, valorizzato da
     * {@code AuthService} dopo un login riuscito.
     *
     * @param utenteCorrente l'utente appena autenticato
     */
    public void setUtenteCorrente(UtenteDTO utenteCorrente) {
        this.utenteCorrente = utenteCorrente;
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Chiude lo stream e il socket verso il server. Non fa nulla se la
     * connessione non è mai stata aperta o è già chiusa, invece di lanciare
     * {@link NullPointerException} — caso reale quando {@link #connect}
     * fallisce e l'applicazione chiude comunque la finestra.
     *
     * @throws IOException se la chiusura di stream o socket fallisce
     */
    public void disconnect() throws IOException {
        if (socket == null || socket.isClosed()) {
            return;
        }
        out.close();
        in.close();
        socket.close();
    }

    public Response inviaRichiesta(Request request) throws IOException, ClassNotFoundException {
        out.writeObject(request);
        out.flush();
        return (Response) in.readObject();
    }

    public Task<Response> inviaRichiestaAsync(Request request) {
        return new Task<>() {
            @Override
            public Response call() throws Exception {
                return inviaRichiesta(request);
            }
        };
    }
    
}
