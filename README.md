# OsmAnd Nightly Google integration

## Exakt build/workflow-kommando

Ingen build eller GitHub Actions/workflow startas som en del av denna ändring.

## Senaste ändring

### #1 | 2026-08-26

**Vad ändrades:** Materialet i `inympningar/` har flyttats till sina avsedda projektplatser. De tre Google Maps pluginfilerna har placerats under `OsmAnd/src/net/osmand/plus/plugins/googlemaps/`. De tre relevanta upstreamreferenserna har placerats under `build/` och `core-legacy/`. De två Googlefiler som redan fanns i `OsmAnd/src/net/osmand/plus/googlemaps/` behölls i sin nuvarande version i stället för att ersättas med äldre stagingkopior.

**Varför:** `inympningar/` var endast ett tillfälligt stagingområde. Målet är att ha en enda aktiv fil för varje funktion och att inte behålla dubbla filer med samma innehåll eller syfte.

**Hur det påverkar projektet:** Den aktiva källstrukturen innehåller nu de relevanta filerna på sina riktiga platser och stagingområdet `inympningar/` är borttaget. Befintliga nyare implementationer i `b` har inte skrivits över av äldre kopior. Ingen build eller GitHub Actions/workflow har startats.

Detta repository är det huvudsakliga utvecklingsrepositoriet för den anpassade OsmAndintegrationen.

## Integration

Projektet innehåller integration för Google Maps API, Google adressökning, sökproviderinställningar och Google kartrelaterad funktionalitet. Ingen Google API-nyckel lagras i repositoriet.
