package elfak.diplomski;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import elfak.diplomski.Model.Item;
import elfak.diplomski.Model.MenuCategory;
import elfak.diplomski.Realm.RealmController;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class AdminActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 100;
    private Bitmap bitmap = null;
    private EditText et_name;
    private EditText et_describe;
    private EditText et_price;
    private EditText et_currency;
    private EditText et_sortNumber;
    private ImageButton selectImage;
    private Spinner categorySpinner;
    private int objectToSave = 1; // Category - 1; Drink - 2; Item - 3;
    private volatile Realm realm;
    private RealmResults<MenuCategory> menuCategories;
    private boolean editMode = false;
    private LinearLayout spinnerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY); // full layout under action bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00212121")) );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        et_name = (EditText) findViewById(R.id.name_item_admin);
        et_describe = (EditText) findViewById(R.id.describe_admin);
        et_price = (EditText) findViewById(R.id.price_admin);
        et_currency = (EditText) findViewById(R.id.currency_admin);
        selectImage = (ImageButton) findViewById(R.id.imageButton);
        categorySpinner = (Spinner) findViewById(R.id.spinner_category);
        et_sortNumber = findViewById(R.id.sort_number_admin);
        spinnerLayout = findViewById(R.id.spinner_layout);

        Button saveBtn = (Button) findViewById(R.id.save_admin);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSave();
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });
        et_sortNumber.setText(Integer.toString(MainActivity.categorySize + 1));

        String categoryName = "";
        String name = "";
        String describe = "";
        String price = "";
        String category = "";
        String sortNumber = "";
        //String rating = "";
        Intent intent = getIntent();
        if (intent != null) {
            //editMode = true;
            // Edit category
            categoryName = intent.getStringExtra("categoryName");
            bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");
            // Edit food
            name = intent.getStringExtra("name");
            describe = intent.getStringExtra("describe");
            price = intent.getStringExtra("price");
            category = intent.getStringExtra("category");
            sortNumber = intent.getStringExtra("sortNumber");
            //rating = intent.getStringExtra("rating");
        }


        if (categoryName != null && !categoryName.equals("")) {
            editMode = true;
            et_name.setText(categoryName);
            et_name.setEnabled(false);
            selectImage.setImageBitmap(bitmap);
            et_sortNumber.setText(sortNumber);
            TextView textView = findViewById(R.id.imageLabel);
            textView.setText("Change image");
        } /*else {
            //categorySpinner.setOnItemSelectedListener(this);
            setSpinner();
        }*/

        if (name != null && !name.equals("")){
            objectToSave = 2;
            editMode = true;
            setSpinner();
            et_sortNumber.setVisibility(View.GONE);
            et_name.setText(name);
            et_name.setEnabled(false);
            et_describe.setText(describe);
            String[] parts = price.split(" ");
            et_price.setText(parts[1]);
            et_currency.setText(parts[2]);
            selectImage.setImageBitmap(bitmap);
            TextView textView = findViewById(R.id.imageLabel);
            textView.setText("Change image");
            ArrayAdapter myAdap = (ArrayAdapter) categorySpinner.getAdapter();
            int spinnerPosition = myAdap.getPosition(category);
            categorySpinner.setSelection(spinnerPosition);
            Toast.makeText(getApplicationContext(), Integer.toString(spinnerPosition) + " - " + category, Toast.LENGTH_SHORT).show();
        } else {
            // Default activity for adding category
            setSpinner();
            et_describe.setVisibility(View.GONE);
            //categorySpinner.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            et_price.setVisibility(View.GONE);
            et_currency.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // If is editMode then dont create menu
        if (editMode)
            return true;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_category:
                et_sortNumber.setVisibility(View.VISIBLE);
                et_describe.setVisibility(View.GONE);
                //categorySpinner.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                et_price.setVisibility(View.GONE);
                et_currency.setVisibility(View.GONE);
                objectToSave = 1;
                return true;
           /* case R.id.action_add_drink:
                et_describe.setVisibility(View.VISIBLE);
                categorySpinner.setVisibility(View.VISIBLE);
                et_price.setVisibility(View.VISIBLE);
                objectToSave = 2;
                return true;*/
            case R.id.action_add_food:
                if (categorySpinner.getAdapter().getCount() > 0) {
                    et_describe.setVisibility(View.VISIBLE);
                    //categorySpinner.setVisibility(View.VISIBLE);
                    spinnerLayout.setVisibility(View.VISIBLE);
                    et_price.setVisibility(View.VISIBLE);
                    et_currency.setVisibility(View.VISIBLE);
                    et_sortNumber.setVisibility(View.GONE);
                    objectToSave = 3;
                } else {
                    Toast.makeText(getApplicationContext(), "Add at least one category first", Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSpinner(){
        final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
        Realm.setDefaultConfiguration(syncConfiguration);
        realm = Realm.getDefaultInstance();

        menuCategories = RealmController.getMenuCategories(realm, getApplicationContext());

        //String[] names = new String[menuCategories.size()];
        List<String> categories = new ArrayList<String>();
        int i = 0;

        if (!editMode)
            categories.add("Select category");

        while (i < menuCategories.size()) { //!menuCategories.isEmpty()
            MenuCategory menuCategory = menuCategories.get(i);//.get(0);
            //names[i] = menuCategory.getCategory();
            categories.add(menuCategory.getCategory());
            i++;
            //Toast.makeText(getApplicationContext(), menuCategory.getCategory() + " " + Integer.toString(i), Toast.LENGTH_SHORT).show();
        }



        // Creating adapter for spinner
        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, categories);

        // Drop down layout style - list view with radio button
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // attaching data adapter to spinner
        categorySpinner.setAdapter(dataAdapter);

        /*if (realm != null && !realm.isClosed()) {
            realm.close();
            realm = null;
        }*/

    }

    private void btnSave(){
        String name = et_name.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Insert name!", Toast.LENGTH_SHORT).show();
            return;
        }

        /*if (realm != null && !realm.isClosed()) {
            realm.close();
            realm = null;
        }*/


        if (objectToSave == 1) // save category
        {
            final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();

            String sortNumber = et_sortNumber.getText().toString();

            final MenuCategory menuCategory = new MenuCategory();
            menuCategory.setCategory(name);
            menuCategory.setImage(bitmapToByte(bitmap));
            menuCategory.setSortNumber(sortNumber);
            menuCategory.setVisibility(true);


            if (menuCategory.getImage() == null) {
                Toast.makeText(getApplicationContext(), "Insert image first", Toast.LENGTH_SHORT).show();
                return;
            }

            //addCategory(menuCategory);
            executeTransaction(menuCategory);
        } /*else if (objectToSave == 2) { // save drink
            String describe = et_describe.getText().toString();
            String price = et_price.getText().toString();
            //String categorySelected = categorySpinner.getSelectedItem().toString();
            MenuCategory menuCategory = menuCategories.get(categorySpinner.getSelectedItemPosition());

            final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();

            final Drink drink = new Drink();
            drink.setDescribe(describe);
            drink.setName(name);
            drink.setImage(bitmapToByte(bitmap));
            drink.setPrice(price);
            drink.setMenuCategory(menuCategory);

            executeTransaction(drink);

        }*/ else { // save item
            String describe = et_describe.getText().toString();
            String price = et_price.getText().toString();
            String priceCurrency = et_currency.getText().toString();
            //String categorySelected = categorySpinner.getSelectedItem().toString();

            if (categorySpinner.getSelectedItemPosition() < 1 && !editMode) {
                Toast.makeText(getApplicationContext(), "Select category", Toast.LENGTH_SHORT).show();
                return;
            }

            MenuCategory menuCategory;// = menuCategories.get(categorySpinner.getSelectedItemPosition() - 1);

            if (editMode) {
                menuCategory = menuCategories.get(categorySpinner.getSelectedItemPosition());
            } else {
                menuCategory = menuCategories.get(categorySpinner.getSelectedItemPosition() - 1);
            }

            final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();

            final Item item = new Item();
            item.setDescribe(describe);
            item.setName(name);
            item.setImage(bitmapToByte(bitmap));
            item.setMenuCategory(menuCategory);
            item.setPrice(price + " " + priceCurrency);
            //item.setPriceCurrency(priceCurrency);
            if (RealmController.getItem(realm, getApplicationContext(), name) != null)
                item.setRating(RealmController.getItem(realm, getApplicationContext(), name).getRating());

            if (item.getRating() == null)
                item.setRating("");

            if (item.getImage() == null) {
                Toast.makeText(getApplicationContext(), "Insert image first", Toast.LENGTH_SHORT).show();
                return;
            }

            executeTransaction(item);
        }
    }

    private void executeTransaction(final Object object) {

        final ProgressDialog progressDialog = new ProgressDialog(AdminActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        try {
            realm.executeTransactionAsync(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm bgRealm) {
                                                  bgRealm.insertOrUpdate((RealmModel) object);
                                                  //uploadData();
                                                  //realm.insertOrUpdate(user);
                                              }
                                          }, new Realm.Transaction.OnSuccess() {
                                              @Override
                                              public void onSuccess() {
                                                  progressDialog.dismiss();
                                                  // Original queries and Realm objects are automatically updated.
                                                  //Toast.makeText(getApplicationContext(), "Created", Toast.LENGTH_SHORT).show();
                                                  //realm.close();
                                                  Intent returnIntent = new Intent();
                                                  returnIntent.putExtra("result", "Successfully added");
                                                  setResult(Activity.RESULT_OK, returnIntent);
                                                  finish();
                                              }
                                          }, new Realm.Transaction.OnError() {
                                              @Override
                                              public void onError(Throwable error) {
                                                  progressDialog.dismiss();
                                                  Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                              }
                                          }
            );
        } catch (Exception ex) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        } finally {
           if(realm != null) {
               realm.close();
               realm = null;
           }
        }
    }

    private byte[] bitmapToByte(Bitmap bitmap){

        if (bitmap == null) {
            return null;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }

    /* Choose an image from Gallery */
    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);

                    try {
                        //Getting the Bitmap from Gallery
                        bitmap = getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri), 256, 256);
                        //Setting the Bitmap to ImageView
                        selectImage.setImageBitmap(bitmap);
                        TextView textView = findViewById(R.id.imageLabel);
                        textView.setText("Change image");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }



}
