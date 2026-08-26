package theknife.common.enums;

/**
 * Rappresenta i tipi di comandi che possono essere inviati dal client al server.
 *
 * @author Gasparini Lorenzo, 759929, VA
 */

public enum CommandType { 
//TUTTI
/**
 * Costante che rappresenta il comando corrispondente alla richiesta di ricerca di ristoranti.
 */
CERCA_RISTORANTI, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta dei dettagli di un ristorante.
 */
OTTIENI_DETTAGLI_RISTORANTE, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di lettura delle recensioni di un ristorante.
 */
LEGGI_RECENSIONI, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di ricerca di ristoranti vicini.
 */
CERCA_VICINO,

// OSPITE 
/**
 * Costante che rappresenta il comando corrispondente alla richiesta di ottenimento di una località iniziale
 * tramite i servizi di geolocalizzazione.
 */
OTTIENI_LOCALITA_INIZIALE, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di accesso al sistema.
 */
ACCEDI, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di registrazione nel sistema.
 */
REGISTRATI,

// CLIENTE
/**
 * Costante che rappresenta il comando corrispondente alla richiesta di aggiunta di un ristorante ai preferiti.
 */
AGGIUNGI_PREFERITO, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di rimozione di un ristorante dai preferiti.
 */
RIMUOVI_PREFERITO, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di visualizzazione dei ristoranti preferiti.
 */
VEDI_PREFERITI, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di aggiunta di una recensione a un ristorante.
 */
AGGIUNGI_RECENSIONE, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di modifica di una recensione a un ristorante.
 */
MODIFICA_RECENSIONE, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di eliminazione di una recensione a un ristorante.
 */
ELIMINA_RECENSIONE,

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di lettura delle recensioni personali scritte.
 */
LEGGI_RECENSIONI_PERSONALI,

// RISTORATORE 
/**
 * Costante che rappresenta il comando corrispondente alla richiesta di aggiunta di un ristorante.
 */
AGGIUNGI_RISTORANTE, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di eliminazione di un ristorante.
 */
ELIMINA_RISTORANTE,

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di visualizzazione dei ristoranti gestiti.
 */
VEDI_RISTORANTI_GESTITI, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di lettura delle recensioni ai ristoranti gestiti.
 */
LEGGI_RECENSIONI_RISTORANTI_GESTITI, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di ottenimento delle statistiche di un ristorante.
 */
OTTIENI_STATISTICHE_RISTORANTE, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di risposta a una recensione.
 */
RISPONDI_RECENSIONE, 

/**
 * Costante che rappresenta il comando corrispondente alla richiesta di associazione a un ristorante.
 */
ASSOCIA_RISTORANTE,

// CLIENTE E RISTORATORE
/**
 * Costante che rappresenta il comando corrispondente alla richiesta di disconnessione dal sistema.
 */
ESCI
}

