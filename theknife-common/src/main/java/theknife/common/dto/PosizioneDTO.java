package theknife.common.dto;

public class PosizioneDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private double latitudine;
    private double longitudine;

//Costruttore
    public PosizioneDTO(double latitudine, double longitudine) {
        this.latitudine = latitudine;
        this.longitudine = longitudine;
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

//Metodo toString
    @Override
    public String toString() {
        return "PosizioneDTO [latitudine=" + latitudine + ", longitudine=" + longitudine + "]";
    }
}
