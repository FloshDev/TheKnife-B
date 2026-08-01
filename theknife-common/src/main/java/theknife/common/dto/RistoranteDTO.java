package theknife.common.dto;
import java.util.List;

public class RistoranteDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long idRistorante;
    private String nome;
    private String indirizzo;
    private String citta;
    private String provincia;
    private String nazione;
    private double latitudine;
    private double longitudine;
    private int fasciaPrezzo;
    private boolean prenotazioneOnline;
    private boolean consegnaADomicilio;
    private String tipoCucina;
    private String website;
    private String telefono;
    private String premi;
    private List<ServizioDTO> servizi;
    private double mediaStelle;
    private int numeroRecensioni;
    private long idGestore;

//Costruttore
    public RistoranteDTO(long idRistorante, String nome, String indirizzo, String citta, 
        String provincia, String nazione, double latitudine, double longitudine, int fasciaPrezzo, 
        boolean prenotazioneOnline, boolean consegnaADomicilio, String tipoCucina, String website, 
        String telefono, String premi, List<ServizioDTO> servizi, double mediaStelle, int numeroRecensioni, 
        long idGestore) {
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

    public long getIdRistorante() {
        return idRistorante;
    }

    public void setIdRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
    }

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

    public double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    public double getMediaStelle() {
        return mediaStelle;
    }

    public void setMediaStelle(double mediaStelle) {
        this.mediaStelle = mediaStelle;
    }

    public int getNumeroRecensioni() {
        return numeroRecensioni;
    }

    public void setNumeroRecensioni(int numeroRecensioni) {
        this.numeroRecensioni = numeroRecensioni;
    }

    public long getIdGestore() {
        return idGestore;
    }

    public void setIdGestore(long idGestore) {
        this.idGestore = idGestore;
    }

//Metodo toString
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
