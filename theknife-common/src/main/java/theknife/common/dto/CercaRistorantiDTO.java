package theknife.common.dto;
import java.util.List;

public class CercaRistorantiDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private String citta;
    private String tipoCucina;
    private Boolean prenotazioneOnline;
    private String fasciaPrezzo;
    private Boolean consegnaADomicilio;
    private List<String> servizi;
//Costruttore
    public CercaRistorantiDTO(String nome, String citta, String tipoCucina, 
            Boolean prenotazioneOnline, String fasciaPrezzo, Boolean consegnaADomicilio, 
            List<String> servizi) {
        this.nome = nome;
        this.citta = citta;
        this.tipoCucina = tipoCucina;
        this.prenotazioneOnline = prenotazioneOnline;
        this.fasciaPrezzo = fasciaPrezzo;
        this.consegnaADomicilio = consegnaADomicilio;
        this.servizi = servizi;
    }
//Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getTipoCucina() {
        return tipoCucina;
    }

    public void setTipoCucina(String tipoCucina) {
        this.tipoCucina = tipoCucina;
    }

    public Boolean isPrenotazioneOnline() {
        return prenotazioneOnline;
    }

    public void setPrenotazioneOnline(Boolean prenotazioneOnline) {
        this.prenotazioneOnline = prenotazioneOnline;
    }

    public String getFasciaPrezzo() {
        return fasciaPrezzo;
    }

    public void setFasciaPrezzo(String fasciaPrezzo) {
        this.fasciaPrezzo = fasciaPrezzo;
    }

    public Boolean isConsegnaADomicilio() {
        return consegnaADomicilio;
    }

    public void setConsegnaADomicilio(boolean consegnaADomicilio) {
        this.consegnaADomicilio = consegnaADomicilio;
    }

    public List<String> getServizi() {
        return servizi;
    }

    public void setServizi(List<String> servizi) {
        this.servizi = servizi;
    }
//Metodo toString
    @Override
    public String toString() {
        return "CercaRistoranteDTO [nome=" + nome + ", citta=" + citta + 
            ", tipoCucina=" + tipoCucina + ", prenotazioneOnline=" + prenotazioneOnline + 
            ", fasciaPrezzo=" + fasciaPrezzo + ", consegnaADomicilio=" + consegnaADomicilio + 
            ", servizi=" + servizi + "]";
    }
}
