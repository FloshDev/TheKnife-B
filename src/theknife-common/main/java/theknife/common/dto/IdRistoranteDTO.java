package theknife.common.dto;

/**
 * Rappresenta l'identificatore di un ristorante, utilizzato per le richieste di dettaglio o modifica.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class IdRistoranteDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * L'identificatore univoco del ristorante.
 */
    private long idRistorante;

//Costruttore
/**
 * Crea il DTO per l'identificatore di un ristorante con l'identificatore specificato.
 * @param idRistorante l'identificatore del ristorante
 */
    public IdRistoranteDTO(long idRistorante) {
        this.idRistorante = idRistorante;
    }

//Getter e Setter
/**
 * Restituisce l'identificatore del ristorante.
 * @return l'identificatore del ristorante
 */
    public long getIdRistorante() {
        return idRistorante;
    }

/**
 * Imposta l'identificatore del ristorante.
 * @param idRistorante l'identificatore del ristorante
 */
    public void setIdRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto IdRistoranteDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "IdRistoranteDTO [idRistorante=" + idRistorante + "]";
    }
}
