package theknife.common.dto;

public class CercaVicinoDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private double raggioKm;
//Costruttore
    public CercaVicinoDTO(double raggioKm) {
        this.raggioKm = raggioKm;
    }
//Getter
    public double getRaggioKm() {
        return raggioKm;
    }
//Setter
    public void setRaggioKm(double raggioKm) {
        this.raggioKm = raggioKm;
    }
//Metodo toString
    @Override
    public String toString() {
        return "CercaVicinoDTO [raggioKm=" + raggioKm + "]";
    }
}
