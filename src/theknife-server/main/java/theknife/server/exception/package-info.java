/**
 * Eccezioni del server, distinte per <i>di chi e' la colpa</i>: da questa
 * distinzione dipende lo {@code ResponseStatus} che il client ricevera'.
 * <ul>
 *   <li>{@link theknife.server.exception.ValidationException} - i dati in
 *       ingresso non sono validi: colpa della richiesta, l'utente puo'
 *       correggerla.</li>
 *   <li>{@link theknife.server.exception.ApplicationException} - i dati sono
 *       validi ma l'operazione viola una regola di dominio (ristorante gia'
 *       recensito, username occupato): colpa dell'operazione richiesta.</li>
 *   <li>{@link theknife.server.exception.DataAccessException} - il database
 *       non risponde o la query fallisce: colpa del sistema, l'utente non puo'
 *       farci nulla.</li>
 * </ul>
 * <p>
 * I permessi non hanno un'eccezione propria (decisione 31): per la decisione
 * 20 il ruolo lo verifica
 * {@link theknife.server.handler.CommandDispatcher} e la proprieta' delle
 * risorse il singolo Command, e chi rifiuta risponde direttamente
 * {@code NON_AUTORIZZATO} senza passare da un lancio.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
package theknife.server.exception;
