# //Másolat a Mobil- és webes szoftverek tárgyra készített házi feladatomról//

# Házi feladat specifikáció

## Mobil- és webes szoftverek
### 2023.okt.22
### ReelRecall
### Kertész Levente - (H8SWUY)
### kerteszlevi@gmail.com
### Laborvezető: Rigó Péter Attila

## Bemutatás

Életünk során nagyon sok filmet és sorozatot látunk, számtalan játékkal játszunk, legalább ennyi könyvet is olvasunk. Viszont ezek közül nem sok akad, amit érdemesnek találunk arra, hogy még egyszer elővegyünk és felelevenítsünk, vagy ajánljunk másoknak. Ahogy telnek az évek az emlékek halványulnak, és ezen tartalmak nagy része lassan a múlt feledésébe merül, így nem sok minden marad, ami a jövőben emlékeztetne minket rájuk. Arra, hogy miért is olyan fontosak a számunkra, miért képezik az életünk részét, amiért nem akarunk tőlük megválni.

Aki meglátja a mögöttes értéket ezekben a tartalmakban, annak biztosan hasznára válik ez az eszköz, ez a lehetőség, ami biztos és tartós megoldást jelent a fenti problémára.

A ReelRecall egy négy alkalmazás tartalmazó lánc egy kezdetleges változata, ami a megnézett filmek, sorozatok követésére alkalmas.

## Főbb funkciók

Az alkalmazásban lehetőség van filmek és sorozatok címének, borítójának, saját értékelésének, megtekintés dátumának és megjegyzések manuális feljegyzésére. A mentett tartalmakat listázhatjuk, és kereshetünk bennük. Sorozatok esetén követhetjük a megnézett részek és évadok számát. Watchlist hasonló kezelése és megjelenítése.
Új film vagy sorozat hozzáadásánál TMDB API használatával ajánl fel lehetőségeket, kéri le és menti a keresett film adatait.

lehetőség van a mentett tartalmak címének, megtekintés dátumának, megjelenésének, saját értékelésének, saját megjegyzésnek, valamint egyedi azonosítójának a tmdb adatbázisban(ha van ilyen), fájlba mentésére és importálására.

## Megjelenés
A kezdőképernyő egy lista a megtekintett filmekről, a személyes értékelés szerint rendezve. A képernyő alján egy Navigation Bar segítségével lehet váltani a mentett filmek, sorozatok, a watchlist és a beállítások között. A hozzáadás gombot megnyomva megjelenik egy keresőmező, és itt van lehetőség filmekre, sorozatokra keresni és hozzáadni. Saját filmet/sorozatot egy erre kijelölt gomb segítségével lehet hozzáadni.

## Választott technológiák:

- Hálózati kommunikáció
- RecyclerView
- Perzisztens adattárolás(Room)
- Fragmentek
