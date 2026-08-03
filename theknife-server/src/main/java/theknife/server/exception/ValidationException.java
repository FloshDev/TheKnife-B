package theknife.server.exception;

/**
 * Eccezione di validazione: dati in ingresso non validi (campo obbligatorio
 * mancante, formato errato). Il chiamante la traduce in ResponseStatus.ERRORE_VALIDAZIONE.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ValidationException(String messaggio) {
        super(messaggio);
    }
}
