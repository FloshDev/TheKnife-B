# 1️⃣ Primo problema: cosa rappresenta davvero una Request?

Voglio partire dal ragionamento architetturale, non dal codice.

Quando il client invia qualcosa al server, state modellando:

> “una richiesta di operazione”

Quindi una `Request` deve contenere:

- quale operazione vuoi eseguire
- chi la sta eseguendo
- eventuali dati necessari all’operazione

Queste tre cose sono il cuore del protocollo.

---

# 2️⃣ Ragioniamo sull’enum CommandType

La vostra scelta di usare un enum è ottima.

Perché?

Perché:

- evita stringhe arbitrarie
- centralizza tutti i comandi
- rende semplice lo `switch` lato server
- evita errori tipo `"login"` vs `"LOGIN"`

Ora però c’è una decisione importante.

## Domanda progettuale:

Volete un enum:

- MOLTO granulare
- oppure più “macro”?

Esempio.

### Approccio granulare

```
ADD_REVIEWDELETE_REVIEWUPDATE_REVIEW
```

### Approccio macro

```
REVIEW_OPERATION
```

con payload che specifica il tipo.

---

## Quale vi conviene?

Nel vostro caso io vi spingerei verso:

# ✅ enum GRANULARE

Perché:

- più leggibile
- più facile da debuggare
- più semplice da autorizzare per ruolo
- migliore all’orale

---

# 3️⃣ Raggruppamento per ruolo

Le specifiche separano chiaramente:

- guest
- cliente
- ristoratore

Quindi il vostro enum dovrebbe riflettere questa divisione logica.

NON necessariamente con enum separati.

Meglio:

```
public enum CommandType {    // GUEST    LOGIN,    REGISTER,    SEARCH_RESTAURANTS,    GET_RESTAURANT_DETAILS,    GET_REVIEWS,    // CLIENTE    ADD_FAVORITE,    REMOVE_FAVORITE,    GET_FAVORITES,    ADD_REVIEW,    UPDATE_REVIEW,    DELETE_REVIEW,    // RISTORATORE    ADD_RESTAURANT,    GET_OWNER_RESTAURANTS,    GET_OWNER_REVIEWS,    REPLY_TO_REVIEW}
```

---

# 4️⃣ Domanda importante:

dove controllate i permessi?

Qui c’è una scelta fondamentale.

## Opzione A

Il client nasconde i pulsanti.

## Opzione B

Il server verifica il ruolo.

Quale scegliete?

👉 La risposta corretta architetturalmente è:

# ✅ SEMPRE il server

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

Qui bisogna stare attenti.

## Cosa NON fare

❌ inviare l’utente completo ad ogni richiesta

Perché:

- payload enorme
- rischio incoerenze
- problema sicurezza

---

# 6️⃣ Soluzione consigliata: session token

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

come gestire il payload variabile?

Qui avete diverse possibilità.

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

Per un progetto universitario Java distribuito:

# ✅ io vi consiglierei:

## Request con payload Object

MA con DTO dedicati.

Esempio:

```
Request(    CommandType.ADD_REVIEW,    token,    new AddReviewDTO(...))
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

Io ragionerei su:

```
class Request implements Serializable {    private CommandType command;    private String sessionToken;    private Object payload;}
```

Ora però arriva la parte più importante di tutto il protocollo.

# ⚠️ La Response

Qui si decidono:

- gestione errori
- UX
- robustezza
- debugging
- manutenibilità

---

# 1️⃣1️⃣ Prima domanda:

Come rappresentare successo/errore?

## Opzione A

Eccezioni serializzate

❌ Sconsigliato.

Perché:

- accoppiamento forte
- fragile
- pessimo debugging distribuito

---

## Opzione B

Campo esplicito nella Response

✅ Molto meglio.

Tipo:

```
boolean success;
```

oppure:

```
ResponseStatus status;
```

---

# 1️⃣2️⃣ Io vi consiglierei fortemente:

## enum ResponseStatus

Perché è estendibile.

Esempio:

```
SUCCESSERRORUNAUTHORIZEDNOT_FOUNDVALIDATION_ERRORSERVER_ERROR
```

---

# 1️⃣3️⃣ Domanda cruciale:

Dove mettete il messaggio d’errore?

Serve sempre?

Secondo me:

# ✅ sì

Perché:

- utile per GUI
- utile per debugging
- utile per logging

Quindi:

```
String message;
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

Io la immaginerei così:

```
class Response implements Serializable {    private ResponseStatus status;    private String message;    private Object payload;}
```

---

# 1️⃣6️⃣ Adesso ragioniamo sui casi reali

## Caso:

`SEARCH_RESTAURANTS`

Cosa ritorna?

```
payload = List<RestaurantPreviewDTO>
```

NON il Restaurant completo.

Perché?

Domanda per voi:

- vi serve davvero inviare:
    - recensioni
    - dettagli completi
    - statistiche
    - ownerId

durante una ricerca?

Probabilmente no.

Quindi:

# DTO diversi per schermate diverse

Questo è design serio.

---

# 1️⃣7️⃣ Gestione errori: decisione critica

Immagina:

```
utente non autorizzato
```

Cosa fate?

## Opzione cattiva

```
status = ERRORmessage = "errore"
```

## Opzione migliore

```
status = UNAUTHORIZEDmessage = "Solo i ristoratori possono eseguire questa operazione"
```

Perché?

Perché il client GUI può reagire diversamente:

- popup login
- redirect
- messaggio utente
- refresh sessione

---

# 1️⃣8️⃣ Ultima domanda architetturale importante

Cosa succede se arriva:

```
CommandType = null
```

oppure:

```
CommandType sconosciuto
```

Il server NON deve crashare.

Quindi:

- validate Request
- switch default
- Response con:

```
status = ERRORmessage = "Unsupported command"
```

---

# 1️⃣9️⃣ Decisione importante:

Response sempre valida?

Io direi:

# ✅ sì

Anche in caso di errore.

Cioè:

- il server cerca SEMPRE di restituire una Response
- evita di chiudere brutalmente la socket

Questa è una scelta molto professionale.

---

# 2️⃣0️⃣ Punto finale: protocollo coerente

Il vostro obiettivo dovrebbe essere:

```
Request:- command- sessionToken- payloadResponse:- status- message- payload
```

con:

- enum chiari
- DTO dedicati
- validazione server-side
- gestione errori esplicita

Questa architettura è:

- semplice
- estendibile
- difendibile all’esame
- molto Java-oriented
- perfetta per socket + serialization.


Le specifiche insistono molto su:

- architettura distribuita,
- concorrenza,
- controllo errori,
- qualità progettuale

e il protocollo che state costruendo va proprio in quella direzione.

Ora facciamo un passo avanti: consolidiamo le vostre scelte e individuiamo i punti delicati.

---

# 1️⃣ La vostra architettura attuale: valutazione

## ✅ Enum granulare

Ottima scelta.

Vantaggi:

- leggibilità
- facilità di debugging
- switch server-side chiaro
- logging molto migliore
- autorizzazioni semplici

Esempio:

```
ADD_REVIEWDELETE_REVIEWLOGINSEARCH_RESTAURANTS
```

molto meglio di:

```
REVIEW_OPERATION
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

Questa è probabilmente la scelta più importante che avete fatto.

Qui però bisogna capire bene COSA state facendo davvero.

---

# 4️⃣ Che cos’è un DTO nel vostro caso?

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

Immaginate:

```
client GUI    ↓Request    ↓DTO    ↓Service layer    ↓DAO    ↓PostgreSQL
```

Questa separazione è ottima.

---

# 7️⃣ Attenzione IMPORTANTISSIMA

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

prima o poi farete:

```
AddReviewDTO dto = (AddReviewDTO) request.getPayload();
```

Questa è la parte fragile.

---

# 9️⃣ Come evitare problemi?

Qui serve disciplina progettuale.

Lo switch sul `CommandType` deve essere coerente col DTO atteso.

Esempio:

```
case ADD_REVIEW -> {    AddReviewDTO dto =        (AddReviewDTO) request.getPayload();}
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
command = ADD_REVIEWpayload = LoginDTO
```

Qui avete due possibilità:

---

## Opzione 1 — crash

Brutta.

---

## Opzione 2 — validazione server

Molto meglio.

Esempio:

```
if (!(payload instanceof AddReviewDTO))
```

e restituite:

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

# 1️⃣2️⃣ Qui c’è una decisione molto importante:

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

Secondo voi:

- il client deve vedere il messaggio SQL reale?

👉 Assolutamente no.

Quindi:

- log tecnico sul server
- messaggio “pulito” nella Response

Esempio:

```
SERVER_ERROR"Errore interno del server"
```

Questo è molto professionale.

---

# 1️⃣3️⃣ Grande attenzione al versioning

Dato che usate:

```
ObjectInputStream/ObjectOutputStream
```

qualsiasi modifica alle classi serializzate può rompere compatibilità.

Quindi:

# IMPORTANTISSIMO

tutti devono sincronizzare bene GitHub.

---

# 1️⃣4️⃣ Consiglio IMPORTANTISSIMO per Maven

Create un modulo condiviso tipo:

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

Dato che usate serializzazione Java:

# mettetelo SEMPRE.

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

Domanda importante:  
dove memorizzate i token?

Probabilmente:

```
ConcurrentHashMap<String, SessionData>
```

sul server.

Questa è una scelta buona.

---

# 1️⃣8️⃣ Scaletta consigliata (IMPORTANTISSIMA)

Vi conviene seguire QUESTO ordine.

---

# FASE 1 — common module

Create:

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

Implementate:

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

Implementate:

- LOGIN
- REGISTER
- token sessione

e:

- validazione ruolo
- gestione sessioni

---

# FASE 4 — service layer

Create:

- RestaurantService
- ReviewService
- AuthService

Qui mettete logica applicativa.

---

# FASE 5 — DAO layer

Solo ora:

- JDBC
- query SQL
- PostgreSQL

Separate bene:

```
Service ≠ DAO
```

---

# FASE 6 — comandi guest

Implementate:

- ricerca ristoranti
- dettagli
- recensioni

---

# FASE 7 — comandi cliente

Implementate:

- preferiti
- recensioni

---

# FASE 8 — comandi ristoratore

Implementate:

- aggiunta ristorante
- risposta recensioni
- statistiche

---

# FASE 9 — gestione errori seria

Aggiungete:

- logging
- validazione
- timeout
- gestione socket chiuse

---

# 2️⃣0️⃣ Ultimo consiglio davvero importante

NON cercate subito:

- perfezione,
- genericità estrema,
- reflection,
- framework complessi.

Per un progetto universitario:

# semplicità + coerenza architetturale

vince SEMPRE.