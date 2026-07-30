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

> **Stato:** progettazione conclusa, scrittura del codice in corso. Lo scaffold
> Maven è pronto e compila; i moduli sono in fase di sviluppo.

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

## Come contribuire

Durante la progettazione ogni membro lavora **solo nella propria cartella** in
`Progettazione/`; un documento pronto e approvato dal gruppo si sposta in
`1_Doc. Conclusi/`. Per il codice ci si coordina sui moduli secondo i verbali in
`Sessioni/`.

### Workflow base

```bash
# Prima di iniziare: aggiorna
git pull

# Dopo aver modificato o aggiunto file
git add <percorso>
git commit -m "descrizione breve di cosa hai aggiunto/modificato"
git push
```

### Regole

- Non modificare i file nelle cartelle di progettazione degli altri
- Non modificare `1_Doc. Conclusi/` senza accordo del gruppo
- I file `.drawio` si editano a mano in draw.io (le modifiche automatiche li corrompono)
- Commit frequenti, messaggi in italiano vanno bene

## Team

| Cognome e nome       | Cartella di progettazione   |
|----------------------|-----------------------------|
| Barlera Marco        | `Progettazione/Barlera/`    |
| Ciani Flavio Angelo  | `Progettazione/Ciani/`      |
| Gasparini Lorenzo    | `Progettazione/Gasparini/`  |
| Scolaro Gabriele     | `Progettazione/Scolaro/`    |

## Licenza

[CC BY 4.0](LICENSE) — Barlera, Ciani, Gasparini, Scolaro, 2026
