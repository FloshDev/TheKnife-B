package theknife.common.dto;

/**
 * Rappresenta le statistiche di un ristorante trasferite tra client e server.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class StatisticheRistoranteDTO implements java.io.Serializable {
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
    private String nomeRistorante;
    
/**
 * La media delle stelle assegnate al ristorante.
 */
    private double mediaStelle;
    
/**
 * Il numero di recensioni del ristorante.
 */
    private int numeroRecensioni;

//Costruttore
/**
 * Crea il DTO per le statistiche del ristorante con i dati specificati.
 * @param idRistorante l'identificatore del ristorante
 * @param nomeRistorante il nome del ristorante
 * @param mediaStelle la media delle stelle assegnate al ristorante
 * @param numeroRecensioni il numero di recensioni del ristorante
 */
    public StatisticheRistoranteDTO(long idRistorante, String nomeRistorante, double mediaStelle, 
            int numeroRecensioni) {
        this.idRistorante = idRistorante;
        this.nomeRistorante = nomeRistorante;
        this.mediaStelle = mediaStelle;
        this.numeroRecensioni = numeroRecensioni;
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
    public String getNomeRistorante() {
        return nomeRistorante;
    }

/**
 * Imposta il nome del ristorante.
 * @param nomeRistorante il nome del ristorante
 */
    public void setNomeRistorante(String nomeRistorante) {
        this.nomeRistorante = nomeRistorante;
    }

/**
 * Restituisce la media delle stelle assegnate al ristorante.
 * @return la media delle stelle assegnate al ristorante
 */
    public double getMediaStelle() {
        return mediaStelle;
    }

/**
 * Imposta la media delle stelle assegnate al ristorante.
 * @param mediaStelle la media delle stelle assegnate al ristorante
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

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto StatisticheRistoranteDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "StatisticheRistoranteDTO [idRistorante=" + idRistorante + 
        ", nomeRistorante=" + nomeRistorante + ", mediaStelle=" + mediaStelle + 
        ", numeroRecensioni=" + numeroRecensioni + "]";
    }
}
