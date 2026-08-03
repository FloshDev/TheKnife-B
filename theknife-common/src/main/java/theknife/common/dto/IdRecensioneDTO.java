package theknife.common.dto;

/**
 * Rappresenta l'identificatore di una recensione, utilizzato per le richieste di eliminazione o risposta.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class IdRecensioneDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long idRecensione;
//Costruttore
    public IdRecensioneDTO(long idRecensione) {
        this.idRecensione = idRecensione;
    }
//Getter
    public long getIdRecensione() {
        return idRecensione;
    }
//Setter
    public void setIdRecensione(long x) {
        this.idRecensione = x;
    }
//Metodo toString
    @Override
    public String toString() {
        return "IdRecensioneDTO [idRecensione=" + idRecensione + "]";
    }
}
