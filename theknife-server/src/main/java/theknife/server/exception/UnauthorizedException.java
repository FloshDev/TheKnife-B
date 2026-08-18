package theknife.server.exception;

/**
 * Eccezione di autorizzazione: l'utente non è loggato o non ha i permessi per
 * l'operazione richiesta. Il chiamante la traduce in ResponseStatus.NON_AUTORIZZATO.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class UnauthorizedException extends Exception {

    /** Versione della classe ai fini della serializzazione. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea l'eccezione con il messaggio che descrive il permesso mancante.
     *
     * @param messaggio la descrizione dell'operazione non consentita
     */
    public UnauthorizedException(String messaggio) {
        super(messaggio);
    }
}
