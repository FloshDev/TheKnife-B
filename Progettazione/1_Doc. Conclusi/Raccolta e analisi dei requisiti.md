# Raccolta e analisi dei requisiti

## Introduzione

**The Knife** è un'applicazione sviluppata come progetto di laboratorio del corso di laurea in Informatica presso l'Università degli Studi dell'Insubria. L'applicazione, client-server, rappresenta un sistema solido e completo per la gestione di ristoranti, utenti, recensioni e ogni aspetto che li pone in relazione.

## Attori

Il sistema prevede tre attori distinti, di cui due autenticati, che possono accedere in concorrenza all'applicazione.

### Ospite

L'utente non autenticato accede come **Ospite** e può ricercare ristoranti attraverso geolocalizzazione o filtri e visualizzarne le recensioni.

### Cliente

L'utente autenticato come **Cliente** ha, in aggiunta alle funzionalità dell'utente Ospite, la possibilità di recensire ristoranti, e di conseguenza modificare o eliminare tali recensioni, e aggiungere ristoranti a una lista preferiti. Inoltre ha la funzionalità "Vicino a me", che gli consente di visualizzare i ristoranti vicini.

### Ristoratore

L'utente autenticato come **Ristoratore** ha la possibilità di inserire nel sistema 
nuovi ristoranti, prenderne in gestione di esistenti, rispondere a recensioni dei 
rispettivi ristoranti e visualizzare il riepilogo delle recensioni ricevute sui 
propri ristoranti.

## Requisiti funzionali

| ID | DESCRIZIONE | ATTORE |
|---|---|---|
| RF-01 | Il sistema propone all'Ospite una località iniziale ottenuta tramite geolocalizzazione approssimativa da IP; l'Ospite può confermarla o modificarla prima della visualizzazione dei ristoranti. | Ospite |
| RF-02 | Il sistema consente all'Ospite di ricercare ristoranti per filtri (nome, tipologia di cucina, fascia di prezzo, ecc.). | Ospite |
| RF-03 | Il sistema consente all'Ospite di ricercare ristoranti per geolocalizzazione e raggio di distanza. | Ospite |
| RF-04 | Il sistema consente all'Ospite di visualizzare le recensioni di un ristorante, le relative risposte, la media delle stelle e il numero totale di recensioni. | Ospite |
| RF-05 | Il sistema consente al Cliente di registrarsi fornendo nome, cognome, username, email, password, domicilio, data nascita e ruolo. | Cliente |
| RF-06 | Il sistema consente al Cliente di autenticarsi tramite username e password. | Cliente |
| RF-07 | Il sistema consente al Cliente di ricercare ristoranti nelle vicinanze del proprio indirizzo registrato ("Vicino a me"). | Cliente |
| RF-08 | Il sistema consente al Cliente di aggiungere un ristorante alla propria lista preferiti. | Cliente |
| RF-09 | Il sistema consente al Cliente di rimuovere un ristorante dalla propria lista preferiti. | Cliente |
| RF-10 | Il sistema consente al Cliente di visualizzare la propria lista preferiti. | Cliente |
| RF-11 | Il sistema consente al Cliente di pubblicare una recensione su un ristorante. | Cliente |
| RF-12 | Il sistema consente al Cliente di modificare una propria recensione. | Cliente |
| RF-13 | Il sistema consente al Cliente di eliminare una propria recensione. | Cliente |
| RF-14 | Il sistema consente al Ristoratore di registrarsi fornendo nome, cognome, username, email, password, domicilio, data nascita e ruolo. | Ristoratore |
| RF-15 | Il sistema consente al Ristoratore di autenticarsi tramite username e password. | Ristoratore |
| RF-16 | Il sistema consente al Ristoratore di inserire un nuovo ristorante nel sistema. | Ristoratore |
| RF-17 | Il sistema consente al Ristoratore di associarsi come gestore di un ristorante esistente non ancora gestito. | Ristoratore |
| RF-18 | Il sistema consente al Ristoratore di rispondere a una recensione di un proprio ristorante. | Ristoratore |
| RF-19 | Il sistema consente al Ristoratore di visualizzare la lista dei propri ristoranti. | Ristoratore |
| RF-20 | Il sistema consente al Ristoratore di visualizzare la lista delle recensioni ai propri ristoranti. | Ristoratore |
| RF-21 | Il sistema consente agli utenti autenticati di disconnettersi dal sistema. | Cliente, Ristoratore |
| RF-22 | Il sistema consente al Ristoratore di visualizzare, per ciascun proprio ristorante, il riepilogo delle recensioni ricevute: media delle stelle e numero totale di recensioni. | Ristoratore |
## Requisiti non funzionali

| ID | DESCRIZIONE | CATEGORIA |
|---|---|---|
| RNF-01 | Le password degli utenti devono essere memorizzate in forma cifrata tramite hash BCrypt (fattore di costo 12). | Sicurezza |
| RNF-02 | Il sistema deve supportare connessioni concorrenti di più client simultaneamente. | Prestazioni |
| RNF-03 | Il sistema deve essere multipiattaforma. | Portabilità |
| RNF-04 | Il progetto deve essere sviluppato in Java 21 (Temurin). | Vincolo tecnologico |
| RNF-05 | La persistenza dei dati deve essere gestita esclusivamente tramite PostgreSQL e JDBC. | Vincolo tecnologico |
| RNF-06 | Il progetto deve essere compilabile e gestibile tramite Maven. | Vincolo tecnologico |
| RNF-07 | Il sistema deve essere distribuito come due JAR separati: serverTK e clientTK. | Vincolo tecnologico |
| RNF-08 | Il codice sorgente deve essere documentato in formato JavaDoc. | Documentazione |
| RNF-09 | Il sistema deve utilizzare un servizio di geocoding (Nominatim/OpenStreetMap) per convertire indirizzi in coordinate geografiche, e ip-api.com per la stima della localita' iniziale da indirizzo IP. | Vincolo tecnologico |
| RNF-10 | L'applicazione deve essere dotata di un'interfaccia grafica desktop realizzata con JavaFX. | Vincolo tecnologico |