# OsmAnd Nightly Google integration

## Exakt build/workflow-kommando

När en APK-build ska köras är det exakta Gradle-kommandot:

```bash
./gradlew assembleNightlyFreeLegacyFatDebug --no-daemon --stacktrace --max-workers=2
```

Kommandot körs från:

```text
osmand-source/
```

Ingen build startas genom denna dokumentationsändring.

## Byggväg och tillvägagångssätt

Detta repository (`999nowa/b`) är integrationsprojektet. OsmAnd upstream ändras inte direkt. GitHub Actions hämtar de upstream-projekt som behövs till separata kataloger och låser dem till uttryckligen angivna commits.

Den fungerande byggprocessen är i huvudsak:

1. Checkout av `999nowa/b`.
2. Installera JDK 17 med Temurin.
3. Kontrollera Java, Python och tillgängligt minne.
4. Klona OsmAnd och checkouta den låsta OsmAnd-committen.
5. Klona och checkouta matchande `OsmAnd-resources`, `OsmAnd-build`, `OsmAnd-core`, `OsmAnd-core-legacy`, `OsmAnd-tools` och `OsmAnd-misc`.
6. Kontrollera workspace, katalogstruktur, obligatoriska filer och relativa sökvägar innan någon patch appliceras.
7. Kontrollera Python-patchskriptet med `py_compile` och kontrollera att gamla/hårdkodade sökvägar inte används.
8. Kontrollera att `osmand-source/gradlew` fungerar.
9. Kopiera integrationsfilerna från `b` till den riktiga OsmAnd-källstrukturen.
10. Kör Python-patchningen mot den faktiska OsmAnd-checkouten.
11. Kontrollera efter patchningen att Google-klasserna, inställningarna och sökintegrationen ligger på rätt platser och faktiskt är injicerade i OsmAnd-koden.
12. Kontrollera att den gamla Google Maps Android SDK-implementationen inte råkar finnas kvar i Google-integrationen.
13. Kör OsmAnd Java-testerna utan att använda `-x test` för att dölja fel.
14. Först när testerna är godkända körs Nightly APK-builden med det exakta kommandot ovan.
15. Kontrollera att en faktisk APK skapades och kopiera den till artifact-sökvägen.
16. Publicera APK:n som GitHub Actions-artifact.

### Varför ordningen är viktig

Tidigare byggförsök visade att workspace-strukturen måste kontrolleras före patchningen. Ett särskilt fel uppstod när pre-patch-auditen förväntade sig en Google-katalog som ännu inte skulle finnas. Den korrekta principen är därför att före patchningen kontrollera upstream-strukturen och integrationsfilerna i `b`, medan destinationsfilerna för Google-integrationen först ska verifieras efter patchningen.

På samma sätt skall Python-skriptens relativa sökvägar alltid utgå från den faktiska checkouten som de arbetar mot. Egna integrationsfiler får inte blandas ihop med upstream-filer.

### Den viktiga korrigeringen för preference-callbacken

Ett tidigare Nightly-build stoppade på en dubblerad:

```java
onPreferenceChange(Preference, Object)
```

i `GlobalSettingsFragment`. Integrationen korrigerades så att Google preference callback-injektionen är idempotent. Efter denna korrigering lyckades Nightly-builden.

## Låsta byggkomponenter

Workflowen låser för närvarande följande commits:

| Komponent | Commit |
|---|---|
| OsmAnd | `55c111894e88cb7049dd796cc16b3c65fca24693` |
| OsmAnd-resources | `b22e0dfa8b3dbb2bb1c7449062041461033a2739` |
| OsmAnd-build | `54356f2a6d1d07fe67510a25ec7912c0c63b277d` |
| OsmAnd-core | `b67c66154a4fdf09fc84614e3e283d677869329f` |
| OsmAnd-core-legacy | `caedfeac444670f74ea480ebc33083e8b65daf6e` |
| OsmAnd-tools | `ebbc3211805d8d66a80663f97e5da6f894ed26e5` |
| OsmAnd-misc | `fa7b6f3c9d4df0007a82f9f575eb1028b8acd52a` |

Byggmiljön använder JDK 17 och GitHub Actions kör på `ubuntu-latest`.

## Google-integrationen

API-nyckeln lagras inte i repositoryt. Appen skall låta användaren ange, ändra och ta bort sin egen nyckel lokalt.

Google Search är en separat opt-in-funktion. OsmAnds normala sökning är standard när Google Search är avstängt.

Google-resultat integreras i OsmAnds sökarkitektur i stället för att visas i ett separat sökgränssnitt.

## Aktuell status

Den senaste lyckade Nightly-körningen har visat att den integrerade koden kan kompileras och att APK-builden kan slutföras. Nästa verifieringssteg är installation/teknisk kontroll av den skapade APK:n och därefter funktionstest av Google-inställningarna, Google Search och satellitfunktionen.

Warnings från Java/Kotlin eller GitHub Actions är inte i sig buildfel. De skall bedömas separat från faktiska kompileringsfel.
