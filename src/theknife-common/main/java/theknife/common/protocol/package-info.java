/**
 * Contiene le classi che definiscono il protocollo di comunicazione tra client e server, 
 * in particolare le classi {@code Request} e {@code Response} che rappresentano rispettivamente 
 * le richieste inviate dal client al server e le risposte inviate dal server al client.
 * 
 * <p>Il protocollo di comunicazione è basato su un modello di richiesta-risposta, in cui il client 
 * invia una richiesta al server e attende una risposta.
 * Le richieste e le risposte sono rappresentate da oggetti serializzabili che contengono i dati necessari 
 * per l'elaborazione della richiesta e la generazione della relativa risposta.</p>
 * 
 * <p>La definizione del protocollo in un package separato e condiviso permette di mantenere
 * coerente la struttura dei messaggi scambiati tra le diverse componenti del sistema.</p>
 * 
 * @author Gasparini Lorenzo, 759929, VA
 * @see theknife.common.protocol.Request
 * @see theknife.common.protocol.Response
 */
package theknife.common.protocol;