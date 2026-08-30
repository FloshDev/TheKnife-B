/**
 * Layer di accesso ai dati (DAO) del server TheKnife.
 * <p>
 * Ogni classe incapsula le operazioni sulle tabelle del database
 * <code>dbTK</code>: apre una connessione tramite
 * {@link theknife.server.dao.DatabaseManager}, la usa con
 * <code>try-with-resources</code> e riavvolge le
 * {@link java.sql.SQLException} in
 * {@link theknife.server.exception.DataAccessException}. Nessun DAO contiene
 * logica di dominio: legge e scrive righe, impacchettandole nei DTO del
 * modulo <code>theknife-common</code>.
 *
 * @author Scolaro Gabriele, 760123, VA
 * @see theknife.server.dao.UtenteDAO
 * @see theknife.server.dao.ServizioDAO
 * @see theknife.server.dao.RistoranteDAO
 * @see theknife.server.dao.RecensioneDAO
 * @see theknife.server.dao.PreferitoDAO
 * @see theknife.server.dao.DatabaseManager
 * @see theknife.server.exception.DataAccessException
 */
package theknife.server.dao;
