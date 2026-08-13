package theknife.common.dto;
import java.util.List;

/**
 * Rappresenta i dati necessari per la richiesta di aggiunta di un ristorante da parte di un utente.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class AggiungiRistoranteDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private String indirizzo;
    private String citta;
    private String provincia;
    private String nazione;
    private int fasciaPrezzo;
    private boolean prenotazioneOnline;
    private boolean consegnaADomicilio;
    private String tipoCucina;
    private String website;
    private String telefono;
    private String premi;
    private List<ServizioDTO> servizi;

    //Costruttore
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
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getNazione() {
        return nazione;
    }

    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

    public int getFasciaPrezzo() {
        return fasciaPrezzo;
    }

    public void setFasciaPrezzo(int fasciaPrezzo) {
        this.fasciaPrezzo = fasciaPrezzo;
    }

    public boolean isPrenotazioneOnline() {
        return prenotazioneOnline;
    }

    public void setPrenotazioneOnline(boolean prenotazioneOnline) {
        this.prenotazioneOnline = prenotazioneOnline;
    }

    public boolean isConsegnaADomicilio() {
        return consegnaADomicilio;
    }

    public void setConsegnaADomicilio(boolean consegnaADomicilio) {
        this.consegnaADomicilio = consegnaADomicilio;
    }

    public String getTipoCucina() {
        return tipoCucina;
    }

    public void setTipoCucina(String tipoCucina) {
        this.tipoCucina = tipoCucina;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPremi() {
        return premi;
    }

    public void setPremi(String premi) {
        this.premi = premi;
    }

    public List<ServizioDTO> getServizi() {
        return servizi;
    }

    public void setServizi(List<ServizioDTO> servizi) {
        this.servizi = servizi;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
//Metodo toString
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
