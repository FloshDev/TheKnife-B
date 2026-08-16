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

Script SQL in Progettazione/1_Doc. Conclusi/Database/, da eseguire in quest'ordine
su PostgreSQL con un utente amministrativo (es. postgres):

    1. Role.sql     — crea il ruolo applicativo tk_app e il database dbTK
    2. Schema.sql    — crea le tabelle
    3. Data.sql      — popola RistorantiTheKnife con il dataset del docente

Esempio con psql:

    psql -U postgres -f Role.sql
    psql -U postgres -d dbTK -f Schema.sql
    psql -U postgres -d dbTK -f Data.sql


ESECUZIONE

1) Avviare il server:

    java -jar serverTK.jar [porta]

   Chiede da terminale host, utente e password del database. La porta di ascolto
   è un argomento opzionale della riga di comando; se omessa vale il default
   condiviso col client (9999).

2) Avviare uno o più client, anche da macchine diverse sulla stessa rete:

    java -jar clientTK.jar [host] [porta]

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
