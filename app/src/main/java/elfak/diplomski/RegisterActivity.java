package elfak.diplomski;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.UserManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.annotation.Nonnull;

import elfak.diplomski.Model.User;
import elfak.diplomski.Realm.RealmController;
import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;
    private EditText et_password2;
    private EditText et_name;
    private EditText et_lastname;
    private EditText et_phonenumber;
    String response = null;
    private View mRegisterFormView;
    private View mProgressView;
    private boolean isMainActivity = false;
    //User myUser = null;
    private volatile Realm realm;
    //private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY); // full layout under action bar
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_register);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00212121")) );

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //get realm instance
        //this.realm = RealmController.with(this).getRealm();
        this.realm = RealmController.getInstance().getRealm();

       /* User user = RealmController.getInstance().getUser("username2");
        if (user != null) {
            Toast.makeText(getApplicationContext(), user.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "User je null", Toast.LENGTH_SHORT).show();
        }*/

        //getSupportActionBar().hide(); //<< this
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_username = (EditText) findViewById(R.id.username_register);
        et_password = (EditText) findViewById(R.id.password_register);
        et_password2 = (EditText) findViewById(R.id.password2_register);
        et_name = (EditText) findViewById(R.id.name_register);
        et_lastname = (EditText) findViewById(R.id.lastname_register);
        et_phonenumber = (EditText) findViewById(R.id.phoneNumber_register);

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        Button registerButton = (Button) findViewById(R.id.createProfile_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptCreateProfile();
                if (isMainActivity) {
                    attemptCreateProfile();
                } else {
                    RealmLoginDefaultUser();
                }
            }
        });

        String username = "";
        String name = "";
        String lastname = "";
        String password = "";
        String phoneNumber = "";
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            name = intent.getStringExtra("name");
            lastname = intent.getStringExtra("lastname");
            password = intent.getStringExtra("password");
            phoneNumber = intent.getStringExtra("phone_number");
        }

        if (username != null && !username.equals("")) {
            isMainActivity = true;
            et_username.setText(username);
            et_username.setEnabled(false);
            et_name.setText(name);
            et_lastname.setText(lastname);
            et_password.setText(password);
            //et_password2.setText(password);
            et_phonenumber.setText(phoneNumber);
            registerButton.setText("Edit profile");
            setTitle("Edit my profile");
        } else {
            //RealmLoginDefaultUser();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (realm != null) {
            realm.close();
            realm = null;
        }*/
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (isMainActivity) {
            Intent returnIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            setResult(RESULT_CANCELED,returnIntent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void attemptCreateProfile() {

        // Store values at the time of the login attempt.
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        String password2 = et_password2.getText().toString();
        String name = et_name.getText().toString();
        String lastName = et_lastname.getText().toString();
        String phoneNumber = et_phonenumber.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(username)) {
            et_username.setError(getString(R.string.error_invalid_username));
            focusView = et_username;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            et_password.setError(getString(R.string.error_invalid_password));
            focusView = et_password;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            et_password.setError(getString(R.string.error_invalid_to_short_password));
            focusView = et_password;
            cancel = true;
        }

        // TREBA PROVERITI DA LI MOZE OVAKO
        // Check for a valid password2, if the user entered one.
        if (!password.equals(password2)) {
            et_password2.setError(getString(R.string.error_invalid_password2));
            focusView = et_password2;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
            else
                Toast.makeText(RegisterActivity.this, "Select your avatar!", Toast.LENGTH_SHORT).show();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            final User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setName(name);
            user.setLastname(lastName);
            user.setPhone_number(phoneNumber);
            //user.setImage(bitmap);
            //String image = getStringImage(bitmap);
            // user.setImage(image);
            //user.setCreated(mydate);

            // Ako nije main activity
            if (!isMainActivity)
            {
               /* myUser = user;
                showProgress(true);
                CheckUsernameExistTask checkUsernameExistTask = new CheckUsernameExistTask(user.getUsername(), realm);
                checkUsernameExistTask.execute((Void) null);*/
               //RealmLoginDefaultUser();

                User userExist = RealmController.getUser(user.getUsername(), realm, getApplicationContext());

                if (userExist == null) {
                    //myUser = user;
                    showProgress(true);
                    //LoginTask(user);
                    //mAuthTask = new UserLoginTask(user, "true");
                    //mAuthTask.execute((Void) null);
                    //RealmController.addUser(user, realm, getApplicationContext());

                    createUser(user);

                } else {
                    //showProgress(false);
                    Toast.makeText(getApplicationContext(), "Username is already taken!", Toast.LENGTH_SHORT).show();
                }


            }
            else // Ako jeste main activity
            {
                showProgress(true);
                createUser(user);
            }
        }
    }

    private void createUser(final User user) {
        try {

            if (isMainActivity) {
                final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
                Realm.setDefaultConfiguration(syncConfiguration);
                realm = Realm.getDefaultInstance();
            }

            realm.executeTransactionAsync(new Realm.Transaction() {
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

                                                  //realm.close();

                                                  if (!isMainActivity) {
                                                      Toast.makeText(getApplicationContext(), "Created", Toast.LENGTH_SHORT).show();
                                                      Intent returnIntent = new Intent();
                                                      returnIntent.putExtra("result", user.getUsername());
                                                      setResult(Activity.RESULT_OK, returnIntent);
                                                      finish();
                                                  } else {
                                                      Toast.makeText(getApplicationContext(), "Edited", Toast.LENGTH_SHORT).show();
                                                      finish();
                                                  }
                                              }
                                          }, new Realm.Transaction.OnError() {
                                              @Override
                                              public void onError(Throwable error) {
                                                  Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                              }
                                          }
            );
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        } /*finally {
           if(realm != null) {
               realm.close();
           }
        }*/

    }

    private void RealmLoginDefaultUser() {
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
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
                //syncUser = user;
                onLoginSuccess();
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


    private void onLoginSuccess() {
        //***Toast.makeText(getApplicationContext(), "Success default login!", Toast.LENGTH_SHORT).show();
        attemptCreateProfile();
    }

    private void onLoginFailed(String errorMsg) {
        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }


    private void RealmCreateUser(User user) {
        showProgress(true);
        SyncUser.logInAsync(SyncCredentials.usernamePassword(user.getUsername(), user.getPassword(), true), RealmController.REALM_AUTH_URL, new SyncUser.Callback<SyncUser>() {
            @Override
            public void onSuccess(SyncUser user) {
                registrationComplete(user);
            }

            @Override
            public void onError(ObjectServerError error) {
                showProgress(false);
                String errorMsg;
                switch (error.getErrorCode()) {
                    case EXISTING_ACCOUNT: errorMsg = "Account already exists"; break;
                    default:
                        errorMsg = error.toString();
                }
                Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registrationComplete(SyncUser user) {
        /*UserManager.setActiveUser(user);
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



}
