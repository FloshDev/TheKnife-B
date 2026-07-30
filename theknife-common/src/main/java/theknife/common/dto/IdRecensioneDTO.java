package theknife.common.dto;

public class IdRecensioneDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long IdRecensione;
//Costruttore
    public IdRecensioneDTO(long idRecensione) {
        this.IdRecensione = idRecensione;
    }
//Getter
    public long getIdRecensione() {
        return IdRecensione;
    }
//Setter
    public void setIdRecensione(long x) {
        this.IdRecensione = x;
    }
//Metodo toString
    @Override
    public String toString() {
        return "IdRecensioneDTO [IdRecensione=" + IdRecensione + "]";
    }
}
