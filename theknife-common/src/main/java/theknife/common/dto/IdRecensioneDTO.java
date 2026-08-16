package theknife.common.dto;

/**
 * Rappresenta l'identificatore di una recensione, utilizzato per le richieste di eliminazione o risposta.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class IdRecensioneDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * L'ID della recensione.
 */
    private long idRecensione;

//Costruttore
/**
 * Crea il DTO per l'identificatore di una recensione con l'ID specificato.
 * @param idRecensione l'ID della recensione
 */
    public IdRecensioneDTO(long idRecensione) {
        this.idRecensione = idRecensione;
    }

//Getter
/**
 * Restituisce l'ID della recensione.
 * @return l'ID della recensione
 */
    public long getIdRecensione() {
        return idRecensione;
    }

//Setter
/**
 * Imposta l'ID della recensione.
 * @param idRecensione l'ID della recensione
 */
    public void setIdRecensione(long x) {
        this.idRecensione = x;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto IdRecensioneDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "IdRecensioneDTO [idRecensione=" + idRecensione + "]";
    }
}
