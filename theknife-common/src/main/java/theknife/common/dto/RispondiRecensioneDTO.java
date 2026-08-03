package theknife.common.dto;

/**
 * Rappresenta i dati necessari per la richiesta di risposta ad una recensione da parte di un utente.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class RispondiRecensioneDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long idRecensione;
    private String risposta;

//Costruttore
    public RispondiRecensioneDTO(long idRecensione, String risposta) {
        this.idRecensione = idRecensione;
        this.risposta = risposta;
    }

//Getters e Setters
    public long getIdRecensione() {
        return idRecensione;
    }

    public void setIdRecensione(long idRecensione) {
        this.idRecensione = idRecensione;
    }

    public String getRisposta() {
        return risposta;
    }

    public void setRisposta(String risposta) {
        this.risposta = risposta;
    }

//Metodo toString
    @Override
    public String toString() {
        return "RispondiRecensioneDTO [idRecensione=" + idRecensione + 
            ", risposta=" + risposta + "]";
    }
}
