package theknife.server.exception;

/**
 * Eccezione di validazione: dati in ingresso non validi (campo obbligatorio
 * mancante, formato errato). Il chiamante la traduce in ResponseStatus.ERRORE_VALIDAZIONE.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class ValidationException extends Exception {

    /** Versione della classe ai fini della serializzazione. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea l'eccezione con il messaggio che descrive il dato non valido.
     *
     * @param messaggio la descrizione dell'errore di validazione
     */
    public ValidationException(String messaggio) {
        super(messaggio);
    }
}
