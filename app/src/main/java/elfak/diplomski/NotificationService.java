package elfak.diplomski;

import android.app.Activity;
import android.app.Application;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import javax.annotation.Nullable;

import elfak.diplomski.Realm.RealmController;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.RealmObject;
import elfak.diplomski.Model.OrderPlaced;
import io.realm.ObjectChangeSet;
import io.realm.RealmObjectChangeListener;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

import static elfak.diplomski.LoginActivity.TAG;


public class NotificationService extends IntentService {

    public volatile Realm realm;
    public static Boolean serviceRunning = false;
    //private RealmObjectChangeListener<OrderPlaced> orderListener = null;
    //private OrderedRealmCollectionChangeListener<RealmResults<OrderPlaced>> ordersChangeListener = null;
    private boolean adminFlag = false;
    private String username = "";
    private RealmResults<OrderPlaced> orderPlacedsListener = null;
    Application application;
    Thread thread;

    public NotificationService() {
        super("NotificationService");
        //setIntentRedelivery(true);
    }

    /*public NotificationService(String name) {
        super(name);
    }*/

    /*public NotificationService() {
    }*/

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        /*myUsername = intent.getStringExtra("myUsername");
        friendsUsernames = intent.getStringExtra("friendsUsernames");*/

        //final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
        //Realm.setDefaultConfiguration(syncConfiguration);
        //realm = Realm.getDefaultInstance();

        //realm = MainActivity.realm;

        /*try {
            //realm = Realm.getInstance(getApplicationContext());
            //Realm.init(getApplicationContext());
            Realm.init(this);
            //realm = Realm.getDefaultInstance();

           final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

        adminFlag = MainActivity.adminFlag;
        username = MainActivity.username;
        //username = intent.getStringExtra("username");

        if (adminFlag) {
            //Toast.makeText(getApplicationContext(), "Admin flag true", Toast.LENGTH_SHORT).show();
        } else {
            setOrdersChangeListener();
            //Toast.makeText(getApplicationContext(), "Admin flag false", Toast.LENGTH_SHORT).show();
        }*/


        /*adminFlag = MainActivity.adminFlag;
        username = MainActivity.username;

        Runnable r = new Runnable()
        {
            public void run()
            {
                try {
                    //realm = Realm.getInstance(getApplicationContext());
                    //Realm.init(getApplicationContext());
                    //Realm.init(this);
                    //realm = Realm.getDefaultInstance();

                    final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
                    Realm.setDefaultConfiguration(syncConfiguration);
                    Realm realm = Realm.getDefaultInstance();


                    final RealmResults<OrderPlaced> orderPlacedsListener = RealmController.getMyNotCompletedOrders(realm, getApplicationContext(), username);

                    if (orderPlacedsListener == null || orderPlacedsListener.size() == 0) {
                        return;
                    }

                    orderPlacedsListener.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<OrderPlaced>>() {
                        @Override
                        public void onChange(RealmResults<OrderPlaced> orderPlaceds, @Nullable OrderedCollectionChangeSet changeSet) {
                            if (changeSet == null)
                                return;

                            //Toast.makeText(getApplicationContext(), "CHANGE", Toast.LENGTH_SHORT).show();

                            OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                            for (int i = 0; i < modifications.length; i++) {
                                showNotification("Order: " + orderPlaceds.get(i).getDateAndTime(), "Status: " + orderPlaceds.get(i).getStatus(), R.mipmap.ic_launcher_icon);
                            }

                            boolean checkStatusOrders = true;
                            for (int i = 0; i < orderPlaceds.size(); i++) {
                                if (!orderPlaceds.get(i).getStatus().equals("Rejected") || !orderPlaceds.get(i).getStatus().equals("Completed")) {
                                    checkStatusOrders = false;
                                }
                            }

                            if (checkStatusOrders) {
                                //orderPlacedsListener.removeAllChangeListeners();
                            }

                        }
                    });
                } catch (Exception ex) {
                    //Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                }

                //username = intent.getStringExtra("username");

            }
        };

        //Thread t = new Thread(r);
        thread = new Thread(r);
        thread.start();*/


        /*final int currentId = startId;
        Runnable r = new Runnable() {
            public void run() {

                for (int i = 0; i < 5; i++)
                {
                    long endTime = System.currentTimeMillis() + 10*1000;

                    while (System.currentTimeMillis() < endTime) {
                        synchronized (this) {
                            try {
                                wait(endTime -
                                        System.currentTimeMillis());
                            } catch (Exception e) {
                            }
                        }
                    }
                    Log.i(TAG, "Service running " + currentId);
                }
                stopSelf();
            }
        };

        Thread t = new Thread(r);
        t.start();*/

        serviceRunning = true;

        //return START_STICKY;
        //return START_REDELIVER_INTENT;
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@android.support.annotation.Nullable Intent intent) {
        /*realm = null;
        try {
            realm  = Realm.getDefaultInstance();
            setOrdersChangeListener();
            Toast.makeText(getApplicationContext(), "Setovan", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }*/

        Toast.makeText(getApplicationContext(), "Setovan", Toast.LENGTH_SHORT).show();
        try {
            //realm = Realm.getInstance(getApplicationContext());
            //Realm.init(getApplicationContext());
            Realm.init(this);
            realm = Realm.getDefaultInstance();

            /*final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();*/

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

        adminFlag = MainActivity.adminFlag;
        username = MainActivity.username;
        //username = intent.getStringExtra("username");

        if (adminFlag) {
            //Toast.makeText(getApplicationContext(), "Admin flag true", Toast.LENGTH_SHORT).show();
        } else {
            setOrdersChangeListener();
            //Toast.makeText(getApplicationContext(), "Admin flag false", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

        @Override
    public void onDestroy()
    {
        serviceRunning = false;

       /* if (thread.isAlive()) {
            thread.stop();
        }*/

        /*if(realm != null && !realm.isClosed()) {
            realm.removeAllChangeListeners();
            realm.close();
        }*/

        super.onDestroy();

    }

    public void showNotification(String title, String text, int icon)
    {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(icon) // notification icon
                    .setContentTitle(title) // title for notification
                    .setContentText(text) // message for notification
                    .setAutoCancel(true); // clear notification after click

            mBuilder.setDefaults(Notification.DEFAULT_ALL);


            Intent intent = new Intent(this, LoginActivity.class);
            //intent.putExtra("username", myUsername);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pi);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
    }

    /*private void orderStatusChangeListener() {

       orderListener = new RealmObjectChangeListener<OrderPlaced>() {
            @Override
            public void onChange(OrderPlaced orderPlaced, ObjectChangeSet changeSet) {
                if (changeSet.isFieldChanged("status")) {
                    //Log.i(TAG, "The dog was deleted");
                    Toast.makeText(getApplicationContext(), "Changed status", Toast.LENGTH_SHORT).show();
                    showNotification("Order: " + orderPlaced.getDateAndTime(), "Status: " + orderPlaced.getStatus(), R.mipmap.ic_launcher_round);
                    return;
                }

                /*for (String fieldName : changeSet.getChangedFields()) {
                    //Log.i(TAG, "Field " + fieldName + " was changed.");
                }*/
        /*    }
        };

    }*/

    private void setOrdersChangeListener() {

        try {


            orderPlacedsListener = RealmController.getMyNotCompletedOrders(realm, getApplicationContext(), username);

            if (orderPlacedsListener == null || orderPlacedsListener.size() == 0) {
                return;
            }

            orderPlacedsListener.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<OrderPlaced>>() {
                @Override
                public void onChange(RealmResults<OrderPlaced> orderPlaceds, @Nullable OrderedCollectionChangeSet changeSet) {
                    if (changeSet == null)
                        return;

                    //Toast.makeText(getApplicationContext(), "CHANGE", Toast.LENGTH_SHORT).show();

                    OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                    for (int i = 0; i < modifications.length; i++) {
                        showNotification("Order: " + orderPlaceds.get(i).getDateAndTime(), "Status: " + orderPlaceds.get(i).getStatus(), R.mipmap.ic_launcher_icon);
                    }

                    boolean checkStatusOrders = true;
                    for (int i = 0; i < orderPlaceds.size(); i++) {
                        if (!orderPlaceds.get(i).getStatus().equals("Rejected") || !orderPlaceds.get(i).getStatus().equals("Completed")) {
                            checkStatusOrders = false;
                        }
                    }

                    if (checkStatusOrders) {
                        orderPlacedsListener.removeAllChangeListeners();
                    }

                }
            });
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

}
