/**
 * Logica di dominio del server: validazione dei dati in ingresso, regole di
 * business e orchestrazione dei DAO.
 * <p>
 * Tre service, uno per area del protocollo:
 * {@link theknife.server.service.RistoranteService} (ricerca, inserimento e
 * preferiti - decisione 21), {@link theknife.server.service.RecensioneService}
 * e {@link theknife.server.service.UtenteService} (autenticazione).
 * {@link theknife.server.service.SessionManager} non e' un service di dominio
 * ma il registro delle sessioni attive, condiviso fra tutti i thread client.
 * <p>
 * Nessun service contiene controlli di autorizzazione: per la decisione 20 il
 * ruolo e' verificato dal Dispatcher e la proprieta' dal singolo Command. I
 * service assumono che il chiamante abbia gia' il diritto di fare cio' che
 * chiede.
 * <p>
 * Le validazioni stanno qui e non solo nella GUI: sono l'ultima difesa contro
 * un client modificato o difettoso.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
package theknife.server.service;
