package elfak.diplomski;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

import java.util.Collections;
import java.util.LinkedList;

import javax.annotation.Nullable;

import elfak.diplomski.Adapter.ListAdapterOrders;
import elfak.diplomski.Model.OrderPlaced;
import elfak.diplomski.Realm.RealmController;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class OrdersActivity extends AppCompatActivity {

    //private List<OrderPlaced> orderPlaceds = new ArrayList<>();
    private RealmResults<OrderPlaced> orderPlaceds;
    private volatile Realm realm;
    private RelativeLayout rl_show_orders;
    private LinearLayout ll_show_order; //LinearLayout ll_show_order;
    private RecyclerView mRecyclerView;
    private TextView tv_dateAndTime;
    private TextView tv_username;
    private TextView tv_fullName;
    private TextView tv_phoneNumber;
    private TextView tv_ordered_items;
    private TextView tv_price;
    private TextView tv_status;
    private ScrollView scrollView;
    private boolean showMenu = false;
    private int lastPosition = -1;
    //private boolean getAllOrders = false;
    private String idOrderPlaced = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY); // full layout under action bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_orders);
        setSupportActionBar(toolbar);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00212121")) );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rl_show_orders = findViewById(R.id.rl_show_orders_items_orders);
        ll_show_order = findViewById(R.id.ll_show_order_item_orders);

        mRecyclerView = findViewById(R.id.recyclerView_show_orders);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrdersActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        tv_dateAndTime = findViewById(R.id.tv_date_and_time_orders);
        tv_username = findViewById(R.id.tv_username_orders);
        tv_fullName = findViewById(R.id.tv_full_name_orders);
        tv_ordered_items = findViewById(R.id.tv_ordered_items_orders);
        tv_price = findViewById(R.id.tv_items_and_price_orders);
        tv_phoneNumber = findViewById(R.id.tv_phone_number_orders);
        tv_status = findViewById(R.id.tv_status_orders);

        scrollView = findViewById(R.id.scrollView_orders);

        setTitle("My orders");

        fetchOrders();

    }

    @Override
    public void onBackPressed() {


        if (ll_show_order.getVisibility() == View.VISIBLE) {
            orderPlaceds.get(lastPosition).removeAllChangeListeners();
            ll_show_order.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            rl_show_orders.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
        } else {
            //finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_orders, menu);

        MenuItem item1 = menu.findItem(R.id.action_status_reject);
        MenuItem item2 = menu.findItem(R.id.action_status_completed);
        MenuItem item3 = menu.findItem(R.id.action_status_in_progress);
        MenuItem item4 = menu.findItem(R.id.action_delete_item_orders);
        //MenuItem item5 = menu.findItem(R.id.action_get_all_orders);


        if (MainActivity.adminFlag && showMenu) {
            item1.setVisible(true);
            item2.setVisible(true);
            item3.setVisible(true);
            item4.setVisible(true);
            //item5.setVisible(false);
            showMenu = false;
        } /*else if (MainActivity.adminFlag) {
            item1.setVisible(false);
            item2.setVisible(false);
            item3.setVisible(false);
            item4.setVisible(false);
            //item5.setVisible(true);
        } */else {
            item1.setVisible(false);
            item2.setVisible(false);
            item3.setVisible(false);
            item4.setVisible(false);
            //item5.setVisible(false);
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
        if (id == android.R.id.home) {

            if (ll_show_order.getVisibility() == View.VISIBLE) {
                orderPlaceds.get(lastPosition).removeAllChangeListeners();
                ll_show_order.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                rl_show_orders.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            } else {
                finish();
            }

            return true;
        } else if (id == R.id.action_status_completed) {

            try {
                    changeOrderStatus("Completed");
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }

            return true;
        } else if (id == R.id.action_status_reject) {
                changeOrderStatus("Rejected");

            return true;
        } else if (id == R.id.action_status_in_progress) {
                changeOrderStatus("In Progress");

            return true;
        } else if (id == R.id.action_delete_item_orders) {

            // IS VALID ???
            if (orderPlaceds.size() > lastPosition && orderPlaceds.get(lastPosition).getUsernameAndDateTime().equals(idOrderPlaced)) {
                deleteOrder();
            } else {
                itemIsNotValid();
            }

            return true;
        } else if (id == R.id.action_get_all_orders) {
            getAllOrders();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchOrders() {
        try {

            final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();

            if (MainActivity.adminFlag) {
                orderPlaceds = RealmController.getAllOrders(realm, getApplicationContext());
                //getAllOrders = false;
            } else {
                orderPlaceds = RealmController.getMyOrders(realm, getApplicationContext(), MainActivity.username);
            }
            //LinkedList<Integer> iconList = new LinkedList<>();

            if (orderPlaceds != null) {
                mRecyclerView.setAdapter(new ListAdapterOrders(this, orderPlaceds, new LinkedList<>(Collections.nCopies(orderPlaceds.size(),R.drawable.ic_action_details)), realm, rl_show_orders, ll_show_order, scrollView));

            } else {
                //Log.e(TAG, "@fetchFood Error: Adapter is null");
                Toast.makeText(getApplicationContext(), "Nothing is ordered", Toast.LENGTH_SHORT).show();
                finish();
            }


            ll_show_order.setTag(ll_show_order.getVisibility());
            ll_show_order.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int newVis = ll_show_order.getVisibility();
                    if((int)ll_show_order.getTag() != newVis)
                    {
                        ll_show_order.setTag(ll_show_order.getVisibility());
                        //visibility has changed

                        if (ll_show_order.getVisibility() == View.VISIBLE) {
                            //scrollView.setVisibility(View.VISIBLE);
                            final int itemPosition = (int) mRecyclerView.getAdapter().getItemId(0);
                            lastPosition = itemPosition;

                            //Toast.makeText(getApplicationContext(), "Usao item: " + String.valueOf(itemPosition), Toast.LENGTH_SHORT).show();

                            if (orderPlaceds.get(itemPosition).getUser() != null) {
                                tv_username.setText(orderPlaceds.get(itemPosition).getUser().getUsername());
                                tv_fullName.setText(orderPlaceds.get(itemPosition).getUser().getName() + " " + orderPlaceds.get(itemPosition).getUser().getLastname());
                                tv_phoneNumber.setText(orderPlaceds.get(itemPosition).getUser().getPhone_number());
                            }
                            else {
                                tv_username.setText("This account is deleted");
                                tv_fullName.setText("");
                                tv_phoneNumber.setText("");
                            }

                            idOrderPlaced = orderPlaceds.get(itemPosition).getUsernameAndDateTime();
                            tv_dateAndTime.setText(orderPlaceds.get(itemPosition).getDateAndTime());
                            //tv_price.setText("Items: " + String.valueOf(orderPlaceds.get(itemPosition).getItems().size()) + "       Total price: " + orderPlaceds.get(itemPosition).getTotalPrice());
                            tv_price.setText("Items: " + String.valueOf(orderPlaceds.get(itemPosition).getQuantitys().size()) + "       Total price: " + orderPlaceds.get(itemPosition).getTotalPrice());
                            tv_status.setText(orderPlaceds.get(itemPosition).getStatus());

                            orderPlaceds.get(itemPosition).addChangeListener(new RealmObjectChangeListener<RealmModel>() {
                                @Override
                                public void onChange(RealmModel realmModel, @Nullable ObjectChangeSet changeSet) {
                                    if (changeSet.isFieldChanged("status"))
                                    {
                                        tv_status.setText(orderPlaceds.get(itemPosition).getStatus());
                                    }
                                }
                            });

                            /*String orders = "";
                            for (int i=0; i < orderPlaceds.get(itemPosition).getItems().size(); i++) {
                                Item food = orderPlaceds.get(itemPosition).getItems().get(i);
                                String quant = orderPlaceds.get(itemPosition).getQuantitys().get(i).toString();
                                orders += food.getName() + "        " + food.getPrice() + "     x" + quant + " \n";
                            }

                            tv_ordered_items.setText(orders);*/

                            tv_ordered_items.setText(orderPlaceds.get(itemPosition).getOrderListText());

                            showMenu = true;
                            invalidateOptionsMenu();

                        }

                    }
                }
            });

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void changeOrderStatus(String status) {

        try {

            if (orderPlaceds.size() > lastPosition && orderPlaceds.get(lastPosition).getUsernameAndDateTime().equals(idOrderPlaced)) {

                //orderPlaceds = new ArrayList<>();
                realm.beginTransaction();
                orderPlaceds.get(lastPosition).setStatus(status);
                realm.commitTransaction();
                tv_status.setText(orderPlaceds.get(lastPosition).getStatus());

                mRecyclerView.getAdapter().notifyItemChanged(lastPosition);

           /* final OrderPlaced orderPlaced = new OrderPlaced();// = orderPlaceds.get(lastPosition);
            orderPlaced.setStatus(status);
            orderPlaced.setUsernameAndDateTime(orderPlaceds.get(lastPosition).getUsernameAndDateTime());
            orderPlaced.setUser(orderPlaceds.get(lastPosition).getUser());
            orderPlaced.setTotalPrice(orderPlaceds.get(lastPosition).getTotalPrice());
            orderPlaced.setTableNumber(orderPlaceds.get(lastPosition).getTableNumber());
            orderPlaced.setPaymentMethod(orderPlaceds.get(lastPosition).getPaymentMethod());
            orderPlaced.setDateAndTime(orderPlaceds.get(lastPosition).getDateAndTime());
            orderPlaced.setQuantitys(orderPlaceds.get(lastPosition).getQuantitys());
            orderPlaced.setItems(orderPlaceds.get(lastPosition).getItems());*/

                //orderPlaced.setStatus(status);
                //RealmController.addOrderPlaced(realm, getApplicationContext(), orderPlaced);

            } else {
                itemIsNotValid();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteOrder(){
        try {
            //orderPlaceds = new ArrayList<>();
            realm.beginTransaction();
            orderPlaceds.get(lastPosition).deleteFromRealm();
            realm.commitTransaction();

            //orderPlaceds.remove(lastPosition);
            mRecyclerView.getAdapter().notifyItemRemoved(lastPosition);
            mRecyclerView.getAdapter().notifyItemRangeChanged(lastPosition, orderPlaceds.size());

            if (ll_show_order.getVisibility() == View.VISIBLE) {
                ll_show_order.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                rl_show_orders.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    public void getAllOrders() {
        try {

            mRecyclerView.setAdapter(null);
            //****orderPlaceds = new ArrayList<>();
            orderPlaceds = null;
            fetchOrders();

            /*if (ll_show_order.getVisibility() == View.VISIBLE) {
                ll_show_order.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                rl_show_orders.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }*/

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void itemIsNotValid() {
        Toast.makeText(getApplication(), "This item is removed from list", Toast.LENGTH_SHORT).show();
        if (ll_show_order.getVisibility() == View.VISIBLE) {
            ll_show_order.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            rl_show_orders.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
        } else {
            finish();
        }
    }
}
