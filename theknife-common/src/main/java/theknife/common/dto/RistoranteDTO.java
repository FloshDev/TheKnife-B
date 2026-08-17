package theknife.common.dto;
import java.util.List;

/**
 * Rappresenta i dati di un ristorante trasferiti tra client e server.
 * 
 * @author Gasparini Lorenzo, 759929, VA
 */

public class RistoranteDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e 
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * L'identificatore univoco del ristorante.
 */
    private long idRistorante;
    
/**
 * Il nome del ristorante.
 */
    private String nome;

/**
 * L'indirizzo del ristorante.
 */
    private String indirizzo;
    
/**
 * La città del ristorante.
 */
    private String citta;
    
/**
 * La provincia del ristorante.
 */
    private String provincia;
    
/**
 * La nazione del ristorante.
 */
    private String nazione;
    
/**
 * La latitudine del ristorante.
 */
    private double latitudine;
    
/**
 * La longitudine del ristorante.
 */
    private double longitudine;
    
/**
 * La fascia di prezzo del ristorante.
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
 * Il tipo di cucina del ristorante.
 */
    private String tipoCucina;
    
/**
 * Il sito web del ristorante.
 */
    private String website;
    
/**
 * Il telefono del ristorante.
 */
    private String telefono;
    
/**
 * I premi o riconoscimenti del ristorante.
 */
    private String premi;
    
/**
 * La lista dei servizi offerti dal ristorante.
 */
    private List<ServizioDTO> servizi;
    
/**
 * La media delle stelle assegnate al ristorante.
 */
    private double mediaStelle;
    
/**
 * Il numero delle recensioni del ristorante.
 */
    private int numeroRecensioni;
    
/**
 * L'identificatore univoco del gestore del ristorante.
 */
    private Long idGestore;

//Costruttore
/**
 * Crea il DTO per il ristorante con i dati specificati.
 * @param idRistorante l'identificatore del ristorante
 * @param nome il nome del ristorante
 * @param indirizzo l'indirizzo del ristorante
 * @param citta la città del ristorante
 * @param provincia la provincia del ristorante
 * @param nazione la nazione del ristorante
 * @param latitudine la latitudine del ristorante
 * @param longitudine la longitudine del ristorante
 * @param fasciaPrezzo la fascia di prezzo del ristorante
 * @param prenotazioneOnline indica se il ristorante offre la prenotazione online
 * @param consegnaADomicilio indica se il ristorante offre la consegna a domicilio
 * @param tipoCucina il tipo di cucina del ristorante
 * @param website il sito web del ristorante
 * @param telefono il numero di telefono del ristorante
 * @param premi i premi o riconoscimenti del ristorante
 * @param servizi la lista dei servizi offerti dal ristorante
 * @param mediaStelle la media delle stelle assegnate al ristorante
 * @param numeroRecensioni il numero di recensioni del ristorante
 * @param idGestore l'identificativo del gestore del ristorante
 */
    public RistoranteDTO(long idRistorante, String nome, String indirizzo, String citta, 
        String provincia, String nazione, double latitudine, double longitudine, int fasciaPrezzo, 
        boolean prenotazioneOnline, boolean consegnaADomicilio, String tipoCucina, String website, 
        String telefono, String premi, List<ServizioDTO> servizi, double mediaStelle, int numeroRecensioni, 
        Long idGestore) {
        this.idRistorante = idRistorante;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.citta = citta;
        this.provincia = provincia;
        this.nazione = nazione;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.fasciaPrezzo = fasciaPrezzo;
        this.prenotazioneOnline = prenotazioneOnline;
        this.consegnaADomicilio = consegnaADomicilio;
        this.tipoCucina = tipoCucina;
        this.website = website;
        this.telefono = telefono;
        this.premi = premi;
        this.servizi = servizi;
        this.mediaStelle = mediaStelle;
        this.numeroRecensioni = numeroRecensioni;
        this.idGestore = idGestore;
    }

//Getters e Setters
/**
 * Restituisce l'identificatore del ristorante.
 * @return l'identificatore del ristorante
 */
    public long getIdRistorante() {
        return idRistorante;
    }

/**
 * Imposta l'identificatore del ristorante.
 * @param idRistorante l'identificatore del ristorante
 */
    public void setIdRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
    }

/**
 * Restituisce il nome del ristorante.
 * @return il nome del ristorante
 */
    public String getNome() {
        return nome;
    }

/**
 * Imposta il nome del ristorante.
 * @param nome il nome del ristorante
 */
    public void setNome(String nome) {
        this.nome = nome;
    }

/**
 * Restituisce l'indirizzo del ristorante.
 * @return l'indirizzo del ristorante
 */
    public String getIndirizzo() {
        return indirizzo;
    }

/**
 * Imposta l'indirizzo del ristorante.
 * @param indirizzo l'indirizzo del ristorante
 */
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

/**
 * Restituisce la città in cui si trova il ristorante.
 * @return la città del ristorante
 */
    public String getCitta() {
        return citta;
    }

/**
 * Imposta la città in cui si trova il ristorante.
 * @param citta la città del ristorante
 */
    public void setCitta(String citta) {
        this.citta = citta;
    }

/**
 * Restituisce la provincia in cui si trova il ristorante.
 * @return la provincia del ristorante
 */
    public String getProvincia() {
        return provincia;
    }

/**
 * Imposta la provincia in cui si trova il ristorante.
 * @param provincia la provincia del ristorante
 */
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

/**
 * Restituisce la nazione in cui si trova il ristorante.
 * @return la nazione del ristorante
 */
    public String getNazione() {
        return nazione;
    }

/**
 * Imposta la nazione in cui si trova il ristorante.
 * @param nazione la nazione del ristorante
 */
    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

/**
 * Restituisce la latitudine della posizione del ristorante.
 * @return la latitudine del ristorante
 */
    public double getLatitudine() {
        return latitudine;
    }

/**
 * Imposta la latitudine della posizione del ristorante.
 * @param latitudine la latitudine del ristorante
 */
    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

/**
 * Restituisce la longitudine della posizione del ristorante.
 * @return la longitudine del ristorante
 */
    public double getLongitudine() {
        return longitudine;
    }

/**
 * Imposta la longitudine della posizione del ristorante.
 * @param longitudine la longitudine del ristorante
 */
    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

/**
 * Restituisce la fascia di prezzo del ristorante.
 * @return la fascia di prezzo del ristorante
 */
    public int getFasciaPrezzo() {
        return fasciaPrezzo;
    }

/**
 * Imposta la fascia di prezzo del ristorante.
 * @param fasciaPrezzo la fascia di prezzo del ristorante
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
 * Restituisce il tipo di cucina del ristorante.
 * @return il tipo di cucina del ristorante
 */
    public String getTipoCucina() {
        return tipoCucina;
    }

/**
 * Imposta il tipo di cucina del ristorante.
 * @param tipoCucina il tipo di cucina del ristorante
 */
    public void setTipoCucina(String tipoCucina) {
        this.tipoCucina = tipoCucina;
    }

/**
 * Restituisce il sito web del ristorante.
 * @return il sito web del ristorante
 */
    public String getWebsite() {
        return website;
    }

/**
 * Imposta il sito web del ristorante.
 * @param website il sito web del ristorante
 */
    public void setWebsite(String website) {
        this.website = website;
    }

/**
 * Restituisce il numero di telefono del ristorante.
 * @return il numero di telefono del ristorante
 */
    public String getTelefono() {
        return telefono;
    }

/**
 * Imposta il numero di telefono del ristorante.
 * @param telefono il numero di telefono del ristorante
 */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

/**
 * Restituisce i premi o riconoscimenti del ristorante.
 * @return i premi o riconoscimenti del ristorante
 */
    public String getPremi() {
        return premi;
    }

/**
 * Imposta i premi o riconoscimenti del ristorante.
 * @param premi i premi o riconoscimenti del ristorante
 */
    public void setPremi(String premi) {
        this.premi = premi;
    }

/**
 * Restituisce la lista dei servizi offerti dal ristorante.
 * @return la lista dei servizi offerti dal ristorante
 */
    public List<ServizioDTO> getServizi() {
        return servizi;
    }

/**
 * Imposta la lista dei servizi offerti dal ristorante.
 * @param servizi la lista dei servizi offerti dal ristorante
 */
    public void setServizi(List<ServizioDTO> servizi) {
        this.servizi = servizi;
    }

/**
 * Restituisce la media delle stelle del ristorante.
 * @return la media delle stelle del ristorante
 */
    public double getMediaStelle() {
        return mediaStelle;
    }

/**
 * Imposta la media delle stelle assegnate al ristorante.
 * @param mediaStelle la media delle stelle assignate al ristorante
 */
    public void setMediaStelle(double mediaStelle) {
        this.mediaStelle = mediaStelle;
    }

/**
 * Restituisce il numero di recensioni del ristorante.
 * @return il numero di recensioni del ristorante
 */
    public int getNumeroRecensioni() {
        return numeroRecensioni;
    }

/**
 * Imposta il numero di recensioni del ristorante.
 * @param numeroRecensioni il numero di recensioni del ristorante
 */
    public void setNumeroRecensioni(int numeroRecensioni) {
        this.numeroRecensioni = numeroRecensioni;
    }

/**
 * Restituisce l'identificatore del gestore del ristorante.
 * @return l'identificatore del gestore del ristorante
 */
    public Long getIdGestore() {
        return idGestore;
    }

/**
 * Imposta l'identificatore del gestore del ristorante.
 * @param idGestore l'identificatore del gestore del ristorante
 */
    public void setIdGestore(Long idGestore) {
        this.idGestore = idGestore;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto RistoranteDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "RistoranteDTO [idRistorante=" + idRistorante + ", nome=" + nome + 
            ", indirizzo=" + indirizzo + ", citta=" + citta + 
            ", provincia=" + provincia + ", nazione=" + nazione + ", latitudine=" + latitudine + 
            ", longitudine=" + longitudine + ", fasciaPrezzo=" + fasciaPrezzo + 
            ", prenotazioneOnline=" + prenotazioneOnline + ", consegnaADomicilio=" + consegnaADomicilio +
            ", tipoCucina=" + tipoCucina + ", website=" + website + ", telefono=" + telefono +
            ", premi=" + premi + ", servizi=" + servizi + ", mediaStelle=" + mediaStelle +
            ", numeroRecensioni=" + numeroRecensioni + ", idGestore=" + idGestore + "]";
    }
}
