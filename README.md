# TheKnife

Sistema **client-server** per la ricerca e la recensione di ristoranti (ispirato a
TheFork). Progetto del corso **Laboratorio Interdisciplinare B**.

Un server si interfaccia con un database PostgreSQL e fornisce i servizi di
back-end; uno o più client si collegano via socket per cercare ristoranti,
leggere e scrivere recensioni. Il client non accede mai direttamente al database:
tutto passa dal server.

## Stack tecnologico

- Java 21 (Temurin), multipiattaforma
- PostgreSQL, accesso via JDBC (nessun ORM)
- Maven multi-modulo
- GUI client: JavaFX
- Trasporto: socket TCP + serializzazione Java, protocollo Request/Response

## Struttura del repository

```
theknife-common/        # Modulo condiviso: protocollo (Request/Response,
                        #   CommandType), DTO, enum. Impacchettato nei due jar.
theknife-server/        # Modulo server → produce serverTK.jar
theknife-client/        # Modulo client → produce clientTK.jar
pom.xml                 # POM padre (Maven multi-modulo)
autori.txt              # Autori del progetto (nome, matricola, sede)
DIRETTIVE_PROGETTO.md   # Vincoli del docente (non negoziabili)
CLAUDE.md               # Contesto operativo e decisioni architetturali chiuse

Progettazione/
├── 1_Doc. Conclusi/    # Documenti definitivi e approvati dal gruppo
├── Barlera/            # Cartella di lavoro — Barlera
├── Ciani/              # Cartella di lavoro — Ciani
├── Gasparini/          # Cartella di lavoro — Gasparini
└── Scolaro/            # Cartella di lavoro — Scolaro

Sessioni/               # Verbali delle sessioni di lavoro (il più recente
                        #   indica i task correnti di ciascun membro)
```

> **Stato:** client completo (dodici schermate agganciate al server), server cablato per
> intero — tutti e venti i comandi del protocollo coperti, login end-to-end in
> collaudo. In corso: rifinitura grafica delle schermate, diagrammi UML, manuali di
> consegna. Dettaglio nel verbale più recente in `Sessioni/`.

## Build

Requisiti: JDK 21 e Maven.

```bash
# Compila tutti i moduli e produce i due jar in target/
mvn clean package
```

Output:
- `theknife-server/target/serverTK.jar`
- `theknife-client/target/clientTK.jar`

Entrambi i jar sono self-contained (includono `theknife-common` e le dipendenze).

## Esecuzione

Database: caricare in ordine `Role.sql`, `Schema.sql`, `Data.sql` (in
`Progettazione/Scolaro/Database/`) su PostgreSQL.

```bash
# Server: chiede host, utente e password del DB al prompt.
# La porta di ascolto è un argomento opzionale, default 9999 se omesso
java -jar theknife-server/target/serverTK.jar [porta]

# Client: host e porta del server sono argomenti opzionali,
# default localhost:9999 se omessi
java -jar theknife-client/target/clientTK.jar [host] [porta]
```

Porta di default e host di default condivisi da client e server in un solo punto:
`theknife.common.config.ConfigurazioneServer`.

## Come contribuire

Durante la progettazione ogni membro lavora **solo nella propria cartella** in
`Progettazione/`; un documento pronto e approvato dal gruppo si sposta in
`1_Doc. Conclusi/`. Per il codice ci si coordina sui moduli secondo i verbali in
`Sessioni/`.

### Regole

- Non modificare i file nelle cartelle di progettazione degli altri
- Non modificare `1_Doc. Conclusi/` senza accordo del gruppo
- Commit frequenti, messaggi in italiano vanno bene

## Team

| Cognome e nome       | Cartella di progettazione   |
|----------------------|-----------------------------|
| Barlera Marco        | `Progettazione/Barlera/`    |
| Ciani Flavio Angelo  | `Progettazione/Ciani/`      |
| Gasparini Lorenzo    | `Progettazione/Gasparini/`  |
| Scolaro Gabriele     | `Progettazione/Scolaro/`    |

## Licenza

[CC BY 4.0](LICENSE) — [Barlera](https://github.com/ZonatedCord), [Ciani](https://github.com/FloshDev), [Gasparini](https://github.com/Gaspa999), [Scolaro](https://github.com/GabriScola), 2026
