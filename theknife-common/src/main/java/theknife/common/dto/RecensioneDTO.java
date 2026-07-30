package theknife.common.dto;
import java.time.LocalDate;

public class RecensioneDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long idRecensione;
    private long idRistorante;
    private String titolo;
    private String testo;
    private int stelle;
    private String usernameAutore;
    private LocalDate dataPubblicazione;
    private String risposta;

//Costruttore
    public RecensioneDTO(long idRecensione,  long idRistorante, String titolo, String testo, 
            int stelle, String usernameAutore, LocalDate dataPubblicazione, String risposta) {
        this.idRecensione = idRecensione;
        this.idRistorante = idRistorante;
        this.titolo = titolo;
        this.testo = testo;
        this.stelle = stelle;
        this.usernameAutore = usernameAutore;
        this.dataPubblicazione = dataPubblicazione;
        this.risposta = risposta;
    }

//Getters e Setters
    public long getIdRecensione() {
        return idRecensione;
    }

    public void setIdRecensione(long idRecensione) {
        this.idRecensione = idRecensione;
    }

    public long getIdRistorante() {
        return idRistorante;
    }

    public void setIdRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public int getStelle() {
        return stelle;
    }

    public void setStelle(int stelle) {
        this.stelle = stelle;
    }

    public String getUsernameAutore() {
        return usernameAutore;
    }

    public void setUsernameAutore(String usernameAutore) {
        this.usernameAutore = usernameAutore;
    }

    public LocalDate getDataPubblicazione() {
        return dataPubblicazione;
    }

    public void setDataPubblicazione(LocalDate dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
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
        return "RecensioneDTO [idRecensione=" + idRecensione + ", idRistorante=" + idRistorante + 
            ", titolo=" + titolo + ", testo=" + testo + ", stelle=" + stelle + 
            ", usernameAutore=" + usernameAutore + ", dataPubblicazione=" + dataPubblicazione + 
            ", risposta=" + risposta + "]";
    }
}
