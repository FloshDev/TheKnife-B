# CLAUDE.md — TheKnife

Sistema client-server per ricerca e recensione ristoranti (stile TheFork).
Progetto del corso **Laboratorio Interdisciplinare B**. Team: Barlera, Ciani (PM),
Gasparini, Scolaro.

## Fonte non negoziabile

`DIRETTIVE_PROGETTO.md` (radice) è l'estratto dei vincoli del docente.
**Leggilo a inizio di ogni sessione.** Se una richiesta o una decisione lo
contraddice, **fermati e segnala il conflitto**: vince sempre il documento del
docente, non si aggira.

## Stack tecnologico (fissato)

- Java 21 Temurin, multipiattaforma
- PostgreSQL 18, accesso via JDBC (nessun ORM)
- Maven multi-modulo: `theknife-common`, `theknife-server`, `theknife-client`
- GUI client: JavaFX
- Trasporto: socket TCP + serializzazione Java
- Protocollo: pattern Request/Response con enum `CommandType`
- **Il client non accede mai al database.** Tutto passa dal server.

## Decisioni architetturali chiuse

Elenco secco. Motivazioni per esteso in
`Progettazione/Sessioni/Sessione 28_07_2026.md`. I verbali di sessione stanno in
`Progettazione/Sessioni/` (il più recente indica i task correnti per ogni membro).

- **I1** — niente `common.model`: una sola classe per entità, il DTO (nasce nel
  DAO dal ResultSet, viaggia sul socket, arriva al client).
- **I2/I7** — layer dati = **DAO** (`server.dao`, classi `*DAO`), non Repository.
- **I3** — client a 4 layer: `app`, `service`, `network`, `ui`. La connessione sta
  in `ServerConnection` (`client.network`, Singleton + Facade); in `client.service`
  tre service: `AuthService`, `RistoranteService`, `RecensioneService`.
- **I4** — interfaccia del pattern Command = **`Command`** (+ `Dispatcher`), non
  `CommandHandler` né `ICommand`.
- **I5** — valori dell'enum `CommandType` in **italiano**.
- **I6** — campo messaggio della `Response` = **`messaggio`**, non `message`.

## Convenzione linguistica

- **Italiano** — identificatori di dominio: valori di `CommandType`, campi dei DTO,
  nomi delle entità.
- **Inglese** — identificatori tecnici e convenzioni Java: `Command`, `Dispatcher`,
  `execute`, `Request`, `Response`, getter/setter.

## Regole di lavoro sulla repo

- **File `.drawio`**: non modificarli via strumenti automatici (formato
  base64+deflate, le modifiche automatiche li corrompono). Si editano a mano in
  draw.io.
- **Diff minimo**: tocca solo ciò che il task richiede; non riformulare né
  riordinare parti non interessate.
- **Decisioni chiuse**: non riaprirle. Se emerge un problema, segnalalo come nota
  separata, non risolverlo di iniziativa.
- **Incoerenze impreviste**: fermati e segnala, non decidere al posto del team.
- **File mancanti**: niente supposizioni. Chiedi o segnala.
