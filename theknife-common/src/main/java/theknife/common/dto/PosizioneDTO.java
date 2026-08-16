package theknife.common.dto;

/**
 * Rappresenta una posizione geografica mediante coordinate di latitudine e longitudine, insieme al
 * luogo ricavato tramite geocoding dal server.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class PosizioneDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e 
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * La latitudine della posizione geografica.
 */
    private double latitudine;
    
/**
 * La longitudine della posizione geografica.
 */
    private double longitudine;
    
/**
 * Il luogo ricavato tramite geocoding dal server.
 */
    private String luogo;

//Costruttore
/**
 * Crea il DTO per la posizione geografica con le coordinate di latitudine e longitudine e il luogo 
 * specificati.
 * @param latitudine la latitudine della posizione geografica
 * @param longitudine la longitudine della posizione geografica
 * @param luogo il luogo ricavato tramite geocoding dal server
 */
    public PosizioneDTO(double latitudine, double longitudine, String luogo) {
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.luogo = luogo;
    }

//Getters e Setters
/**
 * Restituisce la latitudine della posizione geografica.
 * @return la latitudine della posizione geografica
 */
    public double getLatitudine() {
        return latitudine;
    }

/**
 * Imposta la latitudine della posizione geografica.
 * @param latitudine la latitudine della posizione geografica
 */
    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

/**
 * Restituisce la longitudine della posizione geografica.
 * @return la longitudine della posizione geografica
 */
    public double getLongitudine() {
        return longitudine;
    }

/**
 * Imposta la longitudine della posizione geografica.
 * @param longitudine la longitudine della posizione geografica
 */
    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

/**
 * Restituisce il luogo ricavato tramite geocoding dal server.
 * @return il luogo ricavato tramite geocoding dal server
 */
    public String getLuogo() {
        return luogo;
    }

/**
 * Imposta il luogo ricavato tramite geocoding dal server.
 * @param luogo il luogo ricavato tramite geocoding dal server
 */
    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto PosizioneDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "PosizioneDTO [latitudine=" + latitudine + ", longitudine=" + longitudine + 
            ", luogo=" + luogo + "]";
    }
}
