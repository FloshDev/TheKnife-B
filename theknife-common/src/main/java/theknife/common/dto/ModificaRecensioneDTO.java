package theknife.common.dto;

public class ModificaRecensioneDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long idRecensione;
    private String nuovoTitolo;
    private String nuovoTesto;
    private int nuoveStelle;

//Costruttore
    public ModificaRecensioneDTO(long idRecensione, String nuovoTitolo, String nuovoTesto, int nuoveStelle) {
        this.idRecensione = idRecensione;
        this.nuovoTitolo = nuovoTitolo;
        this.nuovoTesto = nuovoTesto;
        this.nuoveStelle = nuoveStelle;
    }

//Getters e Setters
    public long getIdRecensione() {
        return idRecensione;
    }

    public void setIdRecensione(long idRecensione) {
        this.idRecensione = idRecensione;
    }

    public String getNuovoTitolo() {
        return nuovoTitolo;
    }

    public void setNuovoTitolo(String nuovoTitolo) {
        this.nuovoTitolo = nuovoTitolo;
    }

    public String getNuovoTesto() {
        return nuovoTesto;
    }

    public void setNuovoTesto(String nuovoTesto) {
        this.nuovoTesto = nuovoTesto;
    }

    public int getNuoveStelle() {
        return nuoveStelle;
    }

    public void setNuoveStelle(int nuoveStelle) {
        this.nuoveStelle = nuoveStelle;
    }

//Metodo toString
    @Override
    public String toString() {
        return "ModificaRecensioneDTO [idRecensione=" + idRecensione + 
            ", nuovoTesto=" + nuovoTesto + ", nuoveStelle=" + nuoveStelle + "]";
    }
}
