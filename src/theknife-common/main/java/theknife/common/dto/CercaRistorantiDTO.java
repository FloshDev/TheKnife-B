package theknife.common.dto;
import java.util.List;

/**
 * Rappresenta i criteri di ricerca utilizzati nella richiesta di ricerca dei ristoranti da parte 
 * di un utente.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class CercaRistorantiDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;

/**
 * Il nome del ristorante da cercare.
 */
    private String nome;
    
/**
 * La città in cui cercare i ristoranti.
 */
    private String citta;
    
/**
 * Il tipo di cucina dei ristoranti da cercare.
 */
    private String tipoCucina;
    
/**
 * Indica se i ristoranti da cercare offrono la prenotazione online.
 */
    private Boolean prenotazioneOnline;
    
/**
 * La fascia di prezzo dei ristoranti da cercare.
 */
    private int fasciaPrezzo;
    
/**
 * Indica se i ristoranti da cercare offrono la consegna a domicilio.
 */
    private Boolean consegnaADomicilio;
    
/**
 * La lista dei servizi disponibili nei ristoranti da cercare.
 */
    private List<ServizioDTO> servizi;

//Costruttore
/**
 * Crea il DTO per la ricerca dei ristoranti con i criteri specificati.
 * @param nome il nome del ristorante da cercare
 * @param citta la città in cui cercare i ristoranti
 * @param tipoCucina il tipo di cucina dei ristoranti da cercare
 * @param prenotazioneOnline indica se i ristoranti da cercare offrono la prenotazione online
 * @param fasciaPrezzo la fascia di prezzo dei ristoranti da cercare
 * @param consegnaADomicilio indica se i ristoranti da cercare offrono la consegna a domicilio
 * @param servizi la lista dei servizi disponibili nei ristoranti da cercare
 */
    public CercaRistorantiDTO(String nome, String citta, String tipoCucina, 
            Boolean prenotazioneOnline, int fasciaPrezzo, Boolean consegnaADomicilio, 
            List<ServizioDTO> servizi) {
        this.nome = nome;
        this.citta = citta;
        this.tipoCucina = tipoCucina;
        this.prenotazioneOnline = prenotazioneOnline;
        this.fasciaPrezzo = fasciaPrezzo;
        this.consegnaADomicilio = consegnaADomicilio;
        this.servizi = servizi;
    }

//Getters e Setters
/**
 * Restituisce il nome del ristorante da cercare.
 * @return il nome del ristorante
 */
    public String getNome() {
        return nome;
    }

/**
 * Imposta il nome del ristorante da cercare.
 * @param nome il nome del ristorante
 */
    public void setNome(String nome) {
        this.nome = nome;
    }

/**
 * Restituisce la città in cui cercare i ristoranti.
 * @return la città in cui cercare i ristoranti
 */
    public String getCitta() {
        return citta;
    }

/**
 * Imposta la città in cui cercare i ristoranti.
 * @param citta la città in cui cercare i ristoranti
 */
    public void setCitta(String citta) {
        this.citta = citta;
    }

/**
 * Restituisce il tipo di cucina dei ristoranti da cercare.
 * @return il tipo di cucina dei ristoranti da cercare
 */
    public String getTipoCucina() {
        return tipoCucina;
    }

/**
 * Imposta il tipo di cucina dei ristoranti da cercare.
 * @param tipoCucina il tipo di cucina dei ristoranti da cercare
 */
    public void setTipoCucina(String tipoCucina) {
        this.tipoCucina = tipoCucina;
    }

/**
 * Restituisce se i ristoranti da cercare offrono la prenotazione online.
 * @return true se i ristoranti offrono la prenotazione online, false altrimenti
 */
    public Boolean isPrenotazioneOnline() {
        return prenotazioneOnline;
    }

/**
 * Imposta se i ristoranti da cercare offrono la prenotazione online.
 * @param prenotazioneOnline true se i ristoranti offrono la prenotazione online, false altrimenti
 */
    public void setPrenotazioneOnline(Boolean prenotazioneOnline) {
        this.prenotazioneOnline = prenotazioneOnline;
    }

/**
 * Restituisce la fascia di prezzo dei ristoranti da cercare.
 * @return la fascia di prezzo dei ristoranti da cercare
 */
    public int getFasciaPrezzo() {
        return fasciaPrezzo;
    }

/**
 * Imposta la fascia di prezzo dei ristoranti da cercare.
 * @param fasciaPrezzo la fascia di prezzo dei ristoranti da cercare
 */
    public void setFasciaPrezzo(int fasciaPrezzo) {
        this.fasciaPrezzo = fasciaPrezzo;
    }

/**
 * Restituisce se i ristoranti da cercare offrono la consegna a domicilio.
 * @return true se i ristoranti offrono la consegna a domicilio, false altrimenti
 */
    public Boolean isConsegnaADomicilio() {
        return consegnaADomicilio;
    }

/**
 * Imposta se i ristoranti da cercare offrono la consegna a domicilio.
 * @param consegnaADomicilio true se i ristoranti offrono la consegna a domicilio, false altrimenti
 */
    public void setConsegnaADomicilio(Boolean consegnaADomicilio) {
        this.consegnaADomicilio = consegnaADomicilio;
    }

/**
 * Restituisce la lista dei servizi disponibili nei ristoranti da cercare.
 * @return la lista dei servizi disponibili nei ristoranti da cercare
 */
    public List<ServizioDTO> getServizi() {
        return servizi;
    }

/**
 * Imposta la lista dei servizi disponibili nei ristoranti da cercare.
 * @param servizi la lista dei servizi disponibili nei ristoranti da cercare
 */
    public void setServizi(List<ServizioDTO> servizi) {
        this.servizi = servizi;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto CercaRistorantiDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "CercaRistorantiDTO [nome=" + nome + ", citta=" + citta + 
            ", tipoCucina=" + tipoCucina + ", prenotazioneOnline=" + prenotazioneOnline + 
            ", fasciaPrezzo=" + fasciaPrezzo + ", consegnaADomicilio=" + consegnaADomicilio + 
            ", servizi=" + servizi + "]";
    }
}
