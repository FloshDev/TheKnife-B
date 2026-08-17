package theknife.common.dto;

/**
 * Rappresenta i dati di un servizio offerto da un ristorante trasferiti tra client e server.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class ServizioDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e 
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * L'identificatore univoco del servizio.
 */
    private long idServizio;
    
/**
 * Il nome del servizio.
 */
    private String nomeServizio;

//Costruttore
/**
 * Crea il DTO per il servizio con i dati specificati.
 * @param idServizio l'identificatore del servizio.
 * @param nomeServizio il nome del servizio.
 */
    public ServizioDTO(long idServizio, String nomeServizio) {
        this.idServizio = idServizio;
        this.nomeServizio = nomeServizio;
    }

//Getters e Setters
/**
 * Restituisce l'identificatore del servizio.
 * @return l'identificatore del servizio
 */
    public long getIdServizio() {
        return idServizio;
    }

/**
 * Imposta l'identificatore del servizio.
 * @param idServizio l'identificatore del servizio
 */
    public void setIdServizio(long idServizio) {
        this.idServizio = idServizio;
    }

/**
 * Restituisce il nome del servizio.
 * @return il nome del servizio
 */
    public String getNomeServizio() {
        return nomeServizio;
    }

/**
 * Imposta il nome del servizio.
 * @param nomeServizio il nome del servizio
 */
    public void setNomeServizio(String nomeServizio) {
        this.nomeServizio = nomeServizio;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto ServizioDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "ServizioDTO [idServizio=" + idServizio + ", nomeServizio=" + nomeServizio + "]";
    }
}
