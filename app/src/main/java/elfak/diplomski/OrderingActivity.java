package elfak.diplomski;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import elfak.diplomski.Adapter.ListAdapter;
import elfak.diplomski.Adapter.ListAdapterCommentAndRating;
import elfak.diplomski.Adapter.ListAdapterOrderPlaced;
import elfak.diplomski.Model.CommentAndRating;
import elfak.diplomski.Model.Item;
import elfak.diplomski.Model.OrderPlaced;
import elfak.diplomski.Realm.RealmController;
import io.realm.MutableRealmInteger;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class OrderingActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewComAndRat;
    private volatile Realm realm;
    //private List<Item> mItemList = new ArrayList<>();
    private RealmResults<Item> mItemList;
    private RealmResults<CommentAndRating> commentAndRatings;// = new ArrayList<>();
    private static final String TAG = OrderingActivity.class.getSimpleName();
    private String category = "";
    private String categoryType = "";
    private int numberOfItemsInBestSeller;
    private TextView tv_name;
    private TextView tv_rating;
    private TextView tv_price;
    private TextView tv_describe;
    private ImageView iv_image;
    private TextView tv_category;
    RelativeLayout relative_layout_recyclerView;
    LinearLayout relative_layout_recyclerView_show_comments;
    ConstraintLayout linear_layout_showFullItem;
    private static int STATE_LIST_OF_ITEMS_SHOW = 1;
    private static int STATE_ITEM_SHOW = 2;
    private static int STATE_RATE_AND_COMMENT_SHOW = 3;
    private static int STATE_SHOW_SHOP_LIST = 4;
    private static int STATE_DELETE_OR_EDIT_ITEMS = 5;
    //private static int STATE_SHOW_ORDERS = 6;
    private int stateMenu = STATE_LIST_OF_ITEMS_SHOW;
    private boolean editMode = false;
    private int lastPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY); // full layout under action bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00212121")) );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

        mRecyclerView =  findViewById(R.id.recyclerView);
        mRecyclerViewComAndRat = findViewById(R.id.recyclerView_show_comments);

        /**
         * We uses a LinearLayoutManager to set the orientation of the RecyclerView and
         * make it look like a ListView
         */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderingActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(OrderingActivity.this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewComAndRat.setLayoutManager(linearLayoutManager2);

        String shop_btn = "";
        String deleteEditItems = "";
        Intent intent = getIntent();
        if (intent != null) {
            category = intent.getStringExtra("category");
            categoryType = intent.getStringExtra("categoryType");
            numberOfItemsInBestSeller = intent.getIntExtra("numberOfItemsInBestSeller", 0);
            shop_btn = intent.getStringExtra("shop_btn");
            deleteEditItems = intent.getStringExtra("delete_edit_items");
        }

        /*Button button = (Button) findViewById(R.id.show_describe_listview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });*/

        tv_name = findViewById(R.id.show_full_item_name);
        tv_rating =  findViewById(R.id.show_full_item_rating);
        tv_price =  findViewById(R.id.show_full_item_price);
        tv_describe =  findViewById(R.id.show_full_item_describe);
        tv_category = findViewById(R.id.show_full_item_category);
        iv_image =  findViewById(R.id.show_full_item_image_view);
        relative_layout_recyclerView = findViewById(R.id.recyclerView_layout);
        linear_layout_showFullItem = ( findViewById(R.id.show_full_item_layout));
        relative_layout_recyclerView_show_comments = findViewById(R.id.recyclerView_show_comments_and_rating_layout);
        relative_layout_recyclerView_show_comments.setVisibility(View.GONE);

        if (shop_btn != null && shop_btn.equals("true")) {
            stateMenu = STATE_SHOW_SHOP_LIST;
            showShopList();
        } else if (deleteEditItems != null && deleteEditItems.equals("true")) {
            stateMenu = STATE_DELETE_OR_EDIT_ITEMS;
            //fetchFood();
        } else {
            fetchItem();
            this.setTitle(category);
            //fetchComments("food1");
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        if (editMode) {
            editMode = false;

            fetchItem();
        }
    }

    @Override
    public void onBackPressed() {

        if (relative_layout_recyclerView_show_comments.getVisibility() == View.VISIBLE) {
            relative_layout_recyclerView_show_comments.setVisibility(View.GONE);
            mRecyclerViewComAndRat.setAdapter(null);
            //commentAndRatings.clear();
            //****commentAndRatings = new ArrayList<>();
            commentAndRatings.removeAllChangeListeners();
            commentAndRatings = null;
            stateMenu = STATE_ITEM_SHOW;
            invalidateOptionsMenu();
        }

        if (linear_layout_showFullItem.getVisibility() == View.VISIBLE) {
            mItemList.get(lastPosition).removeAllChangeListeners();
            linear_layout_showFullItem.setVisibility(View.GONE);
            relative_layout_recyclerView.setVisibility(View.VISIBLE);
            stateMenu = STATE_LIST_OF_ITEMS_SHOW;
            invalidateOptionsMenu();
        } else {
            //finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ordering, menu);

        MenuItem item1 = menu.findItem(R.id.action_confirm_selected_items);
        MenuItem item2 = menu.findItem(R.id.action_rate_and_comment);
        MenuItem item3 = menu.findItem(R.id.action_show_comments);
        MenuItem item4 = menu.findItem(R.id.action_delete_item);
        MenuItem item5 = menu.findItem(R.id.action_edit_item);

        if (stateMenu == STATE_LIST_OF_ITEMS_SHOW) { // state 1
            item1.setVisible(true);
            item2.setVisible(false);
            item3.setVisible(false);
            if (MainActivity.adminFlag) {
                item5.setVisible(false);
                item4.setVisible(true);
            }
            //stateMenu = STATE_LIST_OF_ITEMS_SHOW;
        } else if (stateMenu == STATE_ITEM_SHOW) { // state 2
            item1.setVisible(false);
            item2.setVisible(true);
            item3.setVisible(true);
            stateMenu = STATE_LIST_OF_ITEMS_SHOW;
            if (MainActivity.adminFlag) {
                item4.setVisible(false);
                item5.setVisible(true);
            }
        } else if (stateMenu == STATE_DELETE_OR_EDIT_ITEMS) { // state 5
            item1.setVisible(false);
            item2.setVisible(false);
            item3.setVisible(false);
        } else {
            item1.setVisible(true);
            item2.setVisible(false);
            item3.setVisible(false);
            //stateMenu = STATE_ITEM_SHOW;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_confirm_selected_items:
                /*if (linear_layout_showFullItem.getVisibility() == View.VISIBLE) {
                    linear_layout_showFullItem.setVisibility(View.GONE);
                    relative_layout_recyclerView.setVisibility(View.VISIBLE);
                    stateMenu = STATE_LIST_OF_ITEMS_SHOW;
                    invalidateOptionsMenu();
                } else {
                    selectedItems();
                }*/
                if (stateMenu == STATE_SHOW_SHOP_LIST) { // state 4`
                    if (MainActivity.orderPlaced.getItems().size() > 0) {
                        boolean isAllValid = true;
                        for (int i = 0; i <  MainActivity.orderPlaced.getItems().size(); i++) {
                            if (!MainActivity.orderPlaced.getItems().get(i).isValid()) {
                                MainActivity.orderPlaced.getItems().remove(i);
                                mRecyclerView.getAdapter().notifyItemRemoved(i);
                                mRecyclerView.getAdapter().notifyItemRangeChanged(i, MainActivity.orderPlaced.getItems().size());
                                isAllValid = false;
                                //i--; da li treba ?
                            }
                        }

                        if (!isAllValid) {
                            //mRecyclerView.getAdapter().notifyDataSetChanged();
                            Toast.makeText(getApplicationContext() ,"Some item are removed from list", Toast.LENGTH_SHORT).show();
                        } else
                            showShopLayout();
                    }
                    else {
                        finish();
                    }
                } else {
                    selectedItems();
                }


                return true;
            case android.R.id.home:

                if (relative_layout_recyclerView_show_comments.getVisibility() == View.VISIBLE) {
                    relative_layout_recyclerView_show_comments.setVisibility(View.GONE);
                    mRecyclerViewComAndRat.setAdapter(null);
                    //commentAndRatings.clear();
                    //****commentAndRatings = new ArrayList<>();
                    commentAndRatings.removeAllChangeListeners();
                    commentAndRatings = null;
                    stateMenu = STATE_ITEM_SHOW;
                    invalidateOptionsMenu();
                    return true;
                }

                if (linear_layout_showFullItem.getVisibility() == View.VISIBLE) {
                    mItemList.get(lastPosition).removeAllChangeListeners();
                    linear_layout_showFullItem.setVisibility(View.GONE);
                    relative_layout_recyclerView.setVisibility(View.VISIBLE);
                    stateMenu = STATE_LIST_OF_ITEMS_SHOW;
                    invalidateOptionsMenu();
                } else {
                    finish();
                }
                return true;
            case R.id.action_rate_and_comment:
                //stateMenu = STATE_RATE_AND_COMMENT_SHOW;
                //invalidateOptionsMenu();
                //linear_layout_showFullItem.setVisibility(View.INVISIBLE);
                if (mItemList.size() > lastPosition && mItemList.get(lastPosition).getName().equals(tv_name.getText().toString())) {
                    showRateAndComment(tv_name.getText().toString());
                } else {
                    itemIsNotValid();
                }
                return true;
            case R.id.action_show_comments:
                //linear_layout_showFullItem.setVisibility(View.INVISIBLE);

                if (mItemList.size() > lastPosition && mItemList.get(lastPosition).getName().equals(tv_name.getText().toString())) {
                    fetchComments(mItemList.get(lastPosition).getName());
                } else {
                    itemIsNotValid();
                }

                return true;
            case R.id.action_delete_item:

                try {
                    if (mItemList.size() > 0 ) {
                        //Toast.makeText(getApplication(), "Delete toast...", Toast.LENGTH_SHORT).show();
                        mRecyclerView.setAdapter(null);
                        //mRecyclerView.getAdapter().
                        stateMenu = STATE_DELETE_OR_EDIT_ITEMS;
                        fetchItem();
                    } else {
                        //Toast.makeText(getApplication(), "ItemIsNotValid toast...", Toast.LENGTH_SHORT).show();
                        itemIsNotValid();
                    }

                    /*if (mItemList.size() > lastPosition && mItemList.get(lastPosition).getName().equals(tv_name.getText().toString())) {
                        Toast.makeText(getApplication(), "Delete toast...", Toast.LENGTH_SHORT).show();
                        mRecyclerView.setAdapter(null);
                        //mRecyclerView.getAdapter().
                        stateMenu = STATE_DELETE_OR_EDIT_ITEMS;
                        fetchFood();
                    } else {
                        Toast.makeText(getApplication(), "ItemIsNotValid toast...", Toast.LENGTH_SHORT).show();
                        itemIsNotValid();
                    }*/
                } catch (Exception ex) {
                    Toast.makeText(getApplication(), ex.toString(), Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.action_edit_item:

                if (mItemList.size() > lastPosition && mItemList.get(lastPosition).getName().equals(tv_name.getText().toString())) {
                    editMode = true;
                    Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                    //Bitmap bitmap = ((BitmapDrawable)iv_image.getDrawable()).getBitmap();
                    //intent.putExtra("BitmapImage", (Bitmap) iv_image.getTag());
                    intent.putExtra("BitmapImage", ((BitmapDrawable)iv_image.getDrawable()).getBitmap());
                    intent.putExtra("name", tv_name.getText().toString());
                    intent.putExtra("describe", tv_describe.getText().toString());
                    intent.putExtra("price", tv_price.getText().toString());
                    intent.putExtra("category", mItemList.get(lastPosition).getMenuCategory().getCategory());
                    //intent.putExtra("category", tv_category.getText().toString());
                    //intent.putExtra("rating", tv_rating.getText().toString());
                    startActivity(intent);

                    if (linear_layout_showFullItem.getVisibility() == View.VISIBLE) {
                        mItemList.get(lastPosition).removeAllChangeListeners();
                        linear_layout_showFullItem.setVisibility(View.GONE);
                        relative_layout_recyclerView.setVisibility(View.VISIBLE);
                        stateMenu = STATE_LIST_OF_ITEMS_SHOW;
                        invalidateOptionsMenu();
                    } else {
                        finish();
                    }
                } else {
                    itemIsNotValid();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showShopLayout() {
        try {
            // get a reference to the already created main layout
            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.popup_layout);
            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = inflater.inflate(R.layout.shop_layout, null);

            // create the popup window
            /*int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);*/

            final PopupWindow popupWindow = new PopupWindow(popupView, mainLayout.getLayoutParams().MATCH_PARENT, mainLayout.getLayoutParams().WRAP_CONTENT, true);

            // show the popup window
            popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

            TextView textViewTotalPrice = popupView.findViewById(R.id.total_price_order_finish);
            textViewTotalPrice.setText("Total price: " + getTotalPrice());

            final RadioGroup radioGroup = popupView.findViewById(R.id.radioGroup);
            final EditText et_table_number = popupView.findViewById(R.id.table_number);

            Button btn_cancel = popupView.findViewById(R.id.cancelFinishOrder);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                }
            });


            Button btn_submit = popupView.findViewById(R.id.submitFinishOrder);
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String dateAndTime = currentDateAndTime();
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId > -1) {
                        RadioButton radioButton = popupView.findViewById(selectedId);
                        String paymentMethod = radioButton.getText().toString();
                        String tableNumber = et_table_number.getText().toString();
                        //EditText et_table_number = findViewById(R.id.table_number);
                        //Toast.makeText(OrderingActivity.this, dateAndTime + "  " + paymentMethod + "  " + et_table_number.getText().toString(), Toast.LENGTH_SHORT).show();

                        MainActivity.orderPlaced.setUser(MainActivity.user);
                        MainActivity.orderPlaced.setUsernameAndDateTime(MainActivity.username + "_" + dateAndTime);
                        MainActivity.orderPlaced.setDateAndTime(dateAndTime);
                        MainActivity.orderPlaced.setPaymentMethod(paymentMethod);
                        MainActivity.orderPlaced.setTableNumber(tableNumber);
                        MainActivity.orderPlaced.setStatus("Ordered");

                        String orders = "";
                        boolean isAllValid = true;
                        for (int i = 0; i <  MainActivity.orderPlaced.getItems().size(); i++) {

                            if (!MainActivity.orderPlaced.getItems().get(i).isValid()) {
                                MainActivity.orderPlaced.getItems().remove(i);
                                mRecyclerView.getAdapter().notifyItemRemoved(i);
                                mRecyclerView.getAdapter().notifyItemRangeChanged(i, MainActivity.orderPlaced.getItems().size());
                                isAllValid = false;
                            }

                            if (isAllValid) {
                                Item item = MainActivity.orderPlaced.getItems().get(i);
                                String quant = MainActivity.orderPlaced.getQuantitys().get(i).toString();
                                orders += item.getName() + "        " + item.getPrice() + "     x" + quant + " \n";
                            }
                        }

                        if (!isAllValid) {
                            //mRecyclerView.getAdapter().notifyDataSetChanged();
                            //popupWindow.dismiss();
                            Toast.makeText(getApplicationContext() ,"Some item are removed from list. Check your items before finish order.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        MainActivity.orderPlaced.setTotalPrice(getTotalPrice());
                        MainActivity.orderPlaced.setOrderListText(orders);

                        RealmController.addOrderPlaced(realm, getApplicationContext(), MainActivity.orderPlaced);

                        MainActivity.orderPlaced = new OrderPlaced(); // Empty brackets
                        popupWindow.dismiss();
                        finish();
                    } else {
                        Toast.makeText(OrderingActivity.this, "Select payment method", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showRateAndComment(final String itemName) {
        try {
            // get a reference to the already created main layout
            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.popup_layout);
            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.rating_and_comment_layout, null);

            // create the popup window
            /*int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);*/

            final PopupWindow popupWindow = new PopupWindow(popupView, mainLayout.getLayoutParams().MATCH_PARENT, mainLayout.getLayoutParams().WRAP_CONTENT, true);

            // show the popup window
            popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

            Button btn_cancel = popupView.findViewById(R.id.cancelRateBtn);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                    //linear_layout_showFullItem.setVisibility(View.VISIBLE);
                    //OrderingActivity.this.invalidateOptionsMenu();
                    //stateMenu = STATE_ITEM_SHOW;
                    //invalidateOptionsMenu();
                    //OrderingActivity.this.linear_layout_showFullItem.setVisibility(View.VISIBLE);
                }
            });

            final RatingBar ratingBar = popupView.findViewById(R.id.ratingBar);
            final EditText et_comment = popupView.findViewById(R.id.rating_and_comment_comment);

            Button btn_submit = popupView.findViewById(R.id.submitRateBtn);
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int numStars = (int) ratingBar.getRating();
                    String comment = et_comment.getText().toString();

                    if (numStars < 1) {
                        Toast.makeText(OrderingActivity.this, "Rate item then submit", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CommentAndRating commentAndRating = new CommentAndRating();
                    commentAndRating.setUsernameAndFood(MainActivity.username + "_" + itemName);
                    commentAndRating.setUser(RealmController.getUser(MainActivity.username, realm, getApplicationContext()));
                    commentAndRating.setItem(mItemList.get(lastPosition));
                    commentAndRating.setComment(comment);
                    //commentAndRating.setRating(Integer.toString(numStars));
                    commentAndRating.setRating((double)numStars);
                    commentAndRating.setDateAndTime(currentDateAndTime());
                    RealmController.addCommentAndRating(realm, getApplicationContext(), commentAndRating);
                    popupWindow.dismiss();

                    /*try {
                        Toast.makeText(OrderingActivity.this, "Rate: " + Integer.toString(numStars) + " " + comment, Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {

                    }*/

                }
            });

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchItem() {
        try {
            final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();

            //RealmResults<Item> foods = RealmController.getItem(realm, getApplicationContext(), category);
            //MenuCategory menuCategory = RealmController.get
            mItemList = RealmController.getFood(realm, getApplicationContext(), category, categoryType, numberOfItemsInBestSeller);
            //realm.close();

            /*LinkedList<Integer> iconList = new LinkedList<>();

            if (stateMenu != STATE_DELETE_OR_EDIT_ITEMS) {
                if (MainActivity.orderPlaced.getItems() != null) {
                    for (int i = 0; i < mItemList.size(); i++) {
                        if (MainActivity.orderPlaced.getItems().contains(mItemList.get(i))) {
                            iconList.add(R.drawable.listview_added_touch);
                        }
                        else {
                            iconList.add(R.drawable.listview_add_touch);
                        }
                    }
                } else {
                    iconList = new LinkedList<>(Collections.nCopies(mItemList.size(),R.drawable.listview_add_touch));
                }
            } else {
                iconList = new LinkedList<>(Collections.nCopies(mItemList.size(),R.drawable.ic_action_delete));
                //Toast.makeText(getApplicationContext(), "Postavljene ikonice za delete", Toast.LENGTH_SHORT).show();
            }*/

            Map<String, Integer> nameDrawableList = new HashMap<String, Integer>();

            if (stateMenu != STATE_DELETE_OR_EDIT_ITEMS) {
                if (MainActivity.orderPlaced.getItems() != null) {
                    for (int i = 0; i < mItemList.size(); i++) {
                        if (MainActivity.orderPlaced.getItems().contains(mItemList.get(i))) {
                            nameDrawableList.put(mItemList.get(i).getName(), (R.drawable.listview_added_touch));
                        }
                        else {
                            nameDrawableList.put(mItemList.get(i).getName(), (R.drawable.listview_add_touch));
                        }
                    }
                } else {
                    for (int i = 0; i < mItemList.size(); i++) {
                        nameDrawableList.put(mItemList.get(i).getName(), (R.drawable.listview_add_touch));
                    }
                }
            } else {
                for (int i = 0; i < mItemList.size(); i++) {
                    nameDrawableList.put(mItemList.get(i).getName(), (R.drawable.ic_action_delete));
                }
            }


            //new LinkedList<>(Collections.nCopies(mItemList.size(),R.drawable.listview_add_touch));
            if (mItemList != null) {
                mRecyclerView.setAdapter(new ListAdapter(this, mItemList, nameDrawableList,
                        relative_layout_recyclerView, linear_layout_showFullItem, realm));
                /*mRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });*/
            } else {
                //Log.e(TAG, "@fetchFood Error: Adapter is null");

            }

            linear_layout_showFullItem.setTag(linear_layout_showFullItem.getVisibility());
            linear_layout_showFullItem.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int newVis = linear_layout_showFullItem.getVisibility();
                    if((int)linear_layout_showFullItem.getTag() != newVis)
                    {
                        linear_layout_showFullItem.setTag(linear_layout_showFullItem.getVisibility());
                        //visibility has changed

                        if (linear_layout_showFullItem.getVisibility() == View.VISIBLE) {
                            final int itemPosition = (int) mRecyclerView.getAdapter().getItemId(0);
                            /*Bitmap bitmap = byteToBitmap(mItemList.get(itemPosition).getImage());
                            iv_image.setImageBitmap(bitmap);
                            iv_image.setTag(bitmap);*/
                            lastPosition = itemPosition;
                            iv_image.setImageBitmap(byteToBitmap(mItemList.get(itemPosition).getImage()));
                            tv_name.setText(mItemList.get(itemPosition).getName());
                            tv_describe.setText(mItemList.get(itemPosition).getDescribe());
                            if (mItemList.get(itemPosition).getRating() != null) {
                                tv_rating.setText("Rating:" + mItemList.get(itemPosition).getRating());
                            } else {
                                tv_rating.setText("Rating: unrated");
                            }

                            mItemList.get(itemPosition).addChangeListener(new RealmObjectChangeListener<RealmModel>() {
                                @Override
                                public void onChange(RealmModel realmModel, @Nullable ObjectChangeSet changeSet) {
                                    if (changeSet.isFieldChanged("rating"))
                                    {
                                        tv_rating.setText(mItemList.get(itemPosition).getRating());
                                    }
                                }
                            });

                            tv_price.setText("Price: " + mItemList.get(itemPosition).getPrice());
                            //tv_category.setText("Category: " + category);
                            tv_category.setText("* " + category + " *");
                            //Toast.makeText(getApplicationContext(), "Jeste VISIBLE", Toast.LENGTH_SHORT).show();
                            stateMenu = STATE_ITEM_SHOW;
                            invalidateOptionsMenu();

                        } else {
                            /*if (stateMenu == 3) {
                                stateMenu = STATE_ITEM_SHOW;
                                invalidateOptionsMenu();
                            }*/
                            //Toast.makeText(getApplicationContext(), "Nije VISIBLE", Toast.LENGTH_SHORT).show();
                            /*if (commentAndRatings != null && commentAndRatings.size() > 0) {
                                relative_layout_recyclerView_show_comments.setVisibility(View.GONE);
                                mRecyclerViewComAndRat.setAdapter(null);
                                //commentAndRatings.clear();
                                /commentAndRatings = new ArrayList<>();
                            }*/
                        }
                    }
                }
            });

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void selectedItems() {
        try {
            RealmList<Item> items; //= new RealmList<>();

            boolean itemsIsValid = true;
            if (MainActivity.orderPlaced.getItems() != null) {
                items = MainActivity.orderPlaced.getItems();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isValid()) {
                        if (items.get(i).getMenuCategory().getCategory().equals(category)) {
                            items.remove(i);
                            i--;
                        }
                    } else {
                        itemsIsValid = false;
                    }
                }
            } else
                items = new RealmList<>();


            for (int i = 0; i < mItemList.size(); i++) {
                if (mRecyclerView.getAdapter().getItemId(i) != -1) {
                    //Toast.makeText(getApplicationContext(), mItemList.get(i).getName(), Toast.LENGTH_SHORT).show();
                    //MainActivity.foodList.add(mItemList.get(i).getName());
                    //if (!items.contains(mItemList.get(i))) // if that object is not in list add him

                    items.add(mItemList.get(i));


                }
            }

            if (!itemsIsValid)
                Toast.makeText(getApplicationContext(), "Some item are removed from list", Toast.LENGTH_SHORT).show();

            MainActivity.orderPlaced.setItems(items);
            finish();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private Bitmap byteToBitmap(byte[] bytes) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        } catch (Exception ex) {
            //Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            ex.getMessage();
            return null;
        }
    }

    private void showShopList() {


        RealmList<Integer> list = new RealmList<>();
        for (int i = 0; i < MainActivity.orderPlaced.getItems().size(); i++) {
            list.add(1);
        }
        MainActivity.orderPlaced.setQuantitys(list);

        mRecyclerView.setAdapter(new ListAdapterOrderPlaced(getApplicationContext(), MainActivity.orderPlaced));

    }

    public static String currentDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); // a
        // get current date time with Date()
        Date date = new Date();
        // System.out.println(dateFormat.format(date));
        // don't print it, but save it!
        return dateFormat.format(date);
    }

    private String getTotalPrice() {

        float sum = 0;
        String returnString = "";
        for (int i = 0; i<MainActivity.orderPlaced.getItems().size(); i++) {
            String[] parts = MainActivity.orderPlaced.getItems().get(i).getPrice().split(" ");
            sum += Float.parseFloat(parts[0]) * (MainActivity.orderPlaced.getQuantitys().get(i));
        }

        returnString = String.valueOf(sum);

        if (MainActivity.orderPlaced.getItems().size() > 0) {
            String[] parts = MainActivity.orderPlaced.getItems().get(0).getPrice().split(" ");
            returnString = returnString + " " + parts[1];
        }

        return returnString;
    }

    private void fetchComments(String itemName) {
        try {

            final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();

            commentAndRatings = RealmController.getCommentsAndRatings(realm, getApplicationContext(), itemName);

            //LinkedList<Integer> iconList = new LinkedList<>();

            if (commentAndRatings != null) {

                //showMyCommentFirst();

                Map<String, Integer> nameDrawableList = new HashMap<String, Integer>();

                ///nameDrawableList.put(commentAndRatings.get(0).getUsernameAndFood(), R.drawable.ic_action_delete);
                for (int i=1; i < commentAndRatings.size(); i++) {
                    nameDrawableList.put(commentAndRatings.get(i).getUsernameAndFood(), R.drawable.ic_action_delete);
                }

                stateMenu = STATE_DELETE_OR_EDIT_ITEMS;
                invalidateOptionsMenu();
                relative_layout_recyclerView_show_comments.setVisibility(View.VISIBLE);
                //mRecyclerViewComAndRat.setAdapter(new ListAdapterCommentAndRating(this, commentAndRatings, new LinkedList<>(Collections.nCopies(commentAndRatings.size(),R.drawable.ic_action_delete)), realm, MainActivity.adminFlag));
                mRecyclerViewComAndRat.setAdapter(new ListAdapterCommentAndRating(this, commentAndRatings, nameDrawableList, realm, MainActivity.adminFlag));

            } else {
                //Log.e(TAG, "@fetchFood Error: Adapter is null");
                Toast.makeText(getApplicationContext(), "No comments for this item", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void itemIsNotValid() {

        Toast.makeText(getApplication(), "This item is removed from list", Toast.LENGTH_SHORT).show();
        if (linear_layout_showFullItem.getVisibility() == View.VISIBLE) {
            mItemList.get(lastPosition).removeAllChangeListeners();
            linear_layout_showFullItem.setVisibility(View.GONE);
            relative_layout_recyclerView.setVisibility(View.VISIBLE);
            stateMenu = STATE_LIST_OF_ITEMS_SHOW;
            invalidateOptionsMenu();
        } else {
            finish();
        }
    }

    /*private void showMyCommentFirst() {
        CommentAndRating myComment = null;
        for (int i = 0; i < commentAndRatings.size(); i++) {

            if (commentAndRatings.get(i).getUser().getUsername().equals(MainActivity.username)) {
                myComment = commentAndRatings.get(i);
                break;
            }

        }
        if (myComment != null) {
            List<CommentAndRating> comments = new ArrayList<>();
            comments.add(myComment);
            for (int i = 0; i < commentAndRatings.size(); i++) {
                if (!commentAndRatings.get(i).getUser().getUsername().equals(MainActivity.username)) {
                    comments.add(commentAndRatings.get(i));
                }
            }
            commentAndRatings = comments;
        }
    }*/

}
