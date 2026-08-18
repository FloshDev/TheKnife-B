package theknife.server.service;

import java.util.concurrent.ConcurrentHashMap;

import theknife.common.dto.UtenteDTO;

/**
 * Gestisce le sessioni utente attive del server. Mantiene la corrispondenza
 * token→utente in una mappa thread-safe: più thread (un client per thread)
 * la leggono e scrivono in concorrenza durante login, richieste e logout.
 * <p>
 * Le sessioni vivono in memoria e non sopravvivono al riavvio del server: al
 * riavvio tutti i client devono autenticarsi di nuovo.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class SessionManager {

    /**
     * Sessioni attive, indicizzate per token. La mappa e' concorrente perche'
     * un thread per client la legge a ogni richiesta e la scrive a ogni login
     * o logout.
     */
    private final ConcurrentHashMap<String, UtenteDTO> sessioni
        = new ConcurrentHashMap<>();

    /**
     * Costruisce un registro vuoto: all'avvio del server nessuna sessione e'
     * attiva, perche' le sessioni non sopravvivono al riavvio.
     */
    public SessionManager() {
    }

    /**
     * Registra una nuova sessione attiva.
     *
     * @param token  il token generato per la sessione
     * @param utente l'utente che si e' autenticato
     */
    public void login (String token, UtenteDTO utente) {
        sessioni.put(token, utente);
    }

    /**
     * Risolve un token nell'utente della sessione corrispondente.
     *
     * @param token il token ricevuto nella richiesta, eventualmente
     *              <code>null</code> per una richiesta non autenticata
     * @return l'utente della sessione, oppure <code>null</code> se il token e'
     *         assente, scaduto o mai emesso
     */
    public UtenteDTO getUtenteFromSession (String token) {
        if (token == null) {
            return null;
        }
        return sessioni.get(token);
    }

    /**
     * Chiude la sessione associata al token. Un token sconosciuto o
     * <code>null</code> non produce errore.
     *
     * @param token il token della sessione da chiudere
     */
    public void logout (String token) {
        if (token != null) {
            sessioni.remove(token);
        }
    }

}
