package elfak.diplomski;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import javax.annotation.Nullable;

import elfak.diplomski.Adapter.CustomAdapter;
import elfak.diplomski.Model.MenuCategory;
import elfak.diplomski.Model.OrderPlaced;
import elfak.diplomski.Model.User;
import elfak.diplomski.Realm.RealmController;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GridView gridview;
    public static volatile Realm realm;
    public static OrderPlaced orderPlaced = new OrderPlaced();
    public static String username = "";
    public static boolean adminFlag = false;
    public static int categorySize = 0;
    public static final String PREFS_NAME = "preferences";
    private static final String PREF_UNAME_ADMIN = "isAdmin";
    private final String DefaultUnameValue = "";
    public static User user = null;
    private RealmResults<MenuCategory> menuCategories;
    NotificationService notificationService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY); // full layout under action bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadPreferences();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00212121")) );

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.nav_admin_on_off).setActionView(new Switch(this));
        // To set whether switch is on/off use:
        ((Switch) navigationView.getMenu().findItem(R.id.nav_admin_on_off).getActionView()).setChecked(adminFlag);
        ((Switch) navigationView.getMenu().findItem(R.id.nav_admin_on_off).getActionView()).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                adminFlag = b;
                savePreferences(String.valueOf(b));
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
        }

        if (user != null) {
            View hView = navigationView.getHeaderView(0);
            TextView textView1 = hView.findViewById(R.id.tv_nav_username);
            TextView textView2 = hView.findViewById(R.id.tv_nav_name);
            textView1.setText(user.getUsername());
            textView2.setText(user.getName() + " " + user.getLastname());
        } else {
            user =  RealmController.getUser(username, realm, this);

            if (user != null) { // One more try
                try {

                    /*
                    RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
                    Realm.setDefaultConfiguration(realmConfig);
                    */
                    final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
                    Realm.setDefaultConfiguration(syncConfiguration);
                    realm = Realm.getDefaultInstance();
                    //MainActivity.orderPlaced.setUser(userGet);
                    View hView = navigationView.getHeaderView(0);
                    TextView textView1 = hView.findViewById(R.id.tv_nav_username);
                    TextView textView2 = hView.findViewById(R.id.tv_nav_name);
                    textView1.setText(user.getUsername());
                    textView2.setText(user.getName() + " " + user.getLastname());
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        //orderPlaced.setUser(user);
       refreshGridView();

        user.addChangeListener(new RealmObjectChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel realmModel, @Nullable ObjectChangeSet changeSet) {
                if (changeSet.isDeleted()) {
                    //RealmController.deleteUser(realm, getApplicationContext(), username);
                    clearPreferences();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                    /*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);*/
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    }*/

                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(), "Your account was deleted!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        invalidateOptionsMenu();
        //***refreshGridView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        turnServiceOnOff();
        /*if(realm != null && !realm.isClosed() && !NotificationService.serviceRunning) {
            realm.close();
        }*/

    }

    @Override
    public void onPause() {
        super.onPause();

        checkCheckedFoodIsValid();
    }

    @Override
    public void finish() {
        super.finish();

        orderPlaced = null;
        orderPlaced = new OrderPlaced();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem brackets = menu.findItem(R.id.action_shop);
        MenuItem emptyBrackets = menu.findItem(R.id.action_empty_brackets);

        if (orderPlaced.getItems() != null && orderPlaced.getItems().size() > 0){
            brackets.setVisible(true);
            emptyBrackets.setVisible(false);
        } else {
            brackets.setVisible(false);
            emptyBrackets.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_shop) {
            //mRecyclerView.setAdapter(new ListAdapter(getApplicationContext(), mFoodList, iconList, relative_layout_recyclerView, linear_layout_showFullItem));

            if (orderPlaced.getItems() != null && orderPlaced.getItems().size() > 0) {

                /*orderPlaced_layout.setVisibility(View.VISIBLE);
                menuCategory_layout.setVisibility(View.INVISIBLE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(linearLayoutManager);

                RealmList<Integer> list = new RealmList<>();
                for (int i = 0; i < orderPlaced.getItems().size(); i++) {
                    list.add(1);
                }
                orderPlaced.setQuantitys(list);

                mRecyclerView.setAdapter(new ListAdapterOrderPlaced(getApplicationContext(), orderPlaced));*/

                //checkCheckedFoodIsValid();

                Intent intent = new Intent(MainActivity.this, OrderingActivity.class);
                //intent.putExtra("username", username);
                intent.putExtra("shop_btn", "true");
                startActivity(intent);
            }



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_restaurant_menu) {
            refreshGridView();
        } else if (id == R.id.nav_show_orders) {
            Intent intent = new Intent(MainActivity.this, OrdersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_edit_profile) {
            // Edit my profile
            User myProfile = RealmController.getUser(username, realm, getApplicationContext());
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            intent.putExtra("username", myProfile.getUsername());
            intent.putExtra("name", myProfile.getName());
            intent.putExtra("lastname", myProfile.getLastname());
            intent.putExtra("password", myProfile.getPassword());
            intent.putExtra("phone_number", myProfile.getPhone_number());
            startActivity(intent);

        } else if (id == R.id.nav_delete_profile) {
            deleteProfile();
        } else if (id == R.id.nav_logout) {
            clearPreferences();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_add_items_in_menu) {
            if (adminFlag) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivityForResult(intent, 1);
            } else {
                Toast.makeText(getApplicationContext(), "You are not admin!", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_admin_on_off) {
           /*if (MainActivity.orderPlaced.getUser() == null) {
               Toast.makeText(getApplicationContext(), "Jeste null", Toast.LENGTH_SHORT).show();
           } else {
               Toast.makeText(getApplicationContext(), "Nije null " + MainActivity.orderPlaced.getUser().getUsername(), Toast.LENGTH_SHORT).show();
           }*/
            /*((Switch) item.getActionView()).toggle();

            if (adminFlag) {
                adminFlag = false;
            } else {
                adminFlag = true;
            }*/

            /*if (SyncUser.currentUser() == null) {
                Toast.makeText(getApplicationContext(), "Jeste null", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Nije null " + MainActivity.orderPlaced.getUser().getUsername(), Toast.LENGTH_SHORT).show();
            }*/

            //get realm instance
            //this.realm = RealmController.with(this).getRealm();
            //SyncUser.currentUser().notify();

            /*SyncUser user = SyncUser.currentUser();

            if (user != null) {
                Toast.makeText(getApplicationContext(), "Nije null", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Jeste null " + MainActivity.orderPlaced.getUser().getUsername(), Toast.LENGTH_SHORT).show();
            }*/


           // orderStatusChangeListener();

            turnServiceOnOff();

            if(!NotificationService.serviceRunning) {
                Toast.makeText(getApplicationContext(), "Service running", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Service not running", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                // primanje usernema
                String result = data.getStringExtra("result");
                result = result.trim();
                if (!result.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void refreshGridView(){
        try {
            final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();

            //**RealmResults<MenuCategory> menuCategories = RealmController.getMenuCategories(realm, getApplicationContext());
            //******List<MenuCategory> menuCategories = RealmController.getMenuCategories(realm, getApplicationContext());
            menuCategories = RealmController.getMenuCategories(realm, getApplicationContext());



            //realm.close();

            if (menuCategories == null) {
                return;
            }

            categorySize = menuCategories.size();

           /* for (int i=0; i<categorySize; i++) {

                if (menuCategories.get(i).getImage() != null )
                    Toast.makeText(getApplicationContext(),menuCategories.get(i).getCategory() + " Nije null", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(),menuCategories.get(i).getCategory() + " Jeste null", Toast.LENGTH_SHORT).show();
            }*/

            gridview = (GridView) findViewById(R.id.custom_grid_menu);
            //**gridview.setAdapter(new CustomAdapter(this, names, bitmaps));
            gridview.setAdapter(new CustomAdapter(this, menuCategories, realm));

            /*menuCategories.addChangeListener(new RealmChangeListener<RealmResults<MenuCategory>>() {
                @Override
                public void onChange(RealmResults<MenuCategory> menuCategories) {

                }
            });*/

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"MainActivity:" +  ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete my profile ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RealmController.deleteUser(realm, getApplicationContext(), username);
                        clearPreferences();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    private void savePreferences(String isAdmin) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Edit and commit
        //UnameValue = isAdmin;
        editor.putString(PREF_UNAME_ADMIN, isAdmin);
        editor.commit();
    }

    private void loadPreferences() {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        // Get value
        String isAdmin = "";
        isAdmin = settings.getString(PREF_UNAME_ADMIN, DefaultUnameValue);


        if (isAdmin != null && !isAdmin.isEmpty() && isAdmin.length() > 0)
        {
            adminFlag = Boolean.valueOf(isAdmin);
            adminFlag = false; // ZAKOMENTARISATI UKOLIKO JE POTREBNO
        } /*else {
            Toast.makeText(getApplicationContext(), "Erron on loading admin flag", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void clearPreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }

    public void checkCheckedFoodIsValid() {
        if (orderPlaced.getItems() != null) {
            for (int i = 0; i < orderPlaced.getItems().size(); i++) {
                if (!orderPlaced.getItems().get(i).isValid()) {
                    orderPlaced.getItems().remove(i);
                    Toast.makeText(getApplicationContext(), "Some item is removed from your list", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void turnServiceOnOff() {
        if (NotificationService.serviceRunning) {
            Intent serviceIntent = new Intent(this, NotificationService.class);
            stopService(serviceIntent);
        } else {
            Intent serviceIntent = new Intent(this, NotificationService.class);
            //serviceIntent.putExtra("adminFlag", adminFlag);
            //serviceIntent.putExtra("username", username);
            startService(serviceIntent);
            /*notificationService.realm = realm;
            notificationService.application = getApplication();*/

        }
    }

}
