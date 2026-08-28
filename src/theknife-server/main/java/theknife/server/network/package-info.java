/**
 * Livello di rete del server: ciclo di vita del socket di ascolto e
 * conversazione con i singoli client.
 * <p>
 * {@link theknife.server.network.Server} verifica il database, costruisce
 * l'intera catena DAO - service - Command e accetta le connessioni;
 * {@link theknife.server.network.ClientHandler} serve un client per volta su
 * un thread dedicato del pool.
 * <p>
 * Due dettagli del protocollo che non si deducono leggendo il codice che li
 * usa. Il primo: l'{@code ObjectOutputStream} va sempre svuotato subito dopo
 * la costruzione, perche' il suo header resta altrimenti nel buffer e i due
 * lati si bloccano a vicenda. Il secondo: la deserializzazione e' filtrata per
 * classe, perche' i dati arrivano da un socket e non sono fidati.
 *
 * @author Ciani Flavio Angelo, 761581, VA
 */
package theknife.server.network;
