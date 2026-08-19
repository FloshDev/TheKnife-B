package theknife.common.dto;
import java.time.LocalDateTime;

/**
 * Rappresenta i dati di una recensione trasferiti tra client e server.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class RecensioneDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e 
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;

/**
 * L'identificatore univoco della recensione.
 */
    private long idRecensione;
    
/**
 * L'identificatore univoco del ristorante per cui è stata scritta la recensione.
 */
    private long idRistorante;
    
/**
 * Il titolo della recensione.
 */
    private String titolo;
    
/**
 * Il testo della recensione.
 */
    private String testo;
    
/**
 * Il numero di stelle assegnate alla recensione.
 */
    private int stelle;
    
/**
 * L'identificatore univoco dell'utente che ha scritto la recensione.
 */
    private long idUtente;
    
/**
 * Lo username dell'utente che ha scritto la recensione.
 */
    private String username;

/**
 * La data e l'ora di pubblicazione della recensione.
 */
    private LocalDateTime dataPubblicazione;
    
/**
 * La risposta alla recensione.
 */
    private String risposta;
    
/**
 * La data e l'ora di pubblicazione della risposta alla recensione.
 */
    private LocalDateTime dataRisposta;

//Costruttore
/**
 * Crea il DTO per la recensione con i dati specificati.
 * @param idRecensione l'identificatore della recensione
 * @param idRistorante l'identificatore del ristorante
 * @param titolo il titolo della recensione
 * @param testo il testo della recensione
 * @param stelle il numero di stelle della recensione
 * @param idUtente l'idetificatore dell'utente
 * @param username lo username dell'utente
 * @param dataPubblicazione la data e l'ora di pubblicazione della recensione
 * @param risposta la risposta alla recensione
 * @param dataRisposta la data e l'ora di pubblicazione della risposta alla recensione
 */
    public RecensioneDTO(long idRecensione, long idRistorante, String titolo, String testo, 
            int stelle, long idUtente, String username, LocalDateTime dataPubblicazione, String risposta, 
            LocalDateTime dataRisposta) {
        this.idRecensione = idRecensione;
        this.idRistorante = idRistorante;
        this.titolo = titolo;
        this.testo = testo;
        this.stelle = stelle;
        this.idUtente = idUtente;
        this.username = username;
        this.dataPubblicazione = dataPubblicazione;
        this.risposta = risposta;
        this.dataRisposta = dataRisposta;
    }

//Getters e Setters
/**
 * Restituisce l'identificatore della recensione.
 * @return l'identificatore della recensione
 */
    public long getIdRecensione() {
        return idRecensione;
    }

/**
 * Imposta l'identificatore della recensione.
 * @param idRecensione l'identificatore della recensione
 */
    public void setIdRecensione(long idRecensione) {
        this.idRecensione = idRecensione;
    }

/**
 * Restituisce l'identificatore del ristorante per cui è stata scritta la recensione.
 * @return l'identificatore del ristorante
 */
    public long getIdRistorante() {
        return idRistorante;
    }

/**
 * Imposta l'identificatore del ristorante per cui è stata scritta la recensione.
 * @param idRistorante l'identificatore del ristorante
 */
    public void setIdRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
    }

/**
 * Restituisce il titolo della recensione.
 * @return il titolo della recensione
 */
    public String getTitolo() {
        return titolo;
    }

/**
 * Imposta il titolo della recensione.
 * @param titolo il titolo della recensione
 */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

/**
 * Restituisce il testo della recensione.
 * @return il testo della recensione
 */
    public String getTesto() {
        return testo;
    }

/**
 * Imposta il testo della recensione.
 * @param testo il testo della recensione
 */
    public void setTesto(String testo) {
        this.testo = testo;
    }

/**
 * Restituisce il numero di stelle della recensione.
 * @return il numero di stelle della recensione
 */
    public int getStelle() {
        return stelle;
    }

/**
 * Imposta il numero di stelle della recensione.
 * @param stelle il numero di stelle della recensione
 */
    public void setStelle(int stelle) {
        this.stelle = stelle;
    }

/**
 * Restituisce l'identificatore dell'utente che ha scritto la recensione.
 * @return l'identificatore dell'utente
 */
    public long getIdUtente() {
        return idUtente;
    }

/**
 * Imposta l'identificatore dell'utente che ha scritto la recensione.
 * @param idUtente l'identificatore dell'utente
 */
    public void setIdUtente(long idUtente) {
        this.idUtente = idUtente;
    }

/**
 * Restituisce lo username dell'utente che ha scritto la recensione.
 * @return lo username dell'utente
 */
    public String getUsername() {
        return username;
    }

/**
 * Imposta lo username dell'utente che ha scritto la recensione.
 * @param username lo username dell'utente
 */
    public void setUsername(String username) {
        this.username = username;
    }

/**
 * Restituisce la data e l'ora di pubblicazione della recensione.
 * @return la data e l'ora di pubblicazione della recensione
 */
    public LocalDateTime getDataPubblicazione() {
        return dataPubblicazione;
    }

/**
 * Imposta la data e l'ora di pubblicazione della recensione.
 * @param dataPubblicazione la data e l'ora di pubblicazione della recensione
 */
    public void setDataPubblicazione(LocalDateTime dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
    }

/**
 * Restituisce la risposta alla recensione.
 * @return la risposta alla recensione
 */
    public String getRisposta() {
        return risposta;
    }

/**
 * Imposta la risposta alla recensione.
 * @param risposta la risposta alla recensione
 */
    public void setRisposta(String risposta) {
        this.risposta = risposta;
    }

/**
 * Restituisce la data e l'ora della risposta alla recensione.
 * @return la data e l'ora della risposta alla recensione
 */
    public LocalDateTime getDataRisposta() {
        return dataRisposta;
    }

/**
 * Imposta la data e l'ora della risposta alla recensione.
 * @param dataRisposta la data e l'ora della risposta alla recensione
 */
    public void setDataRisposta(LocalDateTime dataRisposta) {
        this.dataRisposta = dataRisposta;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto RecensioneDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "RecensioneDTO [idRecensione=" + idRecensione + ", idRistorante=" + idRistorante + 
            ", titolo=" + titolo + ", testo=" + testo + ", stelle=" + stelle + 
            ", idUtente=" + idUtente + ", username=" + username + 
            ", dataPubblicazione=" + dataPubblicazione + 
            ", risposta=" + risposta + ", dataRisposta=" + dataRisposta + "]";
    }
}
