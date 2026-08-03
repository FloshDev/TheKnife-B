package theknife.server.service;

import java.util.concurrent.ConcurrentHashMap;
import theknife.common.dto.UtenteDTO;

/**
 * Gestisce le sessioni utente attive del server. Mantiene la corrispondenza
 * token→utente in una mappa thread-safe: più thread (un client per thread)
 * la leggono e scrivono in concorrenza durante login, richieste e logout.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class SessionManager {
    
    private final ConcurrentHashMap<String, UtenteDTO> sessioni
        = new ConcurrentHashMap<>();

    public void login (String token, UtenteDTO utente) {
        sessioni.put(token, utente);
    }

    public UtenteDTO getUtenteFromSession (String token) {
        return sessioni.get(token);
    }

    public void logout (String token) {
        sessioni.remove(token);
    }

}
