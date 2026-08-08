package theknife.common.dto;

/**
 * Rappresenta i dati necessari per la richiesta di ricerca di ristoranti vicini.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class CercaVicinoDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private double raggioKm;
    private String luogo;
//Costruttore
    public CercaVicinoDTO(double raggioKm, String luogo) {
        this.raggioKm = raggioKm;
        this.luogo = luogo;
    }
//Getter
    public double getRaggioKm() {
        return raggioKm;
    }

    public String getLuogo() {
        return luogo;
    }
//Setter
    public void setRaggioKm(double raggioKm) {
        this.raggioKm = raggioKm;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }
//Metodo toString
    @Override
    public String toString() {
        return "CercaVicinoDTO [raggioKm=" + raggioKm + ", luogo=" + luogo + "]";
    }
}
