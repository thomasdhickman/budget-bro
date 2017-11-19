package com.thickman.aurora.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.thickman.aurora.MainActivity;
import com.thickman.aurora.R;
import com.thickman.aurora.object.Transaction;
import com.thickman.aurora.realm.BudgetItemRealmController;
import com.thickman.aurora.realm.TransactionRealmController;
import com.thickman.aurora.util.TransactionUtils;

import io.realm.RealmResults;

public class TransactionSchedulerService extends Service {

    private final int INTERVAL =  10 * 1000; // One minute

    private Handler handler;

    public TransactionSchedulerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        postDelayed();
        return START_STICKY;
    }

    private void postDelayed() {
        handler.postDelayed(runnable, INTERVAL);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            postTodaysTransactions();
            postDelayed();
        }
    };

    private void postTodaysTransactions() {
        RealmResults<Transaction> todaysTransactions = TransactionRealmController.getInstance().getRecurringTransactions(true);
        for (int i = 0; i < todaysTransactions.size(); i++) {
            Transaction t = TransactionUtils.copy(todaysTransactions.get(i), (int) System.currentTimeMillis() + i);
            TransactionRealmController.getInstance().addTransaction(t);
        }
        showNotification(todaysTransactions.size());
    }

    private void showNotification(int amountPosted) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_menu_transactions)
                        .setContentTitle("Bill Bro Balance")
                        .setContentText(String.format(getMessageFormat(amountPosted), String.valueOf(amountPosted),
                                TransactionUtils.getAmountFormat(BudgetItemRealmController.getInstance().getToBeBudgetedAmount())));

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    private String getMessageFormat(int amountPosted) {
        if (amountPosted == 0) {
            return "Your account balance is %s";
        } else if (amountPosted == 1) {
            return "%s scheduled transaction was posted. Your account balance is %s";
        }
        return "%s scheduled transactions were posted. Your account balance is %s";
    }
}