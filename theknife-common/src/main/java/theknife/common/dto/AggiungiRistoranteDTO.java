package theknife.common.dto;
import java.util.List;

public class AggiungiRistoranteDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private String indirizzo;
    private String citta;
    private String nazione;
    private String fasciaPrezzo;
    private boolean prenotazioneOnline;
    private boolean consegnaADomicilio;
    private String tipoCucina;
    private String website;
    private String telefono;
    private String premi;
    private List<String> servizi;

    //Costruttore
    public AggiungiRistoranteDTO(String nome, String indirizzo, String citta, String nazione, 
            String fasciaPrezzo, boolean prenotazioneOnline, boolean consegnaADomicilio, 
            String tipoCucina, String website, String telefono, String premi, List<String> servizi) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.citta = citta;
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

    public String getNazione() {
        return nazione;
    }

    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

    public String getFasciaPrezzo() {
        return fasciaPrezzo;
    }

    public void setFasciaPrezzo(String fasciaPrezzo) {
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

    public List<String> getServizi() {
        return servizi;
    }

    public void setServizi(List<String> servizi) {
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
