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

* Option 1: Die angebotene [QISChecker.apk](https://github.com/qisello/qischecker/raw/master/qischecker.apk) herunterladen, auf das Handy kopieren und dann über das Handy mit einem Dateimanager installieren.

* Option 2: Den Quellcode auschecken, in Android Studio importieren und über Android Studio auf das Smartphone deployen. 

## Sonst noch was?

Es gilt, wie immer: Nutzung auf eigene Gefahr. Keine Gewährleistung oder Garantie. Wird bereitgestellt unter der Apache License 2.0.
Ich konnte die App nur mit den mir zur Verfügung stehenden Geräten (Android 7.1.1, 5.1.1) testen. Für andere Versionen kann ich keine Garantie geben, ob alles funktioniert.

## War das alles?

Erstmal schon. Vielleicht folgt noch was. 
