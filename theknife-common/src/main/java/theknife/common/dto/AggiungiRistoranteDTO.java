package theknife.common.dto;
import java.util.List;

/**
 * Rappresenta i dati necessari per la richiesta di aggiunta di un ristorante da parte di un utente.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class AggiungiRistoranteDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e 
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;

/**
 * Il nome del ristorante da aggiungere.
 */
    private String nome;
    
/**
 * L'indirizzo del ristorante da aggiungere.
 */
    private String indirizzo;
    
/**
 * La città in cui si trova il ristorante da aggiungere.
 */
    private String citta;
    
/**
 * La provincia in cui si trova il ristorante da aggiungere.
 */
    private String provincia;
    
/**
 * La nazione in cui si trova il ristorante da aggiungere.
 */
    private String nazione;
    
/**
 * La fascia di prezzo del ristorante da aggiungere.
 */
    private int fasciaPrezzo;
    
/**
 * Indica se il ristorante offre la prenotazione online.
 */
    private boolean prenotazioneOnline;
    
/**
 * Indica se il ristorante offre la consegna a domicilio.
 */
    private boolean consegnaADomicilio;
    
/**
 * Il tipo di cucina del ristorante da aggiungere.
 */
    private String tipoCucina;
    
/**
 * Il sito web del ristorante da aggiungere.
 */
    private String website;
    
/**
 * Il numero di telefono del ristorante da aggiungere.
 */
    private String telefono;
    
/**
 * I premi o riconoscimenti del ristorante da aggiungere.
 */
    private String premi;
    
/**
 * La lista dei servizi offerti dal ristorante da aggiungere.
 */
    private List<ServizioDTO> servizi;

//Costruttore
/**
 * Crea il DTO per l'aggiunta di un ristorante con i dati specificati.
 * @param nome il nome del ristorante da aggiungere
 * @param indirizzo l'indirizzo del ristorante da aggiungere
 * @param citta la città in cui si trova il ristorante da aggiungere
 * @param provincia la provincia in cui si trova il ristorante da aggiungere
 * @param nazione la nazione in cui si trova il ristorante da aggiungere
 * @param fasciaPrezzo la fascia di prezzo del ristorante da aggiungere
 * @param prenotazioneOnline indica se il ristorante offre la prenotazione online
 * @param consegnaADomicilio indica se il ristorante offre la consegna a domicilio
 * @param tipoCucina il tipo di cucina del ristorante da aggiungere
 * @param website il sito web del ristorante da aggiungere
 * @param telefono il numero di telefono del ristorante da aggiungere
 * @param premi i premi o riconoscimenti del ristorante da aggiungere
 * @param servizi la lista dei servizi offerti dal ristorante da aggiungere
 */
    public AggiungiRistoranteDTO(String nome, String indirizzo, String citta, String provincia, String nazione, 
            int fasciaPrezzo, boolean prenotazioneOnline, boolean consegnaADomicilio, 
            String tipoCucina, String website, String telefono, String premi, List<ServizioDTO> servizi) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.citta = citta;
        this.provincia = provincia;
        this.nazione = nazione;
        this.fasciaPrezzo = fasciaPrezzo;
        this.prenotazioneOnline = prenotazioneOnline;
        this.consegnaADomicilio = consegnaADomicilio;
        this.tipoCucina = tipoCucina;
        this.website = website;
        this.premi = premi;
        this.servizi = servizi;
        this.telefono = telefono;
    }

//Getters e Setters
/**
 * Restituisce il nome del ristorante da aggiungere.
 * @return nome del ristorante
 */
    public String getNome() {
        return nome;
    }

/**
 * Imposta il nome del ristorante da aggiungere.
 * @param nome nome del ristorante
 */
    public void setNome(String nome) {
        this.nome = nome;
    }

/**
 * Restituisce l'indirizzo del ristorante da aggiungere.
 * @return indirizzo del ristorante
 */
    public String getIndirizzo() {
        return indirizzo;
    }

/**
 * Imposta l'indirizzo del ristorante da aggiungere.
 * @param indirizzo indirizzo del ristorante
 */
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

/**
 * Restituisce la città in cui si trova il ristorante da aggiungere.
 * @return città del ristorante
 */
    public String getCitta() {
        return citta;
    }

/**
 * Imposta la città in cui si trova il ristorante da aggiungere.
 * @param citta città del ristorante
 */
    public void setCitta(String citta) {
        this.citta = citta;
    }

/**
 * Restituisce la provincia in cui si trova il ristorante da aggiungere.
 * @return provincia del ristorante
 */
    public String getProvincia() {
        return provincia;
    }

/**
 * Imposta la provincia in cui si trova il ristorante da aggiungere.
 * @param provincia provincia del ristorante
 */
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

/**
 * Restituisce la nazione in cui si trova il ristorante da aggiungere.
 * @return nazione del ristorante
 */
    public String getNazione() {
        return nazione;
    }

/**
 * Imposta la nazione in cui si trova il ristorante da aggiungere.
 * @param nazione nazione del ristorante
 */
    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

/**
 * Restituisce la fascia di prezzo del ristorante da aggiungere.
 * @return fascia di prezzo del ristorante
 */
    public int getFasciaPrezzo() {
        return fasciaPrezzo;
    }

/**
 * Imposta la fascia di prezzo del ristorante da aggiungere.
 * @param fasciaPrezzo fascia di prezzo del ristorante
 */
    public void setFasciaPrezzo(int fasciaPrezzo) {
        this.fasciaPrezzo = fasciaPrezzo;
    }

/**
 * Restituisce se il ristorante offre la prenotazione online.
 * @return true se il ristorante offre la prenotazione online, false altrimenti
 */
    public boolean isPrenotazioneOnline() {
        return prenotazioneOnline;
    }

/**
 * Imposta se il ristorante offre la prenotazione online.
 * @param prenotazioneOnline true se il ristorante offre la prenotazione online, false altrimenti
 */
    public void setPrenotazioneOnline(boolean prenotazioneOnline) {
        this.prenotazioneOnline = prenotazioneOnline;
    }

/**
 * Restituisce se il ristorante offre la consegna a domicilio.
 * @return true se il ristorante offre la consegna a domicilio, false altrimenti
 */
    public boolean isConsegnaADomicilio() {
        return consegnaADomicilio;
    }

/**
 * Imposta se il ristorante offre la consegna a domicilio.
 * @param consegnaADomicilio true se il ristorante offre la consegna a domicilio, false altrimenti
 */
    public void setConsegnaADomicilio(boolean consegnaADomicilio) {
        this.consegnaADomicilio = consegnaADomicilio;
    }

/**
 * Restituisce il tipo di cucina del ristorante da aggiungere.
 * @return tipo di cucina del ristorante
 */
    public String getTipoCucina() {
        return tipoCucina;
    }

/**
 * Imposta il tipo di cucina del ristorante da aggiungere.
 * @param tipoCucina tipo di cucina del ristorante
 */
    public void setTipoCucina(String tipoCucina) {
        this.tipoCucina = tipoCucina;
    }

/**
 * Restituisce il sito web del ristorante da aggiungere.
 * @return sito web del ristorante
 */
    public String getWebsite() {
        return website;
    }

/**
 * Imposta il sito web del ristorante da aggiungere.
 * @param website sito web del ristorante
 */
    public void setWebsite(String website) {
        this.website = website;
    }

/**
 * Restituisce i premi o riconoscimenti del ristorante da aggiungere.
 * @return premi o riconoscimenti del ristorante
 */
    public String getPremi() {
        return premi;
    }

/**
 * Imposta i premi o riconoscimenti del ristorante da aggiungere.
 * @param premi premi o riconoscimenti del ristorante
 */
    public void setPremi(String premi) {
        this.premi = premi;
    }

/**
 * Restituisce la lista dei servizi offerti dal ristorante da aggiungere.
 * @return lista dei servizi offerti dal ristorante
 */
    public List<ServizioDTO> getServizi() {
        return servizi;
    }

/**
 * Imposta la lista dei servizi offerti dal ristorante da aggiungere.
 * @param servizi lista dei servizi offerti dal ristorante
 */
    public void setServizi(List<ServizioDTO> servizi) {
        this.servizi = servizi;
    }

/**
 * Restituisce il numero di telefono del ristorante da aggiungere.
 * @return numero di telefono del ristorante
 */
    public String getTelefono() {
        return telefono;
    }

/**
 * Imposta il numero di telefono del ristorante da aggiungere.
 * @param telefono numero di telefono del ristorante
 */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto AggiungiRistoranteDTO.
 * @return rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "AggiungiRistoranteDTO{" +
                "nome='" + nome + '\'' +
                ", indirizzo='" + indirizzo + '\'' +
                ", citta='" + citta + '\'' +
                ", provincia='" + provincia + '\'' +
                ", nazione='" + nazione + '\'' +
                ", fasciaPrezzo='" + fasciaPrezzo + '\'' +
                ", prenotazioneOnline=" + prenotazioneOnline +
                ", consegnaADomicilio=" + consegnaADomicilio +
                ", tipoCucina='" + tipoCucina + '\'' +
                ", website='" + website + '\'' +
                ", telefono='" + telefono + '\'' +
                ", premi='" + premi + '\'' +
                ", servizi=" + servizi +
                '}';
    }
}
