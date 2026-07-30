package theknife.common.dto;

public class StatisticheRistoranteDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long idRistorante;
    private String nomeRistorante;
    private double mediaStelle;
    private int numeroRecensioni;

//Costruttore
    public StatisticheRistoranteDTO(long idRistorante, String nomeRistorante, double mediaStelle, 
            int numeroRecensioni) {
        this.idRistorante = idRistorante;
        this.nomeRistorante = nomeRistorante;
        this.mediaStelle = mediaStelle;
        this.numeroRecensioni = numeroRecensioni;
    }
//Getters e Setters
    public long getIdRistorante() {
        return idRistorante;
    }   

    public void setIdRistorante(long idRistorante) {
        this.idRistorante = idRistorante;
    }

    public String getNomeRistorante() {
        return nomeRistorante;
    }

    public void setNomeRistorante(String nomeRistorante) {
        this.nomeRistorante = nomeRistorante;
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

//Metodo toString
    @Override
    public String toString() {
        return "StatisticheRistoranteDTO [idRistorante=" + idRistorante + 
        ", nomeRistorante=" + nomeRistorante + ", mediaStelle=" + mediaStelle + 
        ", numeroRecensioni=" + numeroRecensioni + "]";
    }
}
