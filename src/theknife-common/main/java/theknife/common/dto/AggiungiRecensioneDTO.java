package theknife.common.dto;

/**
 * Rappresenta i dati necessari per la richiesta di aggiunta di una recensione da parte di un utente.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class AggiungiRecensioneDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e 
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
/**
 * L'identificatore univoco del ristorante per cui aggiungere la recensione.
 */
    private long idRistorante;
/**
 * Il titolo della recensione da aggiungere.
 */
    private String titolo;
/**
 * Il testo della recensione da aggiungere.
 */
    private String testo;
/**
 * Il numero di stelle della recensione da aggiungere.
 */
    private int stelle;

//Costruttore
/**
 * Crea il DTO per l'aggiunta di una recensione con i dati specificati.
 * @param idRistorante l'identificatore del ristorante per cui aggiungere la recensione
 * @param titolo il titolo della recensione da aggiungere
 * @param testo il testo della recensione da aggiungere
 * @param stelle il numero di stelle della recensione da aggiungere
 */
    public AggiungiRecensioneDTO(long idRistorante, String titolo, String testo, int stelle) {
        this.idRistorante = idRistorante;
        this.titolo = titolo;
        this.testo = testo;
        this.stelle = stelle;
    }

//Getters e Setters
/**
 * Restituisce l'identificatore del ristorante per cui aggiungere la recensione.
 * @return l'identificatore del ristorante
 */
    public long getIdRistorante() {
        return idRistorante;
    }

/**
 * Imposta l'identificatore del ristorante per cui aggiungere la recensione.
 * @param idRistorante l'identificatore del ristorante
 */
    public void setIdRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
    }

/**
 * Restituisce il titolo della recensione da aggiungere.
 * @return il titolo della recensione
 */
    public String getTitolo() {
        return titolo;
    }

/**
 * Imposta il titolo della recensione da aggiungere.
 * @param titolo il titolo della recensione
 */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

/**
 * Restituisce il testo della recensione da aggiungere.
 * @return il testo della recensione
 */
    public String getTesto() {
        return testo;
    }

/**
 * Imposta il testo della recensione da aggiungere.
 * @param testo il testo della recensione
 */
    public void setTesto(String testo) {
        this.testo = testo;
    }

/**
 * Restituisce il numero di stelle della recensione da aggiungere.
 * @return il numero di stelle della recensione
 */
    public int getStelle() {
        return stelle;
    }

/**
 * Imposta il numero di stelle della recensione da aggiungere.
 * @param stelle il numero di stelle della recensione
 */
    public void setStelle(int stelle) {
        this.stelle = stelle;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto AggiungiRecensioneDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "AggiungiRecensioneDTO [idRistorante=" + idRistorante + ", titolo=" + titolo + 
            ", testo=" + testo + ", stelle=" + stelle + "]";
    }
}
