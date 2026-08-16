/**
 * Punto d'ingresso del client. {@link theknife.client.app.TheKnifeClient} è il
 * solo entry point del jar shaded; {@link theknife.client.app.ClientApplication}
 * porta la logica JavaFX (connessione al server, splash iniziale), separata
 * perché un {@code Main-Class} che estende direttamente {@code Application}
 * impedirebbe l'avvio con {@code java -jar}.
 *
 * @author Barlera Marco, 760000, VA
 */
package theknife.client.app;
