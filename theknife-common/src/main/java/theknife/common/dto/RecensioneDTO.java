package theknife.common.dto;
import java.time.LocalDate;

public class RecensioneDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long idRecensione;
    private long idRistorante;
    private String titolo;
    private String testo;
    private int stelle;
    private long idUtente;
    private String usernameAutore;
    private LocalDate dataPubblicazione;
    private String risposta;
    private LocalDate dataRisposta;

//Costruttore
    public RecensioneDTO(long idRecensione, long idRistorante, String titolo, String testo, 
            int stelle, long idUtente, String usernameAutore, LocalDate dataPubblicazione, String risposta, 
            LocalDate dataRisposta) {
        this.idRecensione = idRecensione;
        this.idRistorante = idRistorante;
        this.titolo = titolo;
        this.testo = testo;
        this.stelle = stelle;
        this.idUtente = idUtente;
        this.usernameAutore = usernameAutore;
        this.dataPubblicazione = dataPubblicazione;
        this.risposta = risposta;
        this.dataRisposta = dataRisposta;
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

    public long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(long idUtente) {
        this.idUtente = idUtente;
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

    public LocalDate getDataRisposta() {
        return dataRisposta;
    }

    public void setDataRisposta(LocalDate dataRisposta) {
        this.dataRisposta = dataRisposta;
    }

//Metodo toString
    @Override
    public String toString() {
        return "RecensioneDTO [idRecensione=" + idRecensione + ", idRistorante=" + idRistorante + 
            ", titolo=" + titolo + ", testo=" + testo + ", stelle=" + stelle + 
            ", idUtente=" + idUtente + ", usernameAutore=" + usernameAutore + 
            ", dataPubblicazione=" + dataPubblicazione + 
            ", risposta=" + risposta + ", dataRisposta=" + dataRisposta + "]";
    }
}
