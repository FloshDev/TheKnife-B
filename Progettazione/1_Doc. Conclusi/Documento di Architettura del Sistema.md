# Documento di Architettura del Sistema

## Struttura dei moduli Maven

Il progetto TheKnife è organizzato come un progetto Maven multi-modulo
composto da tre moduli: `theknife-common`, `theknife-server` e
`theknife-client`.

`theknife-common` è una libreria interna che non produce un'applicazione
eseguibile. Contiene le classi condivise tra client e server: `Request`,
`Response`, `CommandType`, `ResponseStatus` e i DTO delle entità di dominio
(`Utente`, `Ristorante`, `Recensione`, `Servizio`). Queste classi devono essere
note a entrambi i lati perché la comunicazione avviene tramite serializzazione
Java — se una delle due JVM non conosce la definizione di una classe, la
deserializzazione fallisce.

`theknife-server` dipende da `theknife-common` e produce `serverTK.jar`.
Contiene tutta la logica server-side: gestione delle connessioni client,
accesso al database tramite JDBC, esecuzione delle operazioni di dominio.

`theknife-client` dipende da `theknife-common` e produce `clientTK.jar`.
Contiene la GUI JavaFX e la logica di comunicazione col server. Non
dipende da `theknife-server` e non contiene mai codice di accesso al
database — separazione architetturalmente obbligatoria: il client non
deve poter accedere al DB né direttamente né indirettamente.

## Contenuto del modulo common

Il modulo `theknife-common` contiene esclusivamente le classi condivise
tra client e server. Tutte implementano `Serializable` perché viaggiano
sul socket tramite `ObjectOutputStream`/`ObjectInputStream`.

### Protocollo di comunicazione

- `CommandType` — enum che elenca tutte le operazioni disponibili nel
  sistema (es. `LOGIN`, `CERCA_RISTORANTI`, `AGGIUNGI_RECENSIONE`)
- `Request` — incapsula una richiesta del client: contiene un
  `CommandType`, un `Object payload` con i parametri dell'operazione, e
  un `String sessionToken` che il client include in ogni richiesta
  successiva al login (è `null` per le operazioni che non richiedono
  autenticazione)
- `ResponseStatus` — enum che descrive l'esito di una risposta del server:
  `SUCCESS`, `ERROR`, `UNAUTHORIZED`, `NOT_FOUND`, `VALIDATION_ERROR`,
  `SERVER_ERROR`
- `Response` — incapsula la risposta del server: contiene un
  `ResponseStatus status`, un `Object payload` con il risultato, e una
  `String messaggio` per i casi di errore o dettaglio aggiuntivo

### DTO delle entità di dominio

- `UtenteDTO`
- `RistoranteDTO` — include `mediaStelle` (double) e `numeroRecensioni` (int),
  calcolati dal server tramite query aggregata al momento della lettura e
  inclusi nel DTO prima della serializzazione. Non sono persistiti come
  colonne sulla tabella ristorante.
- `RecensioneDTO`
- `ServizioDTO`

Nessuna classe del `common` contiene logica applicativa, query SQL o
riferimenti alla GUI. Il suo unico scopo è definire il contratto di
comunicazione tra i due JAR.

## Gestione delle sessioni

Il server gestisce le sessioni utente tramite token opachi generati al momento
del login. Il meccanismo è il seguente.

Al login il server autentica le credenziali, genera un token UUID (`UUID.randomUUID().toString()`),
lo associa all'`UtenteDTO` autenticato e lo registra nel `SessionManager`. La
`Response` al client include il token nel payload. Il client conserva il token
per tutta la durata della sessione e lo include nel campo `sessionToken` di ogni
`Request` successiva.

Il `SessionManager` è una classe server-side che mantiene una
`ConcurrentHashMap<String, UtenteDTO>` dove la chiave è il token e il valore è
l'utente autenticato. Risiede nel layer `server.handler`, che lo consulta per
risolvere il token in arrivo in un `UtenteDTO` prima di invocare il service.
Operazioni che richiedono autenticazione e ricevono un token assente o non
riconosciuto ottengono una `Response` con `status = UNAUTHORIZED`.

Alla disconnessione (RF-21) il token viene rimosso dalla mappa. Poiché la mappa è
condivisa tra i thread dei diversi client, l'uso di `ConcurrentHashMap` garantisce
la correttezza degli accessi concorrenti senza lock espliciti.

## Architettura a layer

Un layer è uno strato di responsabilità. Il principio è che ogni classe
fa una cosa sola e non mescola responsabilità diverse. Nel server di
TheKnife ci sono responsabilità ben distinte che non devono stare nella
stessa classe.

## Layer del server

Il server è organizzato in cinque layer:

### `server.network`

Accetta le connessioni socket in entrata e delega ogni nuova connessione a un
thread gestito tramite `ExecutorService`. Non conosce la logica di business né
il database — sa solo gestire la connessione e passare la `Request` al layer
superiore.

### `server.handler`

Legge la `Request`, verifica il `sessionToken` tramite il `SessionManager`,
esamina il `CommandType` e decide quale operazione eseguire. È il layer che
implementa il pattern Command: ogni `CommandType` corrisponde a un'operazione
ben definita. Non contiene logica di business né query SQL.

### `server.service`

Contiene la logica di business. È qui che vivono i vincoli di dominio —
per esempio: un cliente non può recensire due volte lo stesso ristorante,
solo il gestore di un ristorante può rispondere alle sue recensioni. Non
sa nulla del database né dei servizi esterni: delega ai layer sottostanti.

Ogni operazione di scrittura che coinvolge più entità viene eseguita in
una singola transazione JDBC: il `service` ottiene la connessione,
imposta `setAutoCommit(false)`, esegue le operazioni sui repository, e
invoca `commit()` al termine. In caso di eccezione viene eseguito il
`rollback()`, garantendo che il database non rimanga in uno stato
inconsistente.

Il `service` intercetta le `SQLException` causate da violazioni di
vincoli di unicità del database — ad esempio il tentativo di pubblicare
una seconda recensione sullo stesso ristorante, che viola il constraint
`UNIQUE(id_cliente, id_ristorante)` sulla tabella recensioni — e le
trasforma in una `Response` con `status = ERROR` e un messaggio
leggibile, senza propagare l'eccezione al layer superiore.

### `server.repository`

Parla con PostgreSQL tramite JDBC. Contiene esclusivamente query SQL e
nessuna logica di business. Ogni entità ha il suo repository:
`UtenteRepository`, `RistoranteRepository`, `RecensioneRepository`,
`ServizioRepository`.

I repository utilizzano direttamente i DTO definiti in `theknife-common`
come struttura dati di ritorno, senza introdurre classi entity separate.
Questo evita duplicazione e strati di conversione superflui — una scelta
coerente con l'assenza di un ORM: i DTO vengono popolati direttamente
dal `ResultSet` JDBC e restituiti al `service`. Eventuali campi non
serializzati necessari solo lato server sono marcati `transient`.

`RistoranteRepository` esegue query aggregate (`AVG(stelle)`,
`COUNT(*)`) per calcolare media e numero di recensioni al momento della
lettura, popolando i campi corrispondenti nel `RistoranteDTO` prima di
restituirlo.

### `server.external`

Contiene i client verso servizi HTTP esterni. `GeocodingClient` chiama
OpenCage per convertire indirizzi in coordinate geografiche.
`GeolocationClient` stima la posizione geografica approssimativa a
partire dall'indirizzo IP del client connesso. Entrambi vengono invocati
dal `server.service`.

## Layer del client

Il client JavaFX è organizzato in tre layer.

### `client.app`

Punto di ingresso dell'applicazione JavaFX. Inizializza il `ServerService`
una volta sola all'avvio e lo rende disponibile a tutti i controller per
tutta la durata della sessione.

### `client.service`

Contiene il `ServerService`, classe Singleton che centralizza tutta la
comunicazione col server. Espone metodi ad alto livello che i controller
chiamano direttamente — `cercaRistoranti(...)`, `login(...)`,
`aggiungiRecensione(...)` — e gestisce internamente la costruzione della
`Request`, la serializzazione, l'invio sul socket, la ricezione e la
deserializzazione della `Response`. I controller non conoscono l'esistenza
del socket.

Il `ServerService` conserva il `sessionToken` restituito dal server al
login e lo include automaticamente in ogni `Request` successiva.

Le chiamate al server avvengono su thread separati rispetto al JavaFX
Application Thread per evitare il blocco della GUI durante l'attesa
della risposta.

### `client.ui`

Contiene i controller JavaFX, uno per schermata. Ogni controller chiama
i metodi del `ServerService` e aggiorna la GUI con i dati ricevuti.
Non contiene logica di comunicazione né logica di business.

## Design pattern adottati

### Command

**Dove si applica:** `server.handler`

Il layer `server.handler` riceve una `Request` contenente un
`CommandType` e deve eseguire l'operazione corrispondente. Senza un
pattern strutturato, questo layer diventerebbe un unico blocco
`if/else` o `switch` con decine di rami, impossibile da documentare
con UML e difficile da estendere.

Il pattern Command risolve il problema rappresentando ogni operazione
come un oggetto autonomo. Ogni `CommandType` corrisponde a una classe
handler dedicata che implementa un'interfaccia comune — ad esempio
`CommandHandler` con un metodo `execute(Request, UtenteDTO): Response`.
Il dispatcher legge il `CommandType`, istanzia l'handler corretto e
lo esegue. Aggiungere una nuova operazione significa aggiungere una
nuova classe, senza toccare il codice esistente.

### Singleton

**Dove si applica:** `client.service` — classe `ServerService`

Il `ServerService` gestisce la connessione socket verso il server.
Aprire una connessione per ogni schermata significherebbe avere
connessioni multiple aperte simultaneamente verso lo stesso server,
con overhead inutile e rischio di inconsistenza dello stato di sessione.

Il pattern Singleton garantisce che esista una sola istanza di
`ServerService` per tutta la durata dell'applicazione client.
Viene inizializzata una volta sola in `client.app` all'avvio e
condivisa da tutti i controller tramite un metodo statico
`ServerService.getInstance()`.

### Facade

**Dove si applica:** `client.service` — classe `ServerService`

I controller JavaFX non devono conoscere i dettagli della comunicazione
di rete: apertura del socket, costruzione della `Request`,
serializzazione, attesa della risposta, deserializzazione, gestione
degli errori di rete. Distribuire questa logica in ogni controller
significherebbe duplicazione e accoppiamento forte con il protocollo.

Il pattern Facade risolve il problema esponendo un'interfaccia
semplificata verso questo sottosistema complesso. Il `ServerService`
offre metodi ad alto livello — `cercaRistoranti(...)`, `login(...)`,
`aggiungiRecensione(...)` — che nascondono interamente i dettagli
implementativi. I controller dipendono solo dall'interfaccia del
`ServerService`, non dal protocollo sottostante.

### Observer

**Dove si applica:** `client.ui` — aggiornamento della GUI

I controller JavaFX devono aggiornare le viste quando i dati cambiano
— ad esempio, dopo una ricerca il risultato deve popolare la lista
visibile all'utente. JavaFX implementa nativamente il pattern Observer
tramite il sistema di Property e Binding: le proprietà osservabili
(`ObservableList`, `StringProperty`, ecc.) notificano automaticamente
i componenti grafici quando il loro valore cambia.

L'adozione esplicita di questo pattern garantisce che la GUI rimanga
sempre sincronizzata con i dati senza che i controller debbano
aggiornare manualmente ogni elemento visivo.

### Thread Pool

**Dove si applica:** `server.network`

Il requisito RNF-02 impone che il server gestisca connessioni
concorrenti di più client simultaneamente. Il server utilizza un
`ExecutorService` (thread pool) per gestire le connessioni in ingresso:
il thread principale rimane in ascolto sul socket e, all'arrivo di una
nuova connessione, sottomette un task al pool tramite `execute()`. I
thread del pool lavorano in parallelo senza bloccarsi a vicenda. L'uso
di un pool a dimensione fissa limita il numero massimo di connessioni
gestite contemporaneamente, proteggendo il server dall'esaurimento delle
risorse sotto carico elevato.