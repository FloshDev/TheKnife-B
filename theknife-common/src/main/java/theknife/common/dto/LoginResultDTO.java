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
//Costruttore
    public LoginResultDTO(String sessionToken, Ruolo ruolo) {
        this.sessionToken = sessionToken;
        this.ruolo = ruolo;
    }
//Getters
    public String getSessionToken() {
        return sessionToken;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }
//Setters
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }
//Metodo toString
    @Override
    public String toString() {
        return "LoginResultDTO [sessionToken=" + sessionToken + ", ruolo=" + ruolo + "]";
    }
}
