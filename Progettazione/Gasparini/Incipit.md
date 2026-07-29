# 1️⃣ Primo problema: cosa rappresenta davvero una Request?

Voglio partire dal ragionamento architetturale, non dal codice.

Quando il client invia qualcosa al server, stiamo modellando:

> “una richiesta di operazione”

Quindi una `Request` deve contenere:

- quale operazione vuoi eseguire
- chi la sta eseguendo
- eventuali dati necessari all’operazione

Queste tre cose sono il cuore del protocollo.

---

# 2️⃣ Ragioniamo sull’enum CommandType

La scelta di usare un enum è ottima.

Perché:

- evita stringhe arbitrarie
- centralizza tutti i comandi
- rende semplice lo `switch` lato server
- evita errori tipo `"login"` vs `"LOGIN"`


### Approccio granulare

```
AGGIUNGI_RECENSIONE, MODIFICA_RECENSIONE, ELIMINA_RECENSIONE
```

### Approccio macro

```
OPERAZIONE_RECENSIONE
```

con payload che specifica il tipo.

---

## Quale conviene?

# enum GRANULARE

Perché:

- più leggibile
- più facile da debuggare
- più semplice da autorizzare per ruolo
- migliore all’orale

---

# 3️⃣ Raggruppamento per ruolo

Le specifiche separano chiaramente:

- guest (ospite)
- cliente
- ristoratore

Quindi l'enum dovrebbe riflettere questa divisione logica.

NON necessariamente con enum separati.

Meglio:

```
public enum CommandType {    // GUEST    OTTIENI_LOCALITA_INIZIALE,    ACCEDI,    REGISTRATI,    CERCA_RISTORANTI,    OTTIENI_DETTAGLI_RISTORANTE,    LEGGI_RECENSIONI,    // CLIENTE    AGGIUNGI_PREFERITO,    RIMUOVI_PREFERITO,    VEDI_PREFERITI,    AGGIUNGI_RECENSIONE,    MODIFICA_RECENSIONE,    ELIMINA_RECENSIONE,  CERCA_VICINO,  ESCI, // RISTORATORE    AGGIUNGI_RISTORANTE,    VEDI_RISTORANTI_GESTITI,    LEGGI_RECENSIONI_RISTORANTI_GESTITI,    RISPONDI_RECENSIONE,  ASSOCIA_RISTORANTE}
```

Nota: `OTTIENI_LOCALITA_INIZIALE` copre RF-01 (località iniziale da IP, schermata
Splash) — comando eseguito dall'Ospite prima di qualsiasi login, invoca
`GeolocationClient` in `server.external`. Mancava nell'enum originale nonostante
il requisito lo richieda esplicitamente.

---

# 4️⃣ Domanda importante:

dove controllate i permessi?

Qui c’è una scelta fondamentale.

## Opzione A

Il client nasconde i pulsanti.

## Opzione B

Il server verifica il ruolo.

## Quindi

La risposta corretta architetturalmente è:

# SEMPRE il server

Perché il client non è affidabile.

Quindi:

- il client può nascondere la GUI
- MA il server deve verificare il ruolo associato alla sessione

Questo impatta direttamente la `Request`.

---

# 5️⃣ Secondo problema: autenticazione/sessione

La Request deve contenere:

- username?
- token?
- sessionId?
- User object completo?


## Cosa NON fare

Inviare l’utente completo ad ogni richiesta

Perché:

- payload enorme
- rischio incoerenze
- problema sicurezza

---

# 6️⃣ Soluzione: session token

Dopo il login:

- il server genera:

```
UUID sessionToken
```

Esempio:

```
550e8400-e29b-41d4-a716-446655440000
```

Il client lo salva.

Ogni Request successiva contiene:

```
String sessionToken;
```

---

# 7️⃣ Ora la domanda più importante della Request:

Come gestire il payload variabile?
Ci sono diverse possibilità.

---

# Opzione 1 — tanti campi opzionali

```
String username;String password;Restaurant restaurant;Review review;
```

Problema:

- Request enorme
- campi null ovunque
- poco elegante

---

# Opzione 2 — Object payload

```
Object payload;
```

Molto flessibile.

Ma:

- cast ovunque
- errori runtime
- meno type-safe

---

# Opzione 3 — Map<String,Object>

```
Map<String,Object> data;
```

Molto dinamico.

Ma:

- quasi nessun type checking
- rischio typo
- difficile da mantenere

---

# 8️⃣ Quale scegliere per TheKnife?

Per un progetto universitario Java distribuito, sarebbe ideale:

## Request con payload Object

MA con DTO dedicati.

Esempio:

```
Request(    CommandType.AGGIUNGI_RECENSIONE,    token,    new AddReviewDTO(...))
```

Quindi:

- Request rimane generica
- payload è tipizzato logicamente
- ogni operazione ha il suo DTO

---

# 9️⃣ Domanda fondamentale:

Perché usare DTO?

Perché:

- separi protocollo da model DB
- eviti di serializzare entity JDBC
- controlli meglio cosa attraversa la rete
- meno coupling

Questa è una motivazione MOLTO forte all’orale.

---

# 🔟 Struttura minima della Request


```
class Request implements Serializable {    private CommandType command;    private String sessionToken;    private Object payload;}
```

La parte più importante di tutto il protocollo:

# La Response

Qui si decidono:

- gestione errori
- UX
- robustezza
- debugging
- manutenibilità

---

# 1️⃣1️⃣ Prima domanda:

Come rappresentare successo/errore?

## Opzione A (sconsigliata)

Eccezioni serializzate

Sconsigliato.

Perché:

- accoppiamento forte
- fragile
- pessimo debugging distribuito

---

## Opzione B (consigliata)

Campo esplicito nella Response

Molto meglio.

Tipo:

```
boolean success;
```

oppure:

```
ResponseStatus status;
```

---

# 1️⃣2️⃣ Implementazione migliore:

## enum ResponseStatus

E' estendibile.

Esempio:

```
SUCCESSERRORUNAUTHORIZEDNOT_FOUNDVALIDATION_ERRORSERVER_ERROR
```

---

# 1️⃣3️⃣ Domanda cruciale:

Dove mettere il messaggio d’errore?

Serve sempre, perché:

- utile per GUI
- utile per debugging
- utile per logging

Quindi:

```
String messaggio;
```

---

# 1️⃣4️⃣ Payload della Response

Stesso problema della Request.

La Response può restituire:

- boolean
- Restaurant
- List<Restaurant>
- Review
- statistiche

Quindi di nuovo:

```
Object payload;
```

con DTO specifici.

---

# 1️⃣5️⃣ Struttura robusta della Response


```
class Response implements Serializable {    private ResponseStatus status;    private String messaggio;    private Object payload;}
```

---

# 1️⃣6️⃣ Esempi concreti

## Caso:

`CERCA_RISTORANTI`

Ritorna:

```
payload = List<RestaurantPreviewDTO>
```

NON il Restaurant completo.

Non serve inviare:
    - recensioni
    - dettagli completi
    - statistiche
    - ownerId

durante una ricerca.

Quindi:

# DTO diversi per schermate diverse

---

# 1️⃣7️⃣ Gestione errori

Ad esempio, avendo:

```
utente non autorizzato
```


## Opzione sbagliata

```
status = ERRORmessage = "errore"
```

## Opzione corretta

```
status = UNAUTHORIZEDmessage = "Solo i ristoratori possono eseguire questa operazione"
```

Perché il client GUI può reagire diversamente:

- popup login
- redirect
- messaggio utente
- refresh sessione

---

# 1️⃣8️⃣ Aspetto architetturale

Se arriva:

```
CommandType = null
```

oppure:

```
CommandType sconosciuto
```

Il server NON deve crashare.

Quindi:

- validare Request
- switch default
- Response con:

```
status = ERROR
messaggio = "Unsupported command"
```

---

# 1️⃣9️⃣ Decisione importante:

Response sempre valida?

Sì, anche in caso di errore.

Cioè:

- il server cerca SEMPRE di restituire una Response
- evita di chiudere brutalmente la socket

Questa è la scelta migliore.

---

# 2️⃣0️⃣ Punto finale: protocollo coerente

Obiettivo:

```
Request:- command- sessionToken- payloadResponse:- status- messaggio- payload
```

con:

- enum chiari
- DTO dedicati
- validazione server-side
- gestione errori esplicita

Questa architettura è:

- semplice
- estendibile
- molto Java-oriented
- perfetta per socket + serialization.


Le specifiche insistono molto su:

- architettura distribuita,
- concorrenza,
- controllo errori,
- qualità progettuale

e il protocollo che stiamo costruendo va proprio in quella direzione.

Ora consolidiamo le scelte e individuiamo i punti delicati.

---

# 1️⃣ Archietttura attuale: valutazione

## Enum granulare

Ottima scelta.

Vantaggi:

- leggibilità
- facilità di debugging
- switch server-side chiaro
- logging molto migliore
- autorizzazioni semplici

Esempio:

```
AGGIUNGI_RECENSIONE, MODIFICA_RECENSIONE, ELIMINA_RECENSIONE
```

molto meglio di:

```
OPERAZIONE_RECENSIONE
```

---

# 2️⃣ Server-side authorization

Anche qui: corretto.

Il client:

- nasconde pulsanti,
- migliora UX,

MA:

- il server è l’unica authority reale.

Questa è una cosa molto importante da dire all’orale.

---

# 3️⃣ Request con Object payload + DTO specifici

Questa è probabilmente la decisione più importante presa finora.

---

# 4️⃣ Che cos’è un DTO in questo caso?

DTO = Data Transfer Object

NON è:

- entity DB,
- DAO,
- model interno.

È:

> un oggetto progettato specificamente per attraversare la rete.

Esempio:

```
AddReviewDTO
```

contiene SOLO:

```
restaurantIdstarstext
```

NON:

- connessioni DB
- logica
- metodi JDBC
- liste inutili
- oggetti annidati enormi

---

# 5️⃣ Perché è importante?

Perché separa:

## protocollo di rete

da

## implementazione interna del server

Questo è un principio architetturale molto forte.

---

# 6️⃣ Flusso corretto lato server


```
client GUI    ↓Request    ↓DTO    ↓Service layer    ↓DAO    ↓PostgreSQL
```

Questa separazione è ottima.

---

# 7️⃣ Attenzione

Mai serializzare direttamente:

- DAO
- Connection
- ResultSet
- entity JDBC “vive”

Attraverso socket devono passare SOLO:

# oggetti serializzabili puri.

---

# 8️⃣ Punto delicato: cast del payload

Con:

```
Object payload;
```

prima o poi sarà necessario:

```
AddReviewDTO dto = (AddReviewDTO) request.getPayload();
```

Questa è la parte fragile.

---

# 9️⃣ Come evitare problemi?

Lo switch sul `CommandType` deve essere coerente col DTO atteso.

Esempio:

```
case AGGIUNGI_RECENSIONE -> {    AddReviewDTO dto =        (AddReviewDTO) request.getPayload();}
```

NON:

```
RestaurantDTO dto = ...
```

---

# 🔟 Domanda importante:

Cosa succede se il payload è sbagliato?

Esempio:

```
command = AGGIUNGI_RECENSIONE
payload = LoginDTO
```

Ci sono due possibilità:

---

## Opzione 1 — crash (errata)

---

## Opzione 2 — validazione server

Esempio:

```
if (!(payload instanceof AddReviewDTO))
```

e restituiamo:

```
ResponseStatus.VALIDATION_ERROR
```

Questa è una protezione molto importante.

---

# 1️⃣1️⃣ Response sempre valida

Questa è una scelta ECCELLENTE.

Perché in sistemi distribuiti:

- connessioni chiuse brutalmente
- eccezioni non gestite
- stacktrace lato client

sono un inferno.

Quindi:

# il server dovrebbe SEMPRE tentare di inviare una Response.

Anche:

- SQL exception
- validazione fallita
- comando sconosciuto
- errore interno

---

# 1️⃣2️⃣ Decisione importante:

Errori tecnici o errori utente?

Esempi:

## errore utente

```
password errata
```

## errore tecnico

```
SQLException
```

Il client NON deve vedere il messaggio SQL reale.

Quindi:

- log tecnico sul server
- messaggio “pulito” nella Response

Esempio:

```
SERVER_ERROR"Errore interno del server"
```

---

# 1️⃣3️⃣ Grande attenzione al versioning

Usando:

```
ObjectInputStream/ObjectOutputStream
```

qualsiasi modifica alle classi serializzate può rompere compatibilità.

Quindi:

# IMPORTANTISSIMO

tutti devono sincronizzare bene GitHub.

---

# 1️⃣4️⃣ Consiglio IMPORTANTISSIMO per Maven

Creare un modulo condiviso tipo:

```
theknife-common
```

contenente:

- Request
- Response
- DTO
- enum
- classi serializzabili condivise

Poi:

- clientTK dipende da common
- serverTK dipende da common

QUESTA è praticamente obbligatoria in un’architettura così.

Altrimenti:

- classi duplicate
- serialVersionUID incompatibili
- ClassNotFoundException
- caos totale

---

# 1️⃣5️⃣ ATTENZIONE ai serialVersionUID

Usando la serializzazione Java, va messo SEMPRE.


Esempio:

```
private static final long serialVersionUID = 1L;
```

in:

- Request
- Response
- tutti i DTO

---

# 1️⃣6️⃣ ATTENZIONE alla concorrenza server

Le specifiche richiedono utenti multipli concorrenti

Quindi:

- un thread per client  
    oppure
- thread pool

Ma:

# mai condividere state mutabile globale senza sincronizzazione.

Specialmente:

- sessioni
- cache
- utenti online

---

# 1️⃣7️⃣ ATTENZIONE alla sessione

Per quanto riguarda dove memorizzare i token, probabilmente:


```
ConcurrentHashMap<String, SessionData>
```

sul server.

Questa è una scelta buona.

---

# 1️⃣8️⃣ Scaletta consigliata (IMPORTANTISSIMA)

Seguire QUESTO ordine.

---

# FASE 1 — common module

Creare:

- enum CommandType
- enum ResponseStatus
- Request
- Response
- DTO base

SENZA GUI.  
SENZA DB.

Solo protocollo.

---

# FASE 2 — networking minimo

Implementare:

- server socket
- accept()
- thread client
- invio/ricezione Request/Response

Con test semplicissimi:

```
PING -> PONG
```

Prima di tutto.

---

# FASE 3 — autenticazione

Implementare:

- ACCEDI
- REGISTRATI
- token sessione

e:

- validazione ruolo
- gestione sessioni

---

# FASE 4 — service layer

Creare:

- RestaurantService
- ReviewService
- AuthService

Qui va messa la logica applicativa.

---

# FASE 5 — DAO layer

Solo ora:

- JDBC
- query SQL
- PostgreSQL

Separare bene:

```
Service ≠ DAO
```

---

# FASE 6 — comandi guest

Implementare:

- ricerca ristoranti
- dettagli
- recensioni

---

# FASE 7 — comandi cliente

Implementare:

- preferiti
- recensioni

---

# FASE 8 — comandi ristoratore

Implementare:

- aggiunta ristorante
- risposta recensioni
- statistiche

---

# FASE 9 — gestione errori seria

Aggiungere:

- logging
- validazione
- timeout
- gestione socket chiuse

---
