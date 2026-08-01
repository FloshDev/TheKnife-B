package theknife.common.dto;
import java.time.LocalDate;
import theknife.common.enums.Ruolo;

public class UtenteDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private long idUtente;
    private String username;
    private String nome;
    private String cognome;
    private String email;
    private Ruolo ruolo;
    private LocalDate dataNascita;
    private String domicilio;

//Costruttore
    public UtenteDTO(long idUtente, String username, String nome, String cognome, String email, 
            Ruolo ruolo, LocalDate dataNascita, String domicilio) {
        this.idUtente = idUtente;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.ruolo = ruolo;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
    }

//Getters e Setters
    public long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(long idUtente) {
        this.idUtente = idUtente;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

//Metodo toString
    @Override
    public String toString() {
        return "UtenteDTO [idUtente=" + idUtente + ", username=" + username + ", nome=" + nome + 
            ", cognome=" + cognome + ", email=" + email + ", ruolo=" + ruolo + 
            ", dataNascita=" + dataNascita + ", domicilio=" + domicilio + "]";
    }
}
