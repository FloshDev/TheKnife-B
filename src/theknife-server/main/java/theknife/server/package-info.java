/**
 * Radice del modulo server: avvio del processo e configurazione del database.
 * <p>
 * {@link theknife.server.TheKnifeServer} e' il punto di ingresso di
 * <code>serverTK.jar</code>: legge l'eventuale porta dalla riga di comando,
 * chiede le credenziali del database e passa il controllo a
 * {@link theknife.server.network.Server}, che apre il socket e resta in
 * ascolto. {@link theknife.server.ConfigurazioneDB} conserva i parametri di
 * connessione richiesti dalle specifiche all'avvio (host, utente, password) e
 * i due valori fissi del progetto, nome e porta del database.
 * <p>
 * I sottopackage seguono il percorso di una richiesta:
 * {@link theknife.server.network} la legge dal socket,
 * {@link theknife.server.handler} la instrada e ne verifica i permessi,
 * {@link theknife.server.command} la esegue, {@link theknife.server.service}
 * applica le regole di dominio, {@link theknife.server.dao} parla con il
 * database e {@link theknife.server.external} con i servizi geografici.
 * {@link theknife.server.exception} definisce i tre tipi di errore che
 * attraversano quei livelli.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 * @see theknife.server.TheKnifeServer
 * @see theknife.server.ConfigurazioneDB
 */
package theknife.server;
