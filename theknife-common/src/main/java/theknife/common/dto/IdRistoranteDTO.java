package theknife.common.dto;

public class IdRistoranteDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long IdRistorante;

//Costruttore
    public IdRistoranteDTO(long idRistorante) {
        this.IdRistorante = idRistorante;
    }
//Getters e Setters
    public long getIdRistorante() {
        return IdRistorante;
    }

    public void setIdRistorante(long idRistorante) {
        this.IdRistorante = idRistorante;
    }
//Metodo toString
    @Override
    public String toString() {
        return "DettagliRistoranteDTO [IdRistorante=" + IdRistorante + "]";
    }
}
