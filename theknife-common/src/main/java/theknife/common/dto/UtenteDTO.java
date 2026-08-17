package theknife.common.dto;
import java.time.LocalDate;
import theknife.common.enums.Ruolo;

/**
 * Rappresenta i dati di un utente trasferiti tra client e server.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class UtenteDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e 
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * L'identificatore univoco dell'utente.
 */
    private long idUtente;
    
/**
 * Lo username dell'utente.
 */
    private String username;
    
/**
 * Il nome dell'utente.
 */
    private String nome;
    
/**
 * Il cognome dell'utente.
 */
    private String cognome;
    
/**
 * L'indirizzo email dell'utente.
 */
    private String email;
    
/**
 * Il ruolo dell'utente.
 */
    private Ruolo ruolo;
    
/**
 * La data di nascita dell'utente.
 */
    private LocalDate dataNascita;
    
/**
 * Il domicilio dell'utente.
 */
    private String domicilio;

//Costruttore
/**
 * Crea il DTO per l'utente con i dati specificati.
 * @param idUtente l'identificatore dell'utente
 * @param username l'username dell'utente
 * @param nome il nome dell'utente
 * @param cognome il cognome dell'utente
 * @param email l'indirizzo email dell'utente
 * @param ruolo il ruolo dell'utente
 * @param dataNascita la data di nascita dell'utente
 * @param domicilio il domicilio dell'utente
 */
    public UtenteDTO(long idUtente, String username, String nome, String cognome, String email, 
            Ruolo ruolo, LocalDate dataNascita, String domicilio) {
        this.idUtente = idUtente;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.ruolo = ruolo;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
    }

//Getters e Setters
/**
 * Restituisce l'identificatore dell'utente.
 * @return l'identificatore dell'utente
 */
    public long getIdUtente() {
        return idUtente;
    }

/**
 * Imposta l'identificatore dell'utente.
 * @param idUtente l'identificatore dell'utente
 */
    public void setIdUtente(long idUtente) {
        this.idUtente = idUtente;
    }

/**
 * Restituisce lo username dell'utente.
 * @return lo username dell'utente
 */
    public String getUsername() {
        return username;
    }

/**
 * Imposta lo username dell'utente.
 * @param username lo username dell'utente
 */
    public void setUsername(String username) {
        this.username = username;
    }

/**
 * Restituisce il nome dell'utente.
 * @return il nome dell'utente
 */
    public String getNome() {
        return nome;
    }

/**
 * Imposta il nome dell'utente.
 * @param nome il nome dell'utente
 */
    public void setNome(String nome) {
        this.nome = nome;
    }

/**
 * Restituisce il cognome dell'utente.
 * @return il cognome dell'utente
 */
    public String getCognome() {
        return cognome;
    }

/**
 * Imposta il cognome dell'utente.
 * @param cognome il cognome dell'utente
 */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

/**
 * Restituisce l'indirizzo email dell'utente.
 * @return l'indirizzo email dell'utente
 */
    public String getEmail() {
        return email;
    }

/**
 * Imposta l'indirizzo email dell'utente.
 * @param email l'indirizzo email dell'utente
 */
    public void setEmail(String email) {
        this.email = email;
    }

/**
 * Restituisce il ruolo dell'utente.
 * @return il ruolo dell'utente
 */
    public Ruolo getRuolo() {
        return ruolo;
    }

/**
 * Imposta il ruolo dell'utente.
 * @param ruolo il ruolo dell'utente
 */
    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

/**
 * Restituisce la data di nascita dell'utente.
 * @return la data di nascita dell'utente
 */
    public LocalDate getDataNascita() {
        return dataNascita;
    }

/**
 * Imposta la data di nascita dell'utente.
 * @param dataNascita la data di nascita dell'utente
 */
    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

/**
 * Resituisce il domicilio dell'utente.
 * @return il domicilio dell'utente
 */
    public String getDomicilio() {
        return domicilio;
    }

/**
 * Imposta il domicilio dell'utente.
 * @param domicilio il domicilio dell'utente
 */
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto UtenteDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "UtenteDTO [idUtente=" + idUtente + ", username=" + username + ", nome=" + nome + 
            ", cognome=" + cognome + ", email=" + email + ", ruolo=" + ruolo + 
            ", dataNascita=" + dataNascita + ", domicilio=" + domicilio + "]";
    }
}
