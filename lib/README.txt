TheKnife — lib/

Librerie esterne non standard usate per compilazione ed esecuzione (vedi anche
README.txt nella radice del progetto). Non sono necessarie per compilare o
eseguire il progetto tramite Maven (pom.xml risolve automaticamente tutte le
dipendenze da Maven Central), né per eseguire i due jar in bin/ (self-contained,
già impacchettano tutte le dipendenze via maven-shade-plugin). Presenti qui solo
a scopo di ispezione, come richiesto dalla consegna.

Solo lato server (theknife-server):

- postgresql-42.7.3.jar — driver JDBC per PostgreSQL
- bcrypt-0.10.2.jar (+ bytes-1.5.0.jar, checker-qual-3.42.0.jar, dipendenze
  transitive) — hashing delle password (BCrypt, costo 12)

Solo lato client (theknife-client):

- javafx-base / javafx-graphics / javafx-controls / javafx-fxml, versione
  21.0.2 — GUI, non incluse nel JDK a partire da Java 11

I quattro jar di JavaFX qui presenti sono la build nativa per macOS Apple
Silicon (classifier mac-aarch64), la piattaforma della macchina usata per lo
sviluppo. JavaFX distribuisce artefatti nativi separati per piattaforma
(Windows, Linux, macOS Intel/ARM): quando si compila con Maven su un'altra
piattaforma, viene scaricato automaticamente il jar nativo corretto per quella
piattaforma, non serve intervenire a mano su questa cartella.
