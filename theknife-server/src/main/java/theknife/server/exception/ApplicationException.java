package theknife.server.exception;

/**
 * Eccezione applicativa di dominio: operazione non permessa dalle regole di
 * business (es. ristorante già recensito dallo stesso utente). Il chiamante la
 * traduce in ResponseStatus.ERRORE.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
public class ApplicationException extends Exception {

    /** Versione della classe ai fini della serializzazione. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea l'eccezione con il messaggio che descrive la regola violata.
     *
     * @param messaggio la descrizione della violazione di dominio
     */
    public ApplicationException(String messaggio) {
        super(messaggio);
    }
}
