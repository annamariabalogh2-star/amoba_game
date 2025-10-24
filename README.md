🎮 Amőba játék (Java, Maven)

Ez a kis projekt a **Programozási technológiák** tárgy beadandója.  
Egy egyszerű, parancssoros **Amőba (Gomoku)** játékot készítettem Java nyelven,  
ahol az emberi játékos az `X`, a gép pedig az `O` jelet használja.  

A játékban az `X` mindig középen kezd,  
utána felváltva lép az ember és a gép,  
és az nyer, akinek először sikerül **5 egymás melletti jelet** kiraknia  
vízszintesen, függőlegesen vagy átlósan.

⚙️ Főbb funkciók
- Parancssoros felület  
- Tábla mentése és betöltése (`board.txt`)  
- **XML mentés és betöltés** (`board.xml`)  
- High score mentése SQLite adatbázisba  
- Egyszerű gépi logika (AI – véletlenszerű legális lépés)

🧪 Tesztelés és kódlefedettség
A projekt **JUnit 5** segítségével tesztelhető.  
A kódlefedettséget a **JaCoCo plugin** méri.  
A lefedettség a végső állapotban kb. **78–80%**, ami lefedi az összes fő logikai ágat.

▶️ Futtatás
A projekt Maven alapú, így terminálból indítható:

- mvn clean install
- java -jar target/amoba_game-1.0-SNAPSHOT.jar
