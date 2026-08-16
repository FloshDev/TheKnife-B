/**
 * Instradamento delle richieste e controllo degli accessi.
 * <p>
 * {@link theknife.server.handler.CommandDispatcher} risolve il token di
 * sessione in un utente, applica il gate di autorizzazione per ruolo
 * (decisione 20) e, solo se il requisito e' soddisfatto, chiede il Command a
 * {@link theknife.server.handler.CommandFactory} e lo esegue. La factory
 * costruisce i venti Command una volta sola all'avvio e li conserva in una
 * mappa immutabile.
 * <p>
 * Il gate rifiuta <b>prima</b> di istanziare il Command: una richiesta senza
 * i permessi necessari non arriva mai alla logica applicativa.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
package theknife.server.handler;
