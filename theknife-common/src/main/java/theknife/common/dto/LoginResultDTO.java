package theknife.common.dto;
import theknife.common.enums.Ruolo;

/**
 * Rappresenta i dati restituiti come risultato della richiesta di autenticazione di un utente.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class LoginResultDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String sessionToken;
    private Ruolo ruolo;
    private UtenteDTO utente;
//Costruttore
    public LoginResultDTO(String sessionToken, Ruolo ruolo, UtenteDTO utente) {
        this.sessionToken = sessionToken;
        this.ruolo = ruolo;
        this.utente = utente;
    }
//Getters
    public String getSessionToken() {
        return sessionToken;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    public UtenteDTO getUtente() {
        return utente;
    }
//Setters
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

    public void setUtente(UtenteDTO utente) {
        this.utente = utente;
    }
//Metodo toString
    @Override
    public String toString() {
        return "LoginResultDTO [sessionToken=" + sessionToken + ", ruolo=" + ruolo + 
        ", utente=" + utente + "]";
    }
}
