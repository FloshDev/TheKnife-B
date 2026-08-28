package theknife.common.dto;

/**
 * Rappresenta i dati necessari per la richiesta di modifica di una recensione da parte di un utente.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public class ModificaRecensioneDTO implements java.io.Serializable {
/**
 * Costante di serializzazione per la compatibilità tra versioni della classe durante la serializzazione e
 * deserializzazione.
 */
    private static final long serialVersionUID = 1L;
    
/**
 * L'identificatore univoco della recensione da modificare.
 */
    private long idRecensione;
    
/**
 * Il nuovo titolo della recensione.
 */
    private String nuovoTitolo;
    
/**
 * Il nuovo testo della recensione.
 */
    private String nuovoTesto;
    
/**
 * Il nuovo numero di stelle della recensione.
 */
    private int nuoveStelle;

//Costruttore
/**
 * Crea il DTO per la modifica di una recensione con i dati specificati.
 * @param idRecensione l'identificatore univoco della recensione da modificare
 * @param nuovoTitolo il nuovo titolo della recensione
 * @param nuovoTesto il nuovo testo della recensione
 * @param nuoveStelle il nuovo numero di stelle della recensione
 */
    public ModificaRecensioneDTO(long idRecensione, String nuovoTitolo, String nuovoTesto, int nuoveStelle) {
        this.idRecensione = idRecensione;
        this.nuovoTitolo = nuovoTitolo;
        this.nuovoTesto = nuovoTesto;
        this.nuoveStelle = nuoveStelle;
    }

//Getters e Setters
/**
 * Restituisce l'identificatore della recensione da modificare.
 * @return l'identificatore della recensione
 */
    public long getIdRecensione() {
        return idRecensione;
    }

/**
 * Imposta l'identificatore della recensione da modificare.
 * @param idRecensione l'identificatore della recensione
 */
    public void setIdRecensione(long idRecensione) {
        this.idRecensione = idRecensione;
    }

/**
 * Restituisce il nuovo titolo della recensione.
 * @return il nuovo titolo della recensione
 */
    public String getNuovoTitolo() {
        return nuovoTitolo;
    }

/**
 * Imposta il nuovo titolo della recensione.
 * @param nuovoTitolo il nuovo titolo della recensione
 */
    public void setNuovoTitolo(String nuovoTitolo) {
        this.nuovoTitolo = nuovoTitolo;
    }

/**
 * Restituisce il nuovo testo della recensione.
 * @return il nuovo testo della recensione
 */
    public String getNuovoTesto() {
        return nuovoTesto;
    }

/**
 * Imposta il nuovo testo della recensione.
 * @param nuovoTesto il nuovo testo della recensione
 */
    public void setNuovoTesto(String nuovoTesto) {
        this.nuovoTesto = nuovoTesto;
    }

/**
 * Restituisce il nuovo numero di stelle della recensione.
 * @return il nuovo numero di stelle della recensione
 */
    public int getNuoveStelle() {
        return nuoveStelle;
    }

/**
 * Imposta il nuovo numero di stelle della recensione.
 * @param nuoveStelle il nuovo numero di stelle della recensione
 */
    public void setNuoveStelle(int nuoveStelle) {
        this.nuoveStelle = nuoveStelle;
    }

//Metodo toString
/**
 * Restituisce una rappresentazione testuale dell'oggetto ModificaRecensioneDTO.
 * @return la rappresentazione testuale dell'oggetto
 */
    @Override
    public String toString() {
        return "ModificaRecensioneDTO [idRecensione=" + idRecensione + 
            ", nuovoTesto=" + nuovoTesto + ", nuoveStelle=" + nuoveStelle + "]";
    }
}
