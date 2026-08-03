package theknife.server.exception;

/**
 * Eccezione applicativa di dominio: operazione non permessa dalle regole di
 * business (es. ristorante già recensito dallo stesso utente). Il chiamante la
 * traduce in ResponseStatus.ERRORE.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class ApplicationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ApplicationException(String messaggio) {
        super(messaggio);
    }
}
