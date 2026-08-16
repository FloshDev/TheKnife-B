package theknife.common.dto;

/**
 * Rappresenta i dati necessari per la richiesta di ricerca di ristoranti vicini.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class CercaVicinoDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * Il raggio in chilometri entro il quale cercare i ristoranti vicini.
 */
    private double raggioKm;
    
/**
 * Il luogo di riferimento per la ricerca dei ristoranti vicini.
 */
    private String luogo;

//Costruttore
/**
 * Crea il DTO per la ricerca di ristoranti vicini con i criteri specificati.
 * @param raggioKm il raggio in chilometri entro il quale cercare i ristoranti vicini
 * @param luogo il luogo di riferimento per la ricerca dei ristoranti vicini
 */
    public CercaVicinoDTO(double raggioKm, String luogo) {
        this.raggioKm = raggioKm;
        this.luogo = luogo;
    }

//Getters
/**
 * Restituisce il raggio in chilometri entro il quale cercare i ristoranti vicini.
 * @return il raggio in chilometri
 */
    public double getRaggioKm() {
        return raggioKm;
    }

/**
 * Restituisce il luogo di riferimento per la ricerca dei ristoranti vicini.
 * @return il luogo di riferimento
 */
    public String getLuogo() {
        return luogo;
    }

//Setter
/**
 * Imposta il raggio in chilometri entro il quale cercare i ristoranti vicini.
 * @param raggioKm il raggio in chilometri
 */
    public void setRaggioKm(double raggioKm) {
        this.raggioKm = raggioKm;
    }

/**
 * Imposta il luogo di riferimento per la ricerca dei ristoranti vicini.
 * @param luogo il luogo di riferimento
 */
    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto CercaVicinoDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "CercaVicinoDTO [raggioKm=" + raggioKm + ", luogo=" + luogo + "]";
    }
}
