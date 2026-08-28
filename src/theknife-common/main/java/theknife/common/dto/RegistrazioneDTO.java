package theknife.common.dto;
import java.io.Serializable;
import java.time.LocalDate;
import theknife.common.enums.Ruolo;

/**
 * Rappresenta i dati necessari per la richiesta di registrazione di un nuovo utente.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class RegistrazioneDTO implements Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * Il nome dell'utente che si registra.
 */
    private String nome;
    
/**
 * Il cognome dell'utente che si registra.
 */
    private String cognome;
    
/**
 * Lo username dell'utente che si registra.
 */
    private String username;
    
/**
 * La password dell'utente che si registra.
 */
    private String password;
    
/**
 * L'email dell'utente che si registra.
 */
    private String email;
    
/**
 * Il ruolo dell'utente che si registra.
 */
    private Ruolo ruolo;
    
/**
 * La data di nascita dell'utente che si registra.
 */
    private LocalDate dataNascita;
    
/**
 * Il domicilio dell'utente che si registra.
 */
    private String domicilio;

//Costruttore
/**
 * Crea il DTO per la registrazione di un nuovo utente con i dati specificati.
 * @param nome il nome dell'utente che si registra
 * @param cognome il cognome dell'utente che si registra
 * @param username lo username dell'utente che si registra
 * @param password la password dell'utente che si registra
 * @param email l'email dell'utente che si registra
 * @param ruolo il ruolo dell'utente che si registra
 * @param dataNascita la data di nascita dell'utente che si registra
 * @param domicilio il domicilio dell'utente che si registra
 */
    public RegistrazioneDTO(String nome, String cognome, String username, 
        String password, String email, Ruolo ruolo, LocalDate dataNascita, String domicilio) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.email = email;
        this.ruolo = ruolo;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
    }
//Setters
/**
 * Imposta il nome dell'utente che si registra.
 * @param nome il nome dell'utente
 */
    public void setNome(String nome) {
        this.nome = nome;
    }

/**
 * Imposta il cognome dell'utente che si registra.
 * @param cognome il cognome dell'utente
 */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

/**
 * Imposta lo username dell'utente che si registra.
 * @param username lo username dell'utente
 */
    public void setUsername(String username) {
        this.username = username;
    }

/**
 * Imposta la password dell'utente che si registra.
 * @param password la password dell'utente
 */
    public void setPassword(String password) {
        this.password = password;
    }

/**
 * Imposta l'email dell'utente che si registra.
 * @param email l'email dell'utente
 */
    public void setEmail(String email) {
        this.email = email;
    }

/**
 * Imposta il ruolo dell'utente che si registra.
 * @param ruolo il ruolo dell'utente
 */
    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

/**
 * Imposta la data di nascita dell'utente che si registra.
 * @param dataNascita la data di nascita dell'utente
 */
    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

/**
 * Imposta il domicilio dell'utente che si registra.
 * @param domicilio il domicilio dell'utente
 */
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

//Getters
/**
 * Restituisce il nome dell'utente che si registra.
 * @return il nome dell'utente
 */
    public String getNome() {
        return nome;
    }

/**
 * Restituisce il cognome dell'utente che si registra.
 * @return il cognome dell'utente
 */
    public String getCognome() {
        return cognome;
    }

/**
 * Restituisce lo username dell'utente che si registra.
 * @return lo username dell'utente
 */
    public String getUsername() {
        return username;
    }

/**
 * Restituisce la password dell'utente che si registra.
 * @return la password dell'utente
 */
    public String getPassword() {
        return password;
    }

/**
 * Restituisce l'email dell'utente che si registra.
 * @return l'email dell'utente
 */
    public String getEmail() {
        return email;
    }

/**
 * Restituisce il ruolo dell'utente che si registra.
 * @return il ruolo dell'utente
 */
    public Ruolo getRuolo() {
        return ruolo;
    }

/**
 * Restituisce la data di nascita dell'utente che si registra.
 * @return la data di nascita dell'utente
 */
    public LocalDate getDataNascita() {
        return dataNascita;
    }

/**
 * Restituisce il domicilio dell'utente che si registra.
 * @return il domicilio dell'utente
 */
    public String getDomicilio() {
        return domicilio;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto RegistrazioneDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "RegistrazioneDTO [nome=" + nome + ", cognome=" + cognome + ", username=" + username + 
            ", email=" + email + ", ruolo=" + ruolo + 
            ", dataNascita=" + dataNascita + ", domicilio=" + domicilio + "]";
    }
}