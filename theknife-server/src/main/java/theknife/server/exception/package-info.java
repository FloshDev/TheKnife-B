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
 *   <li>{@link theknife.server.exception.UnauthorizedException} - permessi
 *       insufficienti.</li>
 * </ul>
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
package theknife.server.exception;
