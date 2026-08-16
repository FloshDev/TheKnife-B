/**
 * Implementazioni del pattern Command: una classe per ciascuno dei venti
 * {@code CommandType} del protocollo.
 * <p>
 * Ogni Command fa quattro cose e solo quelle: verifica che il payload sia del
 * tipo atteso, controlla la <b>proprieta'</b> delle risorse toccate quando
 * l'operazione lo richiede (decisioni 20 e 24), delega la logica di dominio al
 * service e traduce l'esito - o l'eccezione - in una
 * {@link theknife.common.protocol.Response}.
 * <p>
 * Cosa <b>non</b> sta qui: il controllo del ruolo, che appartiene al gate di
 * {@link theknife.server.handler.CommandDispatcher}, e la logica di dominio,
 * che appartiene ai service. La distinzione fra i due controlli di accesso e'
 * netta: il ruolo dice <i>che tipo di utente</i> puo' invocare un comando, la
 * proprieta' dice <i>su quali dati</i> quel particolare utente puo' agire.
 * <p>
 * L'identificativo dell'utente arriva sempre dalla sessione, mai dal payload:
 * un client modificato non deve poter agire a nome di un altro dichiarando il
 * suo identificativo.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 * @see theknife.server.command.Command
 * @see theknife.server.handler.CommandFactory
 */
package theknife.server.command;
