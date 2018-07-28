package elfak.diplomski;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.annotation.Nonnull;

import elfak.diplomski.Model.User;
import elfak.diplomski.Realm.RealmController;
import io.realm.DynamicRealm;
import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;


public class LoginActivity extends AppCompatActivity {


    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    public static final String TAG = LoginActivity.class.getName();

    //private static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/default";
    //private static final String REALM_AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    private volatile Realm realm;

    public static final String PREFS_NAME = "preferences";
    private static final String PREF_UNAME = "Username";
    private static final String PREF_UNAME_ADMIN = "isAdmin";

    private final String DefaultUnameValue = "";
    private String UnameValue;
    //private int numberOfUsers = 0;
    //private SyncConfiguration syncConfig ;
    //private SyncUser syncUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#330000ff")));
        getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#550000ff")));
        getSupportActionBar().setElevation(0);
        /*getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setElevation(0);*/
        getSupportActionBar().hide(); //<< this
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.activity_login);

        // Initialize Realm. Should only be done once when the application starts.
        //Realm.init(getApplicationContext());

        // Create the Realm instance
        //realm = Realm.getDefaultInstance();

        //realm = RealmController.getInstance().getRealm();


        //get realm instance
        this.realm = RealmController.with(this).getRealm();


        //Realm.init(getApplication().getApplicationContext());

        //basicCRUD(realm);
        //basicQuery(realm);
        //basicLinkQuery(realm);

        /*final SyncCredentials syncCredentials = SyncCredentials.usernamePassword(ID, PASSWORD, false);
        SyncUser.loginAsync(syncCredentials, AUTH_URL, new SyncUser.Callback() {
            @Override
            public void onSuccess(SyncUser user) {
                final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(user, REALM_URL).build();
                Realm.setDefaultConfiguration(syncConfiguration);
                realm = Realm.getDefaultInstance();
            }

            @Override
            public void onError(ObjectServerError error) {
            }
        });*/

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();

                /*if (realm != null && !realm.isClosed()) {

                }*/
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(i, 1);

                /*numberOfUsers = numberOfUsers + 1;
                final User user = new User();
                user.setUsername("user" + Integer.toString(numberOfUsers));
                user.setPassword("passw");
                user.setName("name" + Integer.toString(numberOfUsers));
                user.setLastname("lastname" + Integer.toString(numberOfUsers));
                user.setPhone_number(Integer.toString(numberOfUsers));

                //realm = Realm.getDefaultInstance(); // treba izbaciti


                try {
                    //RealmController.getInstance().addUser(user, realm);
                    RealmController.addUser(user, realm, getApplicationContext());
                    //RealmController.with(LoginActivity.this).addUser(user);
                    //Toast.makeText(getApplicationContext(),"Added", Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                }*/


                //addOrUpdateUser(user);
                /*realm.executeTransactionAsync(new Realm.Transaction() {
                                                  @Override
                                                  public void execute(Realm bgRealm) {
                                                      bgRealm.insertOrUpdate(user);
                                                      //uploadData();
                                                      //realm.insertOrUpdate(user);
                                                  }
                                              }, new Realm.Transaction.OnSuccess() {
                                                  @Override
                                                  public void onSuccess() {
                                                      // Original queries and Realm objects are automatically updated.
                                                      Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                                                      //realm.close();
                                                  }
                                              }, new Realm.Transaction.OnError() {
                                                  @Override
                                                  public void onError(Throwable error) {
                                                      Toast.makeText(getApplicationContext(), "Error on creating new user", Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                );*/
            }
        });

        Button mReturnUsers = (Button) findViewById(R.id.return_users);
        mReturnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RealmResults<User> results1 =
                        realm.where(User.class).findAll();

                for(User c:results1) {
                    Log.d("results1: ", c.getName());
                    Toast.makeText(getApplicationContext(), c.getUsername(), Toast.LENGTH_SHORT).show();
                }

                /*User user = RealmController.getInstance().getUser("username2");
                if (user != null) {
                    Toast.makeText(getApplicationContext(), user.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "User je null", Toast.LENGTH_SHORT).show();
                }*/

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        loadPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
            realm = null;
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(username) && !isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_to_short_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_to_short_password));
            focusView = mPasswordView;
            cancel = true;
        }



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);

            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

            //SyncCredentials creds = SyncCredentials.usernamePassword(username, password);
            SyncCredentials creds = SyncCredentials.usernamePassword(RealmController.REALM_USERNAME, RealmController.REALM_PASSWORD);
            SyncUser.Callback<SyncUser> callback = new SyncUser.Callback<SyncUser>() {
                @Override
                public void onSuccess(@Nonnull SyncUser user) {
                    progressDialog.dismiss();
                    final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(user, RealmController.REALM_URL_ROS).build();
                    Realm.setDefaultConfiguration(syncConfiguration);
                    realm = Realm.getDefaultInstance();

                    //realm = Realm.getInstance(syncConfiguration);
                    //syncConfig = syncConfiguration;
                    //syncUser = user;
                    try {
                        User userRealm = RealmController.getUser(username, password, realm, getApplicationContext());
                        if (userRealm != null)
                            onLoginSuccess(username, userRealm);
                        else
                            Toast.makeText(getApplicationContext(), "Username or password does not match", Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                    }
                    //onLoginSuccess();
                }

                @Override
                public void onError(@Nonnull ObjectServerError error) {
                    progressDialog.dismiss();
                    String errorMsg;
                    switch (error.getErrorCode()) {
                        case UNKNOWN_ACCOUNT:
                            errorMsg = "Account does not exists.";
                            break;
                        case INVALID_CREDENTIALS:
                            errorMsg = "User name and password does not match";
                            break;
                        default:
                            errorMsg = error.toString();
                    }
                    onLoginFailed(errorMsg);
                }
            };

            SyncUser.logInAsync(creds, RealmController.REALM_AUTH_URL, callback);

        }
    }

    /*private SyncConfiguration getSyncConfiguration(SyncUser syncUser, String realmURL) {
        SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(syncUser, realmURL)
                .waitForInitialRemoteData()
                .build();
        return syncConfiguration;
    }*/


    private void onLoginSuccess(String username, User user) {
        Toast.makeText(getApplicationContext(), "SUCCES!", Toast.LENGTH_SHORT).show();

        /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", result);
        startActivity(intent);
        finish();*/
        //Bundle bundle = new Bundle();
        //bundle.putParcelable("syncUser", (Parcelable) syncUser);
        savePreferences(username);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        //intent.putExtra("syncUser", bundle);
        startActivity(intent);
        //MainActivity.orderPlaced.setUser(user);
        MainActivity.user = user;
        finish();
    }

    private void onLoginFailed(String errorMsg) {
        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 3;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /*private void showStatus(String txt) {
        Log.i(TAG, txt);
    }*/

    /*private void basicCRUD(Realm realm) {
        showStatus("Perform basic Create/Read/Update/Delete (CRUD) operations...");

        // All writes must be wrapped in a transaction to facilitate safe multi threading
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a person
                User user = realm.createObject(User.class);
                user.setUsername("username");
                user.setPassword("passw");
                user.setName("name");
                user.setLastname("lastname");
                user.setPhone_number("00000");
            }
        });
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                // primanje usernema
                String result = data.getStringExtra("result");
                result = result.trim();
                if (!result.isEmpty())
                {
                    savePreferences(result);

                    //User user = null;

                    try {
                /* final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();*/

                        User user = RealmController.getUser(UnameValue, realm, getApplicationContext());

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", result);
                        //intent.putExtra("syncUser", bundle);
                        startActivity(intent);
                        //MainActivity.orderPlaced.setUser(user);
                        MainActivity.user = user;
                        finish();

                    } catch (Exception ex) {
                        Toast.makeText(getApplication(),"LoginActivity:" +  ex.toString(), Toast.LENGTH_SHORT).show();
                    }



                    /*Bundle bundle = new Bundle();
                    bundle.putParcelable("syncUser", (Parcelable) syncUser);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", result);
                    intent.putExtra("syncUser", bundle);
                    startActivity(intent);
                    finish();*/
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void savePreferences(String usernameToSave) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Edit and commit
        UnameValue = usernameToSave;
        editor.putString(PREF_UNAME, UnameValue);
        editor.putString(PREF_UNAME_ADMIN, "true");
        editor.commit();
    }

    private void loadPreferences() {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        // Get value
        UnameValue = settings.getString(PREF_UNAME, DefaultUnameValue);


        if (UnameValue != null && !UnameValue.isEmpty() && UnameValue.length() > 0)
        {
            //User user = null;

            try {
                /*if (SyncUser.currentUser() != null) {
                    final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
                    Realm.setDefaultConfiguration(syncConfiguration);
                    realm = Realm.getDefaultInstance();
                }*/

               /* if (Realm.getDefaultConfiguration().shouldDeleteRealmIfMigrationNeeded())
                {
                    Toast.makeText(getApplicationContext(), "Yes, you should delete realm", Toast.LENGTH_SHORT).show();
                    //Realm.deleteRealm(Realm.getDefaultConfiguration());
                }*/

                User user = RealmController.getUser(UnameValue, realm, getApplicationContext());

                /*SyncCredentials creds = SyncCredentials.usernamePassword(RealmController.REALM_USERNAME, RealmController.REALM_PASSWORD, false);
                SyncUser.login(creds, RealmController.REALM_AUTH_URL);*/

                if (user == null) {
                    final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
                    Realm.setDefaultConfiguration(syncConfiguration);
                    realm = Realm.getDefaultInstance();
                    realm.deleteAll();
                    return;
                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", UnameValue);
                startActivity(intent);
                //MainActivity.orderPlaced.setUser(user);
                MainActivity.user = user;
                finish();

            } catch (Exception ex) {
                Toast.makeText(getApplication(), "LoginActivity:" + ex.toString(), Toast.LENGTH_SHORT).show();
            }
        } /*else {
            Toast.makeText(getApplicationContext(), "Username is not loaded", Toast.LENGTH_SHORT).show();
        }*/
    }


}
