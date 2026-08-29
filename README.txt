TheKnife — README

Sistema client-server per la ricerca e la recensione di ristoranti.
Progetto del corso Laboratorio Interdisciplinare B.


REQUISITI

- JDK 21 (Temurin o equivalente)
- Apache Maven
- PostgreSQL, in ascolto e raggiungibile dalla macchina che esegue il server


COMPILAZIONE

Dalla radice del progetto:

    mvn clean package

Compila i tre moduli (theknife-common, theknife-server, theknife-client) e produce
due jar eseguibili e self-contained (includono theknife-common e tutte le
dipendenze):

    theknife-server/target/serverTK.jar
    theknife-client/target/clientTK.jar

Solo il pom.xml del modulo padre e dei tre moduli è necessario: nessun altro
strumento di build oltre a Maven.


CREAZIONE DEL DATABASE

Dalla radice del progetto, con Maven (sostituire postgres e miapassword con
utente e password di un amministratore PostgreSQL reali — NON copiare il
comando così com'è, "<" e ">" sono caratteri speciali della shell e la
fanno fallire):

    mvn initialize -Pdb-setup -Ddb.admin.user=postgres -Ddb.admin.password=miapassword

  Crea il ruolo applicativo tk_app, il database dbTK, le tabelle e carica il
  dataset del docente (script Role.sql, Schema.sql, Data.sql in
  Progettazione/Database/, eseguiti in quest'ordine).
  L'utente admin deve poter creare ruoli e database (es. l'utente "postgres"
  creato dall'installazione di PostgreSQL). Se PostgreSQL non è in ascolto su
  localhost:5432, aggiungere al comando -Ddb.host=NOMEHOST e/o
  -Ddb.port=NUMEROPORTA, con i valori reali al posto di NOMEHOST/NUMEROPORTA
  (es. -Ddb.host=192.168.1.10 -Ddb.port=5433).

  In alternativa, gli stessi tre script sono eseguibili a mano con psql, nello
  stesso ordine (Role.sql su un database di manutenzione come "postgres",
  Schema.sql e Data.sql sul database dbTK appena creato).

  Dati di prova (facoltativo): utenti, servizi e recensioni fittizie per
  provare subito l'applicazione senza registrarsi né inserire dati a mano
  (script DataUtentiTest.sql, AbilitaServiziTest.sql, PopolaRecensioniTest.sql
  nella stessa cartella). Vanno eseguiti DOPO db-setup, quando dbTK esiste
  già:

    mvn initialize -Pdb-test-data

  Non richiede argomenti: si connette con l'utente applicativo tk_app, già
  creato dal comando precedente. In alternativa, gli stessi tre script sono
  eseguibili a mano con psql, nello stesso ordine, sul database dbTK.

  Possibili errori:
  - "role X does not exist": il nome dell'utente admin varia a seconda
    dell'installazione di PostgreSQL. Sugli installer ufficiali (Windows,
    Linux, EDB per macOS) di solito si chiama "postgres"; con Homebrew su
    macOS invece il ruolo superuser creato di default prende il nome utente
    del sistema operativo, non "postgres". In caso di errore, verificare
    quale sia il proprio utente amministratore (es. con "psql -l" o "psql -c
    '\du'") e usare quel nome al posto di postgres nel comando.
  - "role tk_app already exists" / "database dbTK already exists": il
    comando è già stato eseguito in precedenza su questo PostgreSQL (ruolo
    e database già creati). Non serve rieseguirlo.


ESECUZIONE

Dalla radice del progetto, usando i jar già pronti in bin/ (oppure, se si è
appena compilato, gli equivalenti in theknife-server/target/ e
theknife-client/target/):

1) Avviare il server:

    java -jar bin/serverTK.jar [porta]

   Chiede da terminale host, utente e password del database (quelle
   dell'utente applicativo tk_app creato in "CREAZIONE DEL DATABASE", non
   quelle dell'admin PostgreSQL usate per crearlo):

       Host:     localhost
       Utente:   tk_app
       Password: TheKnife-B

   (host diverso da localhost solo se PostgreSQL non gira sulla stessa
   macchina del server). La porta di ascolto è un argomento opzionale della
   riga di comando; se omessa vale il default condiviso col client (9999).

2) Avviare uno o più client, anche da macchine diverse sulla stessa rete:

    java -jar bin/clientTK.jar [host] [porta]

   Host e porta del server sono argomenti opzionali; se omessi il client prova
   a connettersi a localhost:9999. Il default è definito in un solo punto
   (theknife.common.config.ConfigurazioneServer), condiviso da client e server.

Se il server non è raggiungibile, il client mostra un errore leggibile con
l'indirizzo tentato e la sintassi per riavviare con un host/porta diversi,
invece di uno stack trace.


LIBRERIE E SERVIZI ESTERNI NON STANDARD

Solo lato server (theknife-server), impacchettate nel jar shaded:

- org.postgresql:postgresql 42.7.3 — driver JDBC per PostgreSQL
- at.favre.lib:bcrypt 0.10.2 — hashing delle password (BCrypt, costo 12).
  Le password non sono mai salvate né trasmesse in chiaro dopo la registrazione

Solo lato client (theknife-client):

- org.openjfx:javafx-controls / javafx-fxml 21.0.2 — GUI, non incluse nel JDK
  a partire da Java 11

Servizi HTTP esterni (nessuna libreria di terze parti aggiuntiva, solo il
client HTTP del JDK; il parsing delle risposte JSON legge con espressioni
regolari i pochi campi necessari, senza un parser generale):

- Nominatim (OpenStreetMap, nominatim.openstreetmap.org) — geocoding di un
  indirizzo in coordinate e viceversa, usato per la ricerca "vicino a me" e
  per calcolare le coordinate di un ristorante appena inserito
- ip-api.com — stima approssimata della località da suggerire all'avvio del
  client (solo un suggerimento precompilato: l'utente può sempre digitare a
  mano la propria località, ed è il percorso che funziona sempre)

Entrambi i servizi sono opzionali per il funzionamento dell'applicazione: se
non rispondono (rete assente, timeout, servizio non raggiungibile), le
funzionalità che ne dipendono restano operative con un valore di fallback o
un messaggio leggibile, mai un errore bloccante.


AUTORI

Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
Barlera Marco, 760000, VA

(vedi anche autori.txt nella radice del progetto)
