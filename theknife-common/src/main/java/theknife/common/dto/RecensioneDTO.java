package theknife.common.dto;
import java.time.LocalDateTime;

/**
 * Rappresenta i dati di una recensione trasferiti tra client e server.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class RecensioneDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long idRecensione;
    private long idRistorante;
    private String titolo;
    private String testo;
    private int stelle;
    private long idUtente;
    private LocalDateTime dataPubblicazione;
    private String risposta;
    private LocalDateTime dataRisposta;

//Costruttore
    public RecensioneDTO(long idRecensione, long idRistorante, String titolo, String testo, 
            int stelle, long idUtente, LocalDateTime dataPubblicazione, String risposta, 
            LocalDateTime dataRisposta) {
        this.idRecensione = idRecensione;
        this.idRistorante = idRistorante;
        this.titolo = titolo;
        this.testo = testo;
        this.stelle = stelle;
        this.idUtente = idUtente;
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

    public LocalDateTime getDataPubblicazione() {
        return dataPubblicazione;
    }

    public void setDataPubblicazione(LocalDateTime dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
    }

    public String getRisposta() {
        return risposta;
    }

    public void setRisposta(String risposta) {
        this.risposta = risposta;
    }

    public LocalDateTime getDataRisposta() {
        return dataRisposta;
    }

    public void setDataRisposta(LocalDateTime dataRisposta) {
        this.dataRisposta = dataRisposta;
    }

//Metodo toString
    @Override
    public String toString() {
        return "RecensioneDTO [idRecensione=" + idRecensione + ", idRistorante=" + idRistorante + 
            ", titolo=" + titolo + ", testo=" + testo + ", stelle=" + stelle + 
            ", idUtente=" + idUtente + ", dataPubblicazione=" + dataPubblicazione + 
            ", risposta=" + risposta + ", dataRisposta=" + dataRisposta + "]";
    }
}
