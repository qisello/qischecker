package com.ello.qischeck.qischeck;

import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    public enum PrefKeys {
        ONOFF,
        USERNAME,
        PASSWORD,
        QUERYFREQ,
        ONLYLWLAN,
        ANZAHL
    }

    /**
     * Name der App. Sehr kreativ.
     */
    public static final String APP_NAME = "QISCHECKER";

    /**
     * Secure Shared Preferences mit Android Keystore System
     * @link https://github.com/ophio/secure-preferences
     */
    private ObscuredSharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = ObscuredSharedPreferences.getPrefs(this, APP_NAME, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Zustand der App. Aktiviert oder deaktiviert
        ToggleButton onoff = findViewById(R.id.onoff);
        onoff.setChecked(mSharedPreferences.getBoolean(PrefKeys.ONOFF.name(), false));

        //Änderungen des Zustandes dauerhaft überwachen
        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkOnOff();
            }
        });

        //Benutzernamen eintragen
        TextView username = findViewById(R.id.username);
        username.setText(mSharedPreferences.getString(PrefKeys.USERNAME.name(), ""));

        //Passwort eintragen
        TextView password = findViewById(R.id.password);
        password.setText(mSharedPreferences.getString(PrefKeys.PASSWORD.name(), ""));

        //Auswahlhäufigkeit eintragen
        int querySel;
        switch (mSharedPreferences.getInt(PrefKeys.QUERYFREQ.name(), 120)) {
            default:
            case 30:
                querySel = 0;
                break;
            case 60:
                querySel = 1;
                break;
            case 90:
                querySel = 2;
                break;
            case 120:
                querySel = 3;
                break;
            case 15:
                querySel = 4;
                break;
        }
        Spinner queryFreq = findViewById(R.id.queryFreq);
        queryFreq.setSelection(querySel);

        //Nur bei WLAN-Option eintragen
        CheckBox onlyWlan = findViewById(R.id.onlyWlan);
        onlyWlan.setChecked(mSharedPreferences.getBoolean(PrefKeys.ONLYLWLAN.name(), false));

        //Zustand der App prüfen und Eingaben danach ausrichten
        //App deaktiviert: Eingaben deaktiviert
        //App aktiviert: Eingaben aktiviert
        checkOnOff();

    }

    /**
     * Methode, die den Zustand der App prüft und direkt persistiert.
     * App deaktiviert: Eingaben deaktiviert
     * App aktiviert: Eingaben aktiviert
     */
    private void checkOnOff() {
        ToggleButton onoff = findViewById(R.id.onoff);

        findViewById(R.id.username).setEnabled(onoff.isChecked());
        findViewById(R.id.password).setEnabled(onoff.isChecked());
        findViewById(R.id.queryFreq).setEnabled(onoff.isChecked());
        findViewById(R.id.onlyWlan).setEnabled(onoff.isChecked());
        findViewById(R.id.save).setEnabled(onoff.isChecked());

        //Zustand der App speichern
        mSharedPreferences.edit().putBoolean(PrefKeys.ONOFF.name(), onoff.isChecked()).commit();

        //Alle Hintergrundaufgaben der App löschen oder neu anlegen
        if (!onoff.isChecked()) {
            cancelAllJobs();
        } else {
            createSchedulerTask();
        }
    }

    /**
     * Methode, die beim Speichern die Daten verschlüsselt persistiert
     *
     * @param v View
     * @throws NoSuchAlgorithmException nicht unterstütztes Verfahren
     */
    public void onClickBtn(View v) throws NoSuchAlgorithmException {
        int time;

        //Alle bisherigen Jobs löschen
        cancelAllJobs();

        //Benutzereingaben erfassen
        final TextView username = findViewById(R.id.username);
        final TextView password = findViewById(R.id.password);
        final Spinner queryFreq = findViewById(R.id.queryFreq);
        final CheckBox onlyWlan = findViewById(R.id.onlyWlan);
        switch (queryFreq.getSelectedItemPosition()) {
            default:
            case 0:
                time = 30;
                break;
            case 1:
                time = 60;
                break;
            case 2:
                time = 90;
                break;
            case 3:
                time = 120;
                break;
            case 4:
                time = 15;
                break;
        }
        boolean onlyWlanChecked = onlyWlan.isChecked();

        //Eingaben verschlüsselt persistieren
        ObscuredSharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PrefKeys.USERNAME.name(), username.getText().toString());
        editor.putString(PrefKeys.PASSWORD.name(), password.getText().toString());
        editor.putInt(PrefKeys.QUERYFREQ.name(), time);
        editor.putBoolean(PrefKeys.ONLYLWLAN.name(), onlyWlanChecked);

        //Anzahl der erkannten Noten initial auf 0 setzen
        if (!mSharedPreferences.contains(PrefKeys.ANZAHL.name())) {
            editor.putInt(PrefKeys.ANZAHL.name(), 0);
        }

        editor.commit();

        createSchedulerTask();
    }

    /**
     * Methode zum Anlegen von Hintergrundaufgaben der App
     */
    private void createSchedulerTask() {
        //Hintergrundaufgaben einrichten
        JobInfo.Builder builder = new JobInfo.Builder( 1,
                new ComponentName( getPackageName(), QISCheckerService.class.getName() ) );

        builder.setPeriodic(mSharedPreferences.getInt(PrefKeys.QUERYFREQ.name(), 120) * 60 * 1000);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiredNetworkType(mSharedPreferences.getBoolean(PrefKeys.ONLYLWLAN.name(), false) ? JobInfo.NETWORK_TYPE_UNMETERED : JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert jobScheduler != null;
        jobScheduler.schedule(builder.build());
    }

    /**
     * Beendet alle Hintergrundaufgaben der App
     */
    public void cancelAllJobs() {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert tm != null;
        tm.cancelAll();
    }


    public void onClickLic(View view) {
        displayLicensesAlertDialog("file:///android_asset/license.html");
    }

    private void displayLicensesAlertDialog(String url) {
        WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.webview, null);
        view.loadUrl(url);
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(getString(R.string.LICENSE_TITLE))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }


}