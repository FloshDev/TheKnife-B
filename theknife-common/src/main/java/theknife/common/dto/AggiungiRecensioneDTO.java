package theknife.common.dto;

public class AggiungiRecensioneDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long idRistorante;
    private String titolo;
    private String testo;
    private int stelle;
//Costruttore
    public AggiungiRecensioneDTO(long idRistorante, String titolo, String testo, int stelle) {
        this.idRistorante = idRistorante;
        this.titolo = titolo;
        this.testo = testo;
        this.stelle = stelle;
    }
//Getters e Setters
    public long getIdRistorante() {
        return idRistorante;
    }

    public void setIdRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public int getStelle() {
        return stelle;
    }

    public void setStelle(int stelle) {
        this.stelle = stelle;
    }
//Metodo toString
    @Override
    public String toString() {
        return "AggiungiRecensioneDTO [idRistorante=" + idRistorante + ", titolo=" + titolo + 
            ", testo=" + testo + ", stelle=" + stelle + "]";
    }
}
