package theknife.common.dto;
import java.io.Serializable;
import java.time.LocalDate;
import theknife.common.enums.Ruolo;

public class RegistrazioneDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
     private String cognome;
    private String username;
    private String password;
    private String email;
    private Ruolo ruolo;
    private LocalDate dataNascita;
    private String domicilio;

//Costruttore
    public RegistrazioneDTO(String nome, String cognome, String username, 
        String password, String email, Ruolo ruolo, LocalDate dataNascita, String domicilio) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.email = email;
        this.ruolo = ruolo;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
    }
//Setters
    public void setNome(String x) {
        this.nome = x;
    }

    public void setCognome(String x) {
        this.cognome = x;
    }

    public void setUsername(String x) {
        this.username = x;
    }

    public void setPassword(String x) {
        this.password = x;
    }

    public void setEmail(String x) {
        this.email = x;
    }

    public void setRuolo(Ruolo x) {
        this.ruolo = x;
    }

    public void setDataNascita(LocalDate x) {
        this.dataNascita = x;
    }

    public void setDomicilio(String x) {
        this.domicilio = x;
    }
//Getters
    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public String getDomicilio() {
        return domicilio;
    }
//Metodo toString
    @Override
    public String toString() {
        return "RegistrazioneDTO [nome=" + nome + ", cognome=" + cognome + ", username=" + username + 
            ", email=" + email + ", ruolo=" + ruolo + 
            ", dataNascita=" + dataNascita + ", domicilio=" + domicilio + "]";
    }
}