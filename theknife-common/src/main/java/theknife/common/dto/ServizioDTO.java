package theknife.common.dto;

/**
 * Rappresenta i dati di un servizio offerto da un ristorante trasferiti tra client e server.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class ServizioDTO implements java.io.Serializable {
/**
 * 
 */
    private static final long serialVersionUID = 1L;
    
    private long idServizio;
    
    private String nomeServizio;

//Costruttore
    public ServizioDTO(long idServizio, String nomeServizio) {
        this.idServizio = idServizio;
        this.nomeServizio = nomeServizio;
    }

//Getters e Setters
    public long getIdServizio() {
        return idServizio;
    }

    public void setIdServizio(long idServizio) {
        this.idServizio = idServizio;
    }

    public String getNomeServizio() {
        return nomeServizio;
    }

    public void setNomeServizio(String nomeServizio) {
        this.nomeServizio = nomeServizio;
    }

//Metodo toString
    @Override
    public String toString() {
        return "ServizioDTO [idServizio=" + idServizio + ", nomeServizio=" + nomeServizio + "]";
    }
}
