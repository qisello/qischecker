# qischecker

## Was macht die App?

Die App erlaubt das Prüfen des QIS-Systems der (momentan einzigen hinterlegten) Hochschule. Sobald neue Noten vorliegen, informiert die App darüber. Es werden keine Noten heruntergeladen oder innerhalb der App dargestellt.

## Was bietet die App?

* Die App erlaubt das Speichern der Zugangsdaten zum QIS-System. Die Daten werden verschlüsselt auf dem Smartphone gespeichert und nur zum Abfragen neuer Noten genutzt. Die Abfrage erfolgt mittels HTTPS-verschlüsselter Verbindung.

* Die App erlaubt es, einen Zeitraum zum Prüfen auf neue Noten anzugeben. Hierbei kann zwischen 15, 30, 60, 90 und 120 Minuten gewählt werden.

* Das Abfragen der Noten kann, wenn gewünscht, nur bei aktiver WLAN-Verbindung erfolgen

* Die App kann, sobald alle Noten vorliegen deaktiviert werden. Es werden dann keine Anfragen mehr an das QIS-System gestellt. 

## Wie installiere ich die App?

Zwei Möglichkeiten:

* Option 1: Die angebotene [QISChecker.apk](https://github.com/qisello/qischecker/raw/master/qischecker.apk) herunterladen, auf das Smartphone kopieren und dann über das Smartphone mit einem Dateimanager installieren.

* Option 2: Den Quellcode auschecken, in Android Studio importieren und über Android Studio auf das Smartphone deployen. 

## Wie benutze ich die App?

Nach der Installation rufst du die App einfach auf, aktivierst sie und gibst im Anschluss deinen Benutzernamen und das Passwort vom QIS-System an. Wähle dann den Zeitabstand, in dem auf neue Noten geprüft werden soll und entscheide, ob nur bei aktiver WLAN-Verbindung eine Prüfung stattfindet.

Bestätige deine Angaben mit der Schaltfläche **Speichern**.

**Wichtig:** Nach dem Speichern wird erstmalig das QIS-System nach Noten abgefragt. Du erhältst in jedem Fall eine Benachrichtigung über neue Noten. Dies ist notwendig, um die bereits vorhandene Notenanzahl einmalig festzustellen.

Wenn du die Benachrichtigung anklickst, öffnet dein Smartphone direkt den Browser und navigiert dich zur Login-Seite des QIS-Systems.

## Sonst noch was?

Es gilt, wie immer: Nutzung auf eigene Gefahr. Keine Gewährleistung oder Garantie. Wird bereitgestellt unter der Apache License 2.0.
Ich konnte die App nur mit den mir zur Verfügung stehenden Geräten (Android 7.1.1, 5.1.1) testen. Für andere Versionen kann ich keine Garantie geben, ob alles funktioniert.

## War das alles?

Erstmal schon. Vielleicht folgt noch was. 
