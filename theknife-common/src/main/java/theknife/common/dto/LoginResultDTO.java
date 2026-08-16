package theknife.common.dto;
import theknife.common.enums.Ruolo;

/**
 * Rappresenta i dati restituiti come risultato della richiesta di autenticazione di un utente.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class LoginResultDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * Il token di sessione generato per l'utente autenticato.
 */
    private String sessionToken;
    
/**
 * Il ruolo dell'utente autenticato.
 */
    private Ruolo ruolo;
    
/**
 * I dati dell'utente autenticato.
 */
    private UtenteDTO utente;

//Costruttore
/**
 * Crea il DTO per il risultato dell'accesso con il token di sessione, il ruolo e i dati dell'utente 
 * specificati.
 * @param sessionToken il token di sessione
 * @param ruolo il ruolo dell'utente
 * @param utente i dati dell'utente
 */
    public LoginResultDTO(String sessionToken, Ruolo ruolo, UtenteDTO utente) {
        this.sessionToken = sessionToken;
        this.ruolo = ruolo;
        this.utente = utente;
    }

//Getters
/**
 * Restituisce il token di sessione generato per l'utente autenticato.
 * @return il token di sessione
 */
    public String getSessionToken() {
        return sessionToken;
    }

/**
 * Restituisce il ruolo dell'utente autenticato.
 * @return il ruolo dell'utente
 */
    public Ruolo getRuolo() {
        return ruolo;
    }

/**
 * Restituisce i dati dell'utente autenticato.
 * @return i dati dell'utente
 */
    public UtenteDTO getUtente() {
        return utente;
    }

//Setters
/**
 * Imposta il token di sessione per l'utente autenticato.
 * @param sessionToken il token di sessione
 */
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

/**
 * Imposta il ruolo dell'utente autenticato.
 * @param ruolo il ruolo dell'utente
 */
    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

/**
 * Imposta i dati dell'utente autenticato.
 * @param utente i dati dell'utente
 */
    public void setUtente(UtenteDTO utente) {
        this.utente = utente;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto LoginResultDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "LoginResultDTO [sessionToken=" + sessionToken + ", ruolo=" + ruolo + 
        ", utente=" + utente + "]";
    }
}
