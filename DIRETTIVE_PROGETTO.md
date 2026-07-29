# Direttive di progetto — Laboratorio Interdisciplinare B

Estratto fedele dalle slide ufficiali del corso (Specifiche Progetto,
Documentazione Progetto, Maven quickstart, Introduzione Corso). È materiale del
docente, non del team: nessuna decisione interna può contraddirlo. Se una scelta
architetturale entra in conflitto con quanto scritto qui, vince questo documento.

---

## 1. Il progetto

TheKnife è una piattaforma che consente di trovare ristoranti in tutto il mondo e
selezionarli in base al luogo, alla tipologia del ristorante, alla fascia di
prezzo, alla possibilità di prenotare un tavolo o di ordinare da asporto. Simula
alcune delle funzionalità della piattaforma TheFork.

A partire da un repository di dati da costruire, l'applicazione permette:

- **ai ristoratori** di creare i propri ristoranti, definirne le caratteristiche,
  visualizzarne le recensioni e rispondere ad esse;
- **agli utenti** di cercare ristoranti in base alle caratteristiche desiderate,
  inserire, modificare e cancellare le recensioni.

### Struttura della piattaforma

La piattaforma consiste di due moduli:

1. **serverTK** — interfacciandosi con un DBMS relazionale (PostgreSQL), fornisce
   servizi di back-end.
2. **clientTK** — fornisce tutti i servizi e le funzionalità designate per gli
   utilizzatori dell'applicazione.

Al lancio di `serverTK` deve essere richiesto di specificare:

- le credenziali per accedere al `dbTK`, database di supporto all'esecuzione dei
  servizi della piattaforma;
- l'host del DB.

Una volta lanciato, `serverTK` deve rimanere in attesa di richieste di connessione
da parte dei client `clientTK`.

L'applicazione deve essere realizzata in modo tale da **supportare l'interazione
in parallelo con più utenti** connessi alla piattaforma da postazioni (client)
differenti.

### Interfaccia richiesta

All'avvio l'applicazione mostra un menù iniziale in cui è possibile loggarsi o
registrarsi alla piattaforma. In alternativa è possibile proseguire come utente
*guest* indicando solo il nome di un luogo, accedendo alle funzionalità che non
richiedono autenticazione.

L'applicazione deve fornire **almeno**:

- una schermata con l'elenco dei ristoranti vicini al luogo indicato dall'utente
  guest, o al domicilio in caso di utente loggato;
- una schermata di ricerca dei ristoranti.

---

## 2. Database

Prima di progettare l'applicazione è necessario costruire un database con una
tabella denominata **`RistorantiTheKnife`**, che deve contenere almeno i seguenti
campi:

- nome del ristorante
- informazioni sul luogo: nazione, città, indirizzo, latitudine, longitudine
- fascia di prezzo (prezzo medio in euro)
- disponibilità del servizio di delivery (sì/no)
- disponibilità del servizio di prenotazione online (sì/no)
- tipo di cucina

Il docente fornisce un file draft di partenza con coordinate e nomi di località:
`michelin_my_maps.csv`, disponibile sulla pagina e-learning
(fonte: kaggle.com/datasets/ngshiheng/michelin-guide-restaurants-2021).

---

## 3. Codice sorgente

1. Il progetto deve essere sviluppato in linguaggio **Java** (versione recente) e
   deve essere **multipiattaforma**.
2. Il codice deve essere opportunamente commentato in **formato JavaDoc**.
3. Il package **`theknife`** deve essere definito e deve contenere le relative
   classi. Ulteriori package sono ammessi.
4. Sono richiesti **due eseguibili `.jar` separati** per l'esecuzione di client e
   server.
5. L'intestazione di tutti i file `*.java` deve contenere **nome, cognome, numero
   di matricola e sede (VA o CO)** degli autori del progetto.

---

## 4. Consegna

La consegna avviene compilando una form in cui va indicato il link al repository
GitHub. **Responsabile della consegna è il project manager.** Le date di consegna
e il link alla form vengono comunicati di volta in volta, indicativamente 30
giorni prima della data di appello d'esame.

Il repository deve contenere, **nella cartella radice**:

| Elemento | Contenuto |
|---|---|
| `autori.txt` | Cognome, nome, matricola e sede (VA o CO) di ogni membro del team. In caso di progetto di gruppo, link al repository GitHub di progetto. |
| `doc/` | Manuale utente e manuale tecnico in formato `.pdf`, tutti gli artefatti prodotti (diagrammi ER, UML), la JavaDoc generata. |
| `bin/` | I file eseguibili `.jar` dell'applicazione. |
| `src/` | Il codice sorgente del progetto. |
| `lib/` | Eventuali librerie esterne per compilazione ed esecuzione. |
| `pom.xml` | Build Maven: compilare il progetto, creare il database, creare la documentazione JavaDoc, ecc. |
| `README.txt` | Indicazioni precise su installazione e compilazione, comandi Maven da utilizzare, indicazioni su librerie usate in modo non standard. |

---

## 5. Documentazione

Tre deliverable distinti: **Manuale Utente**, **Manuale Tecnico**, **JavaDoc**.

### Manuale Utente

Manuale di alto livello del funzionamento del programma: quale ambiente usare,
come installare l'ambiente, come installare il programma, come eseguirlo.
Aggiungere screenshot per semplificare la comprensione del funzionamento e
dell'uso. Suddividere in macro sezioni attinenti al progetto. Utilizzare un
livello di astrazione e un linguaggio adeguati a un **pubblico non esperto e non
tecnico**.

Struttura richiesta:

- Frontespizio (titolo, autori, data e versione del documento)
- Indice
- Installazione: requisiti di sistema, setup ambiente, installazione programma
- Esecuzione e uso: setup e lancio del programma, uso delle funzionalità
- Data set di test
- Limiti della soluzione sviluppata
- Sitografia / Bibliografia

### Manuale Tecnico

Segue la **stessa struttura del Manuale Utente**, con livello di astrazione e
linguaggio adeguati a esperti tecnici del dominio.

Deve dettagliare dal punto di vista tecnico la struttura dell'applicazione:

- le scelte progettuali (diagrammi UML ed ER)
- le scelte architetturali
- le strutture dati utilizzate
- le scelte algoritmiche
- l'uso eventuale di pattern, riportando parti di codice significativo

La JavaDoc è parte del manuale tecnico: il codice deve quindi essere commentato
opportunamente.

**Progettazione software.** La documentazione del sistema client/server va fatta
con UML. L'UML deve mostrare la **struttura statica** del sistema a un livello di
astrazione maggiore del codice sorgente (Class Diagram) e la **struttura
dinamica**, catturando le principali interazioni fra classi attraverso Sequence
Diagram, Interaction Diagram e State Diagram. Non è necessario usare tutti questi
diagrammi, a patto che l'insieme prodotto sia sufficiente a rappresentare
chiaramente il comportamento del sistema, sia strutturale sia comportamentale.

**Progettazione database.** Documento di analisi dei requisiti ristrutturato e
documentazione associata allo schema ER **ristrutturato e non ristrutturato**, con
eventuale specifica di vincoli in linguaggio naturale.

### JavaDoc

JavaDoc documenta i file sorgente usando le informazioni riportate al loro
interno. Un commento JavaDoc è testo HTML racchiuso tra `/**` e `*/`. È pensato
solo per descrivere package, classi, interfacce e metodi — **non può essere usato
per commentare il codice**.

La documentazione deve comprendere la descrizione di **ogni package, classe,
interfaccia, metodo e attributo**. Il commento è sempre posto immediatamente prima
della dichiarazione. Può contenere tag HTML per la formattazione (`<strong>`,
`<em>`, ecc.).

Formato generale di un tag: `@nome commento`. Ogni tag deve stare su una riga
nuova. I commenti possono estendersi su più righe, ma non devono contenere righe
vuote.

**Package** — riportare la documentazione generale del package; usare `@see` per
riferirsi a classi, metodi e attributi in esso contenuti.

**Classi** — descrivere lo scopo della classe; al termine della descrizione, il tag
`@author Nome Cognome`. Se ci sono più autori, più tag su righe separate.

**Attributi** — descrivere a cosa serve l'attributo. Solo il primo paragrafo viene
riportato nella descrizione generale, gli altri nella descrizione puntuale. Un
nuovo paragrafo inizia con `<p>` a inizio riga. Racchiudere i nomi degli attributi
fra `<code>` e `</code>`.

**Metodi** — usare sempre, a inizio riga:

- `@param <nome parametro>` — breve descrizione del parametro; se più parametri,
  rispettare l'ordine di dichiarazione
- `@return` — breve descrizione di ciò che il metodo ritorna
- `@throws <nome eccezione>` — descrizione delle circostanze che determinano il
  sollevamento dell'eccezione

La documentazione si genera con il comando `javadoc`, incluso nel JDK.

---

## 6. Maven

Maven è un tool basato su Java: una versione di Java deve essere installata per
poterlo eseguire, e `JAVA_HOME` deve puntare all'installazione di Java.
Installazione: scaricare l'archivio da maven.apache.org e aggiungere la cartella
`/bin` alla variabile `PATH`.

La struttura standard di un progetto Maven prevede `pom.xml` nella radice e i
sorgenti sotto `src/main/java`, i test sotto `src/test/java`, seguendo la gerarchia
dei package.

Compilazione con `mvn package`: produce la compilazione e la creazione del jar in
`/target`. Formalmente `package` è una *phase* all'interno del *build lifecycle*,
cioè la sequenza di fasi per la gestione del progetto Maven. Quando si richiede una
phase, tutte le phase precedenti vengono eseguite: nel caso di `package`, prima di
creare il jar vengono eseguite verifica, pre-processing di sorgenti e risorse, e
compilazione.

---

## 7. Criteri di valutazione

**Applicazione sviluppata**

- Gestione architettura distribuita client/server e concorrenza
- Aderenza delle funzionalità implementate ai requisiti
- Controllo degli errori e gestione delle eccezioni
- Implementazione e gestione del DB
- Navigabilità della GUI
- Struttura e completezza della consegna

**Documentazione**

- Struttura e completezza dei manuali
- Linguaggio adatto per manuale tecnico e utente
- Manuale utente: descrizione delle funzionalità, con screenshot
- Manuale tecnico: scelte architetturali, algoritmiche e strutture dati
- Manuale tecnico: documentazione della progettazione software, con UML
- Manuale tecnico: documentazione della progettazione del DB, con ER
- Documentazione JavaDoc

---

## 8. Ordine di lavoro indicato dal docente

1. Progettazione della soluzione
2. Sviluppo della soluzione software
3. Documentazione di progetto — due documenti distinti: Manuale Utente e Manuale
   Tecnico

---

## Nota sulla fedeltà dell'estratto

Le slide sono state acquisite con OCR e in alcuni punti il testo è degradato.
Dove una sigla o un numero risultavano illeggibili si è preferito omettere
piuttosto che ricostruire: l'estratto non è garantito esaustivo al cento per
cento. Va confrontato una volta con i PDF originali, dopodiché resta congelato e
si aggiorna solo se il docente modifica le specifiche.
