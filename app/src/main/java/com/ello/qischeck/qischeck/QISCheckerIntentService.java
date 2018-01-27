package com.ello.qischeck.qischeck;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class QISCheckerIntentService extends IntentService {

    protected int TIME_OUT = 36 * 1000;
    private ObscuredSharedPreferences mSharedPreferences;

    private enum PARAM {
        RESULT_RECEIVER
    }

    public QISCheckerIntentService() {
        super(MainActivity.APP_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = ObscuredSharedPreferences.getPrefs(this, MainActivity.APP_NAME, Context.MODE_PRIVATE);
    }

    public static void startServiceToCheck(Context context, QISResultReceiver.ResultReceiverCallBack resultReceiverCallBack){
        QISResultReceiver qisResultReceiver = new QISResultReceiver(new Handler(context.getMainLooper()));
        qisResultReceiver.setReceiver(resultReceiverCallBack);

        Intent intent = new Intent(context, QISCheckerIntentService.class);
        intent.putExtra(PARAM.RESULT_RECEIVER.name(), qisResultReceiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver resultReceiver = intent.getParcelableExtra(PARAM.RESULT_RECEIVER.name());
        try {
            String QIS_USERNAME = "asdf";
            String QIS_PASSWORD = "fdsa";
            String QIS_SUBMIT = "submit";

            //Login durchführen. Cookie setzen.
            Connection.Response document = Jsoup.connect(getResources().getString(R.string.QIS_URL))
                    .data(QIS_USERNAME, mSharedPreferences.getString(MainActivity.PrefKeys.USERNAME.name(), ""))
                    .data(QIS_PASSWORD, mSharedPreferences.getString(MainActivity.PrefKeys.PASSWORD.name(), ""))
                    .data(QIS_SUBMIT, QIS_SUBMIT)
                    .method(Connection.Method.POST)
                    .timeout(TIME_OUT)
                    .execute();

            //Erste Seite vom QIS nach Login, um den Link zur Prüfungsverwaltung zu erhalten
            Document qisFirstPage = document.parse();
            String pruefungsVerwaltung = qisFirstPage.select("a.auflistung").first().attr("href");

            //Aufruf der Prüfungsverwaltung
            Document qisSecondPage = Jsoup.connect(pruefungsVerwaltung)
                    .cookies(document.cookies())
                    .timeout(TIME_OUT)
                    .get();

            //Zweite Seite vom QIS, um den Link zum Notenspiegel zu erhalten
            String notenSpiegel = qisSecondPage.select("a.auflistung").last().attr("href");

            //Aufruf des Notenspiegels
            Document qisThirdPageA = Jsoup.connect(notenSpiegel)
                    .cookies(document.cookies())
                    .timeout(TIME_OUT)
                    .get();

            //Dritte Seite (1) vom QIS, um den Link des Abschluss zu erhalten
            String abschluss = qisThirdPageA.select("form ul li > a.regular").first().attr("href");

            //Aufruf der Abschluss-Unterseite
            Document qisThirdPageB = Jsoup.connect(abschluss)
                    .cookies(document.cookies())
                    .timeout(TIME_OUT)
                    .get();

            //Dritte Seite (2) vom QIS, um den Link der gültigen PO zu erhalten
            String info = qisThirdPageB.select("form ul li ul li > a").first().attr("href");

            //Vierte Seite, auf der die Noten aufgeführt werden
            Document qisFourthPage = Jsoup.connect(info)
                    .cookies(document.cookies())
                    .timeout(TIME_OUT)
                    .get();

            //Selektion der Notentabelle
            Element table = qisFourthPage.select("table").last();

            //Zählen der Tabelleneinträge
            int notenAnzahl = table.select("tr").size();
            handleRetreiveBalance(resultReceiver, notenAnzahl);

        } catch (Exception e) {
            handleRetreiveBalance(resultReceiver, 0);
        }
    }

    private void handleRetreiveBalance(ResultReceiver resultReceiver, int anzahl) {
        Bundle bundle = new Bundle();
        int code = QISResultReceiver.RESULT_CODE_OK;

        bundle.putSerializable(QISResultReceiver.PARAM_RESULT, anzahl);
        if(resultReceiver != null){
            resultReceiver.send(code, bundle);
        }
    }
}