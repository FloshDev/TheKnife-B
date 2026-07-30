public class LoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
//Costruttore
    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
//Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
//Metodo toString
@Override
    public String toString() {
        return "LoginDTO [username=" + username + ", password=" + password + "]";
    }
}
