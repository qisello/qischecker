package com.ello.qischeck.qischeck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import java.lang.ref.WeakReference;

public class QISCheckerService extends JobService {

    private ObscuredSharedPreferences mSharedPreferences;
    private Handler mJobHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mSharedPreferences = ObscuredSharedPreferences.getPrefs(this, MainActivity.APP_NAME, MODE_PRIVATE);
        doIt();
        return true;
    }

    private void doIt() {
        QISCheckerIntentService.startServiceToCheck(this, new InfoResultReceiver(this));
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mJobHandler.removeMessages(1);
        return false;
    }

    public ObscuredSharedPreferences getmSharedPreferences() {
        return mSharedPreferences;
    }

    private static class InfoResultReceiver implements QISResultReceiver.ResultReceiverCallBack<Integer> {
        private final QISCheckerService activity;

        private WeakReference<QISCheckerService> activityRef;

        InfoResultReceiver(QISCheckerService activity) {
            activityRef = new WeakReference<>(activity);
            this.activity = activity;
        }

        @Override
        public void onSuccess(Integer data) {
            if (activityRef != null && activityRef.get() != null) {

                if (activity.getmSharedPreferences().getInt(MainActivity.PrefKeys.ANZAHL.name(), 0) < data) {
                    int anzahl = data - activity.getmSharedPreferences().getInt(MainActivity.PrefKeys.ANZAHL.name(), 0);

                    Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(
                            Resources.getSystem(),
                            R.drawable.ic_notification);

                    Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
                    notificationIntent.setData(Uri.parse(activity.getResources().getString(R.string.QIS_URL)));
                    PendingIntent pi = PendingIntent.getActivity(activity, 0, notificationIntent, 0);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, "M_CH_ID")
                            .setSmallIcon(R.drawable.ic_school_black_24dp)
                            .setLargeIcon(notificationLargeIconBitmap)
                            .setContentTitle(MainActivity.APP_NAME)
                            .setAutoCancel(true)
                            .setContentIntent(pi)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setDefaults(Notification.DEFAULT_ALL);

                    String msg;
                    if (anzahl > 1) {
                        msg = activity.getResources().getString(R.string.NEW_GRADES);
                    } else {
                        msg = activity.getResources().getString(R.string.NEW_GRADE);
                    }

                    mBuilder.setContentText(msg)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg));

                    NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                    assert mNotificationManager != null;
                    mNotificationManager.notify(0, mBuilder.build());

                    activity.getmSharedPreferences().edit().putInt(MainActivity.PrefKeys.ANZAHL.name(), data).commit();
                }
            }
        }

        @Override
        public void onError(Exception exception) {
            //nothing
        }
    }
}