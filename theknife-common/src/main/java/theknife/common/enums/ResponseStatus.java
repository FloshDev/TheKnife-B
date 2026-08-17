package theknife.common.enums;

/**
 * Rappresenta i possibili stati di una risposta inviata dal server al client.
 *
 * <ul>
 *   <li><b>SUCCESSO</b>: operazione completata con successo.</li>
 *   <li><b>ERRORE</b>: errore di dominio applicativo (es. credenziali errate,
 *       recensione già esistente, operazione non consentita).</li>
 *   <li><b>NON_AUTORIZZATO</b>: utente non autenticato o privo dei permessi necessari.</li>
 *   <li><b>NON_TROVATO</b>: la risorsa richiesta non è stata trovata.</li>
 *   <li><b>ERRORE_VALIDAZIONE</b>: dati della richiesta non validi.</li>
 *   <li><b>ERRORE_SERVER</b>: errore tecnico interno del server
 *       (eccezioni, problemi di comunicazione o accesso ai dati).</li>
 * </ul>
 * 
 * @author Gasparini Lorenzo, 759929, VA
 */

public enum ResponseStatus {
/**
 * Stato di una risposta in seguito ad un'operazione completata con successo.
 */
	SUCCESSO, 
	
/**
 * Stato di una risposta in seguito ad un'operazione fallita a causa di dati della richiesta non validi.
 */
	ERRORE_VALIDAZIONE, 
	
/**
 * Stato di una risposta in seguito ad un'operazione fallita a causa di un'autenticazione non avvenuta o
 * della mancanza dei permessi necessari.
 */
	NON_AUTORIZZATO, 

/**
 * Stato di una risposta in seguito ad un'operazione fallita a causa di una risorsa non trovata.
 */
	NON_TROVATO, 
	
/**
 * Stato di una risposta in seguito ad un'operazione fallita a causa di un errore tecnico interno del server.
 */
	ERRORE_SERVER, 
	
/**
 * Stato di una risposta in seguito ad un'operazione fallita a causa di un errore di dominio applicativo.
 */
	ERRORE
}
