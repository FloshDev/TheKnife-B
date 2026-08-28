package theknife.common.dto;

/**
 * Rappresenta i dati necessari per la richiesta di risposta ad una recensione da parte di un utente.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class RispondiRecensioneDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e 
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * L'identificatore univoco della recensione a cui rispondere.
 */
    private long idRecensione;

/**
 * La risposta alla recensione.
 */
    private String risposta;

//Costruttore
/**
 * Crea il DTO per la risposta ad una recensione con l'identificatore della recensione e la risposta 
 * specificati.
 * @param idRecensione l'identificatore della recensione
 * @param risposta la risposta alla recensione
 */
    public RispondiRecensioneDTO(long idRecensione, String risposta) {
        this.idRecensione = idRecensione;
        this.risposta = risposta;
    }

//Getters e Setters
/**
 * Restituisce l'identificatore della recensione a cui rispondere.
 * @return l'identificatore della recensione
 */
    public long getIdRecensione() {
        return idRecensione;
    }

/**
 * Imposta l'identificatore della recensione a cui rispondere.
 * @param idRecensione l'identificatore della recensione
 */
    public void setIdRecensione(long idRecensione) {
        this.idRecensione = idRecensione;
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

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto RispondiRecensioneDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "RispondiRecensioneDTO [idRecensione=" + idRecensione + 
            ", risposta=" + risposta + "]";
    }
}
