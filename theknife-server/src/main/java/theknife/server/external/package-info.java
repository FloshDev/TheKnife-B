/**
 * Client dei servizi esterni interrogati dal server: geocoding diretto e
 * inverso su Nominatim/OpenStreetMap ({@link theknife.server.external.GeocodingClient})
 * e localizzazione approssimata da indirizzo IP
 * ({@link theknife.server.external.LocalizzazioneIpClient}).
 * <p>
 * Regola comune a tutto il package: nessun fallimento di un servizio esterno
 * puo' propagarsi come eccezione al chiamante. Ogni metodo restituisce
 * <code>null</code> quando il servizio non risponde, perche' queste
 * informazioni sono sempre migliorative e mai necessarie al completamento
 * dell'operazione richiesta dall'utente.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
package theknife.server.external;
