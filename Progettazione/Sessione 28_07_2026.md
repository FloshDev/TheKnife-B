# Sessione 28/07/2026
## Issue aperti
### I1 — Model separati nel common: RISOLTO
**Conflitto:** il diagramma package prevede `common.model` (Utente, Ristorante,
Recensione) accanto a `common.dto`. Il Documento di Architettura prevede solo i
DTO: i repository popolano direttamente i DTO dal ResultSet, senza classi entity
separate.

**Decisione:** si eliminano i model. Un solo oggetto per entità — il DTO — che
nasce nel repository (popolato dal ResultSet), viaggia sul socket e arriva al
client. Nessun package `common.model`.

**Motivazione:** la separazione model/DTO ha senso quando le entità contengono
logica di dominio "vera", cioè regole che l'entità può applicare guardando solo
sé stessa (i propri campi), senza interrogare il database né sapere chi sta
chiedendo. Nel nostro progetto la logica non è di questo tipo: è relazionale
(es. "un cliente non recensisce due volte lo stesso ristorante" — richiede di
guardare le altre recensioni) o autorizzativa (es. "solo il gestore risponde" —
richiede di confrontare l'utente con il gestore). Entrambe vivono nel layer
`service`, non dentro le entità. Le nostre entità restano quindi scatole di dati:
avere anche i model significherebbe scrivere classi identiche ai DTO più codice
di conversione campo-per-campo, per nessun guadagno.

Motivo aggiuntivo, specifico di un sistema distribuito: ogni classe nel `common`
è una classe che deve restare sincronizzata tra i due processi (client e server).
Tenere il `common` magro — solo DTO e protocollo — riduce la superficie su cui
client e server possono divergere.

**Condizione di riapertura:** se durante lo sviluppo un'entità accumula una
regola applicabile guardando solo sé stessa (esempio ipotetico: "una recensione
è modificabile solo entro 48h dalla pubblicazione e diventa immodificabile dopo
la prima risposta" — valutabile dai soli campi della recensione), allora per
quella specifica entità un oggetto con comportamento torna a essere giustificato.
La decisione va rivista solo in quel caso e solo per quell'entità.

**Azione:** rimuovere `common.model` dal diagramma package (`DiagrammaPackage.drawio`)
in `Doc. Conclusi`, così che diagramma e Documento di Architettura tornino coerenti.

### I2 / I7 — Layer dati: repository vs DAO: RISOLTO
**Conflitto:** il Documento di Architettura chiama il layer dati
`server.repository`, con classi `UtenteRepository`, `RistoranteRepository`, ecc.
Il diagramma package (I2) e l'Incipit di Gasparini (I7) chiamano lo stesso layer
`server.dao`, con classi `*DAO`. Stessa responsabilità — eseguire SQL via JDBC e
popolare i DTO dal ResultSet — con due nomi diversi in tre documenti.
**Decisione:** si unifica su **DAO**. Package `server.dao`; classi `UtenteDAO`,
`RistoranteDAO`, `RecensioneDAO`, `ServizioDAO`. Il nome "Repository" viene
abbandonato ovunque.
**Motivazione:** DAO e Repository non sono sinonimi, portano presupposti diversi.
Il DAO (Data Access Object) è il pattern JDBC canonico: una classe per tabella,
metodi che mappano su operazioni SQL (`insert`, `findById`, `update`, `delete`),
mappatura ResultSet → oggetto fatta a mano. Presuppone solo l'esistenza di uno
statement da eseguire e di una struttura da riempire. Il Repository (Domain-Driven
Design) presuppone di più: è una collezione collection-like di *oggetti di dominio
ricchi* — entità con comportamento e regole proprie — di cui nasconde del tutto la
persistenza, e nella pratica vive appoggiato a un ORM (JPA/Hibernate) che produce
quell'illusione.
Nel nostro progetto quel presupposto non esiste. Lo stack è JDBC puro, senza ORM.
E soprattutto I1 ha eliminato i model: nel `common` restano solo DTO, cioè scatole
di dati senza comportamento. Chiamare "Repository" delle classi che eseguono query
a mano e restituiscono DTO nominerebbe un'architettura che non abbiamo: prometterebbe
il dominio ricco e l'astrazione della persistenza che I1 ha esplicitamente rimosso.
"DAO" descrive invece esattamente ciò che quelle classi sono. È la stessa scelta di
I1 vista da un altro lato: tolto il comportamento dalle entità, il nome onesto per
il layer dati è quello che non pretende comportamento.
Nota sul verso della correzione: qui è il Documento di Architettura a doversi
adeguare al diagramma e all'Incipit, non viceversa — a differenza delle altre issue
tra "conclusi", dove il diagramma è quello divergente.
**Azione:** correggere il Documento di Architettura: `server.repository` → `server.dao`,
`*Repository` → `*DAO`, ovunque compaia (nomi di package, nomi di classe, firme di
metodo, testo delle sezioni). Verificare che diagramma e Incipit siano già coerenti al
ricaricamento. Controllare l'output SQL/query di Scolaro: se referenzia nomi di classe
del layer dati, allinearlo a `*DAO`.

## Piano operativo — chi fa cosa (breve termine)

Guida per applicare tutte le decisioni I1–I7 sui documenti. Ogni membro apre il
proprio file, cerca il testo "PRIMA", lo sostituisce con "DOPO". Fatto.

> Nota: in questo documento sono scritte per esteso solo le decisioni I1 e I2/I7.
> Le decisioni I3–I6 (sotto) sono state prese a voce e sono qui trascritte come
> istruzioni operative. Se servono anche le motivazioni per esteso, vanno aggiunte
> come sezioni I3–I6 sopra (task PM, non urgente).

### Riepilogo decisioni (una riga ciascuna)
- **I1** — niente `common.model`: una sola classe per entità, il DTO. (→ diagramma)
- **I2/I7** — layer dati si chiama **DAO**, non Repository. (→ Documento Architettura)
- **I3** — client a **4 layer**: `app`, `service`, `network`, `ui`. Niente
  `ServerService`: la connessione sta in `ServerConnection` (`client.network`) che
  porta i pattern **Singleton** e **Facade**; in `client.service` stanno tre service:
  `AuthService`, `RistoranteService`, `RecensioneService`. (→ Documento Architettura)
- **I4** — l'interfaccia del pattern Command si chiama **`Command`** (non
  `CommandHandler`, non `ICommand`), con `Dispatcher`. (→ Documento Architettura + diagramma)
- **I5** — i valori dell'enum `CommandType` sono **in italiano**. (→ Incipit)
- **I6** — il campo messaggio della Response si chiama **`messaggio`** (non `message`).
  (→ Incipit)

---

### FLAVIO (PM) — `Progettazione/1_Doc. Conclusi/Documento di Architettura del Sistema.md`

**Fix 1 — I2/I7: Repository → DAO** (righe 119, 131, 134, 135–136, 138, 145)
- `server.repository` → `server.dao` (anche header di sezione riga 131)
- `UtenteRepository` → `UtenteDAO`, `RistoranteRepository` → `RistoranteDAO`,
  `RecensioneRepository` → `RecensioneDAO`, `ServizioRepository` → `ServizioDAO`
- prosa: "i repository" / "il suo repository" → "i DAO" / "il suo DAO"

**Fix 2 — I3: client a 4 layer, niente ServerService** (sezione "Layer del client", ~158–189)
- Aggiungere il layer `client.network`. I quattro layer diventano:
  `client.app`, `client.service`, `client.network`, `client.ui`.
- `client.network` contiene **`ServerConnection`**: apre il socket, serializza la
  `Request`, riceve la `Response`, conserva il `sessionToken`. È l'unica istanza
  (Singleton) e nasconde la rete ai controller (Facade).
- `client.service` contiene **tre** classi: `AuthService`, `RistoranteService`,
  `RecensioneService`, che chiamano `ServerConnection`.
- Ovunque compaia `ServerService` (righe 164,170,178,188,213,215,221,224,228,237,241)
  → sostituire col riferimento corretto: `ServerConnection` per socket/Singleton/Facade,
  o il service specifico per le operazioni di dominio.

**Fix 3 — I3: pattern Singleton e Facade su ServerConnection** (sez. Singleton ~212, Facade ~226)
- Le due sezioni ora dicono "`ServerService` in `client.service`" → cambiare in
  "**`ServerConnection`** in `client.network`".

**Fix 4 — I4: CommandHandler → Command** (riga 206–207)
- `CommandHandler` → `Command`. Mantieni `Dispatcher` e la firma
  `execute(Request, UtenteDTO): Response`.

**Fix 5 — I5: coerenza esempi enum** (riga 36)
- Verifica che gli esempi di `CommandType` siano italiani (già `CERCA_RISTORANTI`,
  `AGGIUNGI_RECENSIONE`; concorda con Gasparini se `LOGIN` resta o diventa `ACCEDI`).

> Il diagramma package non è di Flavio: lo aggiorna Barlera (vedi sotto).

---

### GASPARINI — `Progettazione/Gasparini/Incipit.md`

**Fix 1 — I5: enum CommandType in italiano** (blocco enum riga 89; occorrenze sparse
46, 52, 241, 401, 567, 573, 707, 725, 977–978)
- Traduci i valori. Mapping proposto (nomi definitivi da confermare col PM):

  | Inglese | Italiano |
  |---|---|
  | LOGIN | ACCEDI (o LOGIN) |
  | REGISTER | REGISTRATI |
  | SEARCH_RESTAURANTS | CERCA_RISTORANTI |
  | GET_RESTAURANT_DETAILS | DETTAGLIO_RISTORANTE |
  | GET_REVIEWS | LEGGI_RECENSIONI |
  | ADD_FAVORITE | AGGIUNGI_PREFERITO |
  | REMOVE_FAVORITE | RIMUOVI_PREFERITO |
  | GET_FAVORITES | LEGGI_PREFERITI |
  | ADD_REVIEW | AGGIUNGI_RECENSIONE |
  | UPDATE_REVIEW | MODIFICA_RECENSIONE |
  | DELETE_REVIEW | ELIMINA_RECENSIONE |
  | ADD_RESTAURANT | AGGIUNGI_RISTORANTE |
  | GET_OWNER_RESTAURANTS | LEGGI_RISTORANTI_GESTITI |
  | GET_OWNER_REVIEWS | LEGGI_RECENSIONI_GESTITE |
  | REPLY_TO_REVIEW | RISPONDI_RECENSIONE |

- Aggiungere i comandi mancanti coperti dai requisiti ma assenti dall'enum:
  `LOGOUT` (RF-21), `ASSOCIA_RISTORANTE` (RF-17), `CERCA_VICINO` "Vicino a me" (RF-07).

**Fix 2 — I6: message → messaggio** (righe 360, 392, 518; testo riga 489)
- Nel campo della `Response`: `String message` → `String messaggio`.

**Nota:** l'Incipit usa già `DAO` (I2/I7 ok) e non ha `common.model` come package
(I1 ok). Se preferisci, invece di allineare l'Incipit puoi marcarlo "superato dal
Documento di Architettura" — decidi col PM.

**common (codice):** nessun modulo `theknife-common` esiste ancora. Alla stesura del
codice, rispettare: solo DTO (no model), enum italiano, campo `messaggio`, interfaccia
`Command`.

---

### SCOLARO — SQL e query — cartella `Progettazione/Scolaro/`

- **Stato attuale: cartella vuota** (solo `.gitkeep`). Nessuno script SQL nella repo.
- Non c'è niente da correggere adesso.
- Quando produci gli script: se il testo referenzia classi del layer dati, usa i nomi
  `*DAO` (mai `Repository`). Nessun altro vincolo da I1–I7 sul SQL.

---

### BARLERA — diagramma package + mappa schermate

**Fix 1 — I1 + I4 sul diagramma package** — file
`Progettazione/1_Doc. Conclusi/DiagrammaPackage.drawio`, modifica **a mano in draw.io**:
- **[I1]** rimuovere il package `c.common.model` (Utente, Ristorante, Recensione) —
  restano solo `c.common.dto` e `c.common.protocol`.
- **[I4]** nel package `c.server.handler`: rinominare `ICommand` → `Command`
  (lasciare `Dispatcher` e `LoginCommand`).
- Farlo **dopo** che Flavio ha stabilizzato il Documento di Architettura, così
  diagramma e documento restano coerenti 1:1.

**Mappa schermate** — `Progettazione/Barlera/mappa-schermate.md`
- **Nessun task da I1–I7.** Verificato: i termini toccati dalle decisioni non compaiono
  ("Login"/"Logout" nel file sono etichette di schermata GUI, non valori `CommandType`).

---

### Ordine consigliato (per non correggere due volte)
1. **Gasparini** fissa i nomi condivisi: enum italiano (I5) + campo `messaggio` (I6) +
   concorda con Flavio il nome interfaccia `Command` (I4).
2. **Flavio** riscrive il Documento di Architettura: DAO (I2/I7), 4 layer client +
   ServerConnection (I3), `Command` (I4), esempi enum italiani (I5) — usando i nomi
   fissati al punto 1.
3. **Barlera** aggiorna a mano il diagramma draw.io (I1 + I4) quando il Documento di
   Architettura è stabile.
4. **Scolaro**: nessuna azione ora.

Motivo dell'ordine: `common` (protocollo: enum, Response, interfacce) precede server e
client; fissati quei nomi, tutte le riscritture testuali usano già la forma definitiva.
