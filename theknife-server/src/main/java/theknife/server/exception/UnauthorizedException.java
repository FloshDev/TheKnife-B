package theknife.server.exception;

/**
 * Eccezione di autorizzazione: l'utente non è loggato o non ha i permessi per
 * l'operazione richiesta. Il chiamante la traduce in ResponseStatus.NON_AUTORIZZATO.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class UnauthorizedException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String messaggio) {
        super(messaggio);
    }
}
