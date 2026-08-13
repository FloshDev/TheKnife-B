package theknife.common.dto;

/**
 * Rappresenta una posizione geografica mediante coordinate di latitudine e longitudine, insieme al
 * luogo ricavato tramite geocoding dal server.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class PosizioneDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private double latitudine;
    private double longitudine;
    private String luogo;

//Costruttore
    public PosizioneDTO(double latitudine, double longitudine, String luogo) {
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.luogo = luogo;
    }

//Getters e Setters
    public double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

//Metodo toString
    @Override
    public String toString() {
        return "PosizioneDTO [latitudine=" + latitudine + ", longitudine=" + longitudine + 
            ", luogo=" + luogo + "]";
    }
}
