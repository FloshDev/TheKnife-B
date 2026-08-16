package theknife.common.dto;
import java.io.Serializable;

/**
 * Rappresenta le credenziali utilizzate nella richiesta di autenticazione di un utente.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class LoginDTO implements Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * Lo username dell'utente che effettua l'accesso.
 */
    private String username;
    
/**
 * La password dell'utente che effettua l'accesso.
 */
    private String password;

//Costruttore
/**
 * Crea il DTO per le credenziali di accesso con lo username e la password specificati.
 * @param username lo username dell'utente
 * @param password la password dell'utente
 */
    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

//Getters e Setters
/**
 * Restituisce lo username dell'utente che effettua l'accesso.
 * @return lo username dell'utente
 */
    public String getUsername() {
        return username;
    }

/**
 * Imposta lo username dell'utente che effettua l'accesso.
 * @param username lo username dell'utente
 */
    public void setUsername(String username) {
        this.username = username;
    }

/**
 * Restituisce la password dell'utente che effettua l'accesso.
 * @return la password dell'utente
 */
    public String getPassword() {
        return password;
    }

/**
 * Imposta la password dell'utente che effettua l'accesso.
 * @param password la password dell'utente
 */
    public void setPassword(String password) {
        this.password = password;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto LoginDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "LoginDTO [username=" + username + "]";
    }
}
