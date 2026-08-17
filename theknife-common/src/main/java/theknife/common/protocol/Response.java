package theknife.common.protocol;
import java.io.Serializable;
import theknife.common.enums.ResponseStatus;

/**
 * Rappresenta una risposta inviata dal server al client.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class Response implements Serializable{
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * Lo stato della risposta.
 */
    private ResponseStatus status;
    
/**
 * L'oggetto payload contenente i dati associati alla risposta.
 */
    private Object payload;
    
/**
 * Il messaggio trasmesso dalla risposta, utile per comunicare informazioni aggiuntive o errori specifici 
 * al client.
 */
    private String messaggio;

//Costruttore
/**
 * Costruisce una nuova risposta con lo stato, il payload e il messaggio specificati.
 * @param status lo stato della risposta
 * @param payload l'oggetto payload
 * @param messaggio il messaggio trasmesso
 */
    public Response(ResponseStatus status, Object payload, String messaggio) {
        this.status = status;
        this.payload = payload;
        this.messaggio = messaggio;
    }

//Setters
/**
 * Imposta lo stato della risposta.
 * @param status lo stato della risposta
 */
    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

/**
 * Imposta l'oggetto payload della risposta.
 * @param payload l'oggetto payload della risposta
 */
    public void setPayload(Object payload) {
        this.payload = payload;
    }

/**
 * Imposta il messaggio trasmesso dalla risposta.
 * @param messaggio il messaggio trasmesso dalla risposta
 */
    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

//Getters
/**
 * Restituisce lo stato della risposta.
 * @return lo stato della risposta
 */
    public ResponseStatus getStatus() {
        return status;
    }

/**
 * Restituisce l'oggetto payload della risposta.
 * @return l'oggetto payload della risposta
 */
    public Object getPayload() {
        return payload;
    } 

/**
 * Restituisce il messaggio trasmesso dalla risposta.
 * @return il messaggio trasmesso dalla risposta
 */
    public String getMessaggio() {
        return messaggio;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto Response.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "Response [status=" + status + ", payload=" + payload + 
            ", messaggio=" + messaggio + "]";
    }
}
