package elfak.diplomski.Realm;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import elfak.diplomski.BuildConfig;
import elfak.diplomski.Model.CommentAndRating;
import elfak.diplomski.Model.Item;
import elfak.diplomski.Model.MenuCategory;
import elfak.diplomski.Model.OrderPlaced;
import elfak.diplomski.Model.User;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

/**
 * Created by Neca on 25.10.2017..
 */

public class RealmController {
    private static RealmController instance;
    private final Realm realm;

    //public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/default";
    public static final String REALM_AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    //public static final String REALM_URL_USERS = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/Users";
    //public static final String REALM_URL_CATEGORY = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/Category";
    //public static final String REALM_URL_FOOD = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/Item";
    //public static final String REALM_URL_DRINK = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/Drink";
    public static final String REALM_URL_ROS = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/ROS5";
    public static final String REALM_USERNAME = "golden";
    public static final String REALM_PASSWORD = "golden";

    public RealmController(Application application) {
        // Initialize Realm. Should only be done once when the application starts.

        // If dont work then uncomment this
        //Realm.init(application.getApplicationContext());

        // Create the Realm instance
        realm = Realm.getDefaultInstance();

    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }



    public static void addUser(final User user, Realm realm, final Context context) {
       /* try {
            realm.beginTransaction();
            realm.insertOrUpdate(user);
            realm.commitTransaction();
            return true;
        } catch (Exception ex) {
            return false;
        }*/

       try {
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
                                                 Toast.makeText(context, "Created", Toast.LENGTH_SHORT).show();
                                                 //realm.close();
                                             }
                                         }, new Realm.Transaction.OnError() {
                                             @Override
                                             public void onError(Throwable error) {
                                                 Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                             }
                                         }
           );
       } catch (Exception ex) {
           Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
       } finally {
           /*if(realm != null) {
               realm.close();
           }*/
       }

    }

    //clear all objects from User.class
    public void clearAllUsers() {

        realm.beginTransaction();
        realm.delete(User.class);
        realm.commitTransaction();
    }

    //find all objects in the User.class
    public RealmResults<User> getUsers() {

        return realm.where(User.class).findAll();
    }

    //query a single item with the given id
    public static User getUser(String username, Realm realm, Context context) {

        //if (realm == null || realm.isClosed()) {
            final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();

       /* DynamicRealm dynamicRealm = DynamicRealm.getInstance(syncConfiguration);
        dynamicRealm.getVersion();

        if (dynamicRealm.getVersion() != -1)
        {
            int da = 4 +5;
        }*/
      //  }

        User user = null;

        try {
            user = realm.where(User.class).equalTo("username", username).findFirst();
        } catch (Exception ex) {
            Toast.makeText(context,"RealmController: " + ex.toString(), Toast.LENGTH_LONG).show();
        } /*finally {
            if(realm != null && !realm.isClosed()) {
                realm.close();
            }
        }*/
        return user;
    }

    public static User getUser(String username, String password, Realm realm, Context context) {
        User user = null;
        try {
            user = realm.where(User.class).equalTo("username", username).equalTo("password", password).findFirst();
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
        return user;
    }

    public static void deleteUser(Realm realm, final Context context, final String username) {
        try {
            realm.executeTransactionAsync(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm bgRealm) {
                                                  bgRealm.where(CommentAndRating.class).equalTo("user.username", username).findAll().deleteAllFromRealm(); // brisemo sve njegove komentare
                                                  /*** Ako treba obrisati narudzbine ovo odkomentarisati ***/
                                                  //bgRealm.where(OrderPlaced.class).equalTo("user.username", username).findAll().deleteAllFromRealm(); // brisemo sve njegove narudzbine
                                                  bgRealm.where(User.class).equalTo("username", username).findAll().deleteAllFromRealm(); // a zatim brisemo njegov profil
                                              }
                                          }, new Realm.Transaction.OnSuccess() {
                                              @Override
                                              public void onSuccess() {
                                                  // Original queries and Realm objects are automatically updated.
                                                  Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                                                  //realm.close();
                                              }
                                          }, new Realm.Transaction.OnError() {
                                              @Override
                                              public void onError(Throwable error) {
                                                  Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                              }
                                          }
            );
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if(realm != null && !realm.isClosed()) {
                realm.close();
            }
        }
    }

    //check if User.class is empty
    public boolean hasUsers() {

        return !realm.where(User.class).findAll().isEmpty();
    }

    //query example
    /*public RealmResults<User> queryedBooks() {

        return realm.where(User.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                .findAll();
    }*/

    //find all objects in the MenuCategory.class
    public static RealmResults<MenuCategory> getMenuCategories(Realm realm, Context context) {
        RealmResults<MenuCategory> realmResults = null;

        try {
            realmResults = realm.where(MenuCategory.class).equalTo("visibility", true).findAll();
            if (realmResults != null) {
                realmResults = realmResults.sort("sortNumber", Sort.ASCENDING);
            }
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
        return realmResults;
    }

    public static RealmResults<Item> getFood(Realm realm, Context context, String category, String categoryType, int numberOfItemsInBestSeller) {
        RealmResults<Item> realmResults = null;

        try {
            if (categoryType.equals("Classic"))
                realmResults = realm.where(Item.class).equalTo("menuCategory.category", category).findAll();
            else if (categoryType.equals("Recommended"))
                realmResults = realm.where(Item.class).equalTo("recommended", true).findAll();
            else if (categoryType.equals("On discount"))
                realmResults = realm.where(Item.class).equalTo("onDiscount", true).findAll();
            else if (categoryType.equals("Best sellers")) {
                realmResults = realm.where(Item.class).greaterThan("numberTimesOrdered", 0).findAll().sort("numberTimesOrdered", Sort.DESCENDING);
                if (numberOfItemsInBestSeller != 0) {
                    /** NE MOZE SE KASTUJE **/
                    realmResults = (RealmResults<Item>) realmResults.subList(0, numberOfItemsInBestSeller);
                }
            }
            if (realmResults != null && !categoryType.equals("Best sellers")) {
                realmResults = realmResults.sort("name", Sort.ASCENDING);
            }
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
        return realmResults;
    }

    public static Item getItem(Realm realm, Context context, String itemName)
    {
        Item item = null;

        try {
            item = realm.where(Item.class).equalTo("name", itemName).findFirst();
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
        return item;
    }

    public static void deleteFoodOrDrink(Realm realm, final Context context, final String name) {
        //Realm realm = null;
        try {
            /*final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();*/

            realm.executeTransactionAsync(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm bgRealm) {
                                                  bgRealm.where(Item.class).equalTo("name", name).findAll().deleteAllFromRealm();
                                                  //uploadData();
                                                  //realm.insertOrUpdate(user);
                                              }
                                          }, new Realm.Transaction.OnSuccess() {
                                              @Override
                                              public void onSuccess() {
                                                  // Original queries and Realm objects are automatically updated.
                                                  Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                                                  //realm.close();
                                              }
                                          }, new Realm.Transaction.OnError() {
                                              @Override
                                              public void onError(Throwable error) {
                                                  Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                              }
                                          }
            );
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if(realm != null && !realm.isClosed()) {
                realm.close();
            }
        }
    }

    public static void deleteAllFoodWithNullCategory(final Realm realm, final Context context) {
        try {
            /*final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();*/

            //final RealmResults<Item> foods = realm.where(Item.class).equalTo("menuCategory.category", category).findAll();
            //final RealmResults<MenuCategory> menuCategories = realm.where(MenuCategory.class).equalTo("category", category).findAll();

            realm.executeTransactionAsync(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm bgRealm) {

                                                  //bgRealm.where(CommentAndRating.class).equalTo("food", usernameAndFood).findAll().deleteAllFromRealm();

                                                  bgRealm.where(Item.class).isNull("menuCategory").findAll().deleteAllFromRealm();

                                                  /*bgRealm.where(Item.class).equalTo("menuCategory.category", null).findAll();//.deleteAllFromRealm();

                                                  for (int i= 0; i < foods.size(); i++) {
                                                      foods.get(i).getFoodCoomments().deleteAllFromRealm();
                                                  }
                                                  foods.deleteAllFromRealm();

                                                  bgRealm.where(MenuCategory.class).equalTo("category", category).findAll().deleteAllFromRealm();*/

                                              }
                                          }, new Realm.Transaction.OnSuccess() {
                                              @Override
                                              public void onSuccess() {
                                                  // Original queries and Realm objects are automatically updated.
                                                  //Toast.makeText(context, "Successfully deleted category and all items", Toast.LENGTH_SHORT).show();
                                                  deleteAllCommentsWithNullFood(realm, context);
                                              }
                                          }, new Realm.Transaction.OnError() {
                                              @Override
                                              public void onError(Throwable error) {
                                                  Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                              }
                                          }
            );
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        } /*finally {
            if(realm != null && !realm.isClosed()) {
                realm.close();
            }
        }*/
    }

    public static void deleteAllCommentsWithNullFood(final Realm realm, final Context context) {
        try {
            /*final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();*/

            //final RealmResults<Item> foods = realm.where(Item.class).equalTo("menuCategory.category", category).findAll();
            //final RealmResults<MenuCategory> menuCategories = realm.where(MenuCategory.class).equalTo("category", category).findAll();

            realm.executeTransactionAsync(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm bgRealm) {

                                                  //bgRealm.where(CommentAndRating.class).equalTo("food", usernameAndFood).findAll().deleteAllFromRealm();

                                                  bgRealm.where(CommentAndRating.class).isNull("item").findAll().deleteAllFromRealm();

                                                  /*bgRealm.where(Item.class).equalTo("menuCategory.category", null).findAll();//.deleteAllFromRealm();

                                                  for (int i= 0; i < foods.size(); i++) {
                                                      foods.get(i).getFoodCoomments().deleteAllFromRealm();
                                                  }
                                                  foods.deleteAllFromRealm();

                                                  bgRealm.where(MenuCategory.class).equalTo("category", category).findAll().deleteAllFromRealm();*/

                                              }
                                          }, new Realm.Transaction.OnSuccess() {
                                              @Override
                                              public void onSuccess() {
                                                  // Original queries and Realm objects are automatically updated.
                                                  //Toast.makeText(context, "Successfully deleted category and all items", Toast.LENGTH_SHORT).show();
                                              }
                                          }, new Realm.Transaction.OnError() {
                                              @Override
                                              public void onError(Throwable error) {
                                                  Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                              }
                                          }
            );
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if(realm != null && !realm.isClosed()) {
                realm.close();
            }
        }
    }

    public static void deleteCategoryAndAllItems(Realm realm, final Context context, final String category) {
       // Realm realm = null;
        try {
            /*final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();*/

            //final RealmResults<Item> foods = realm.where(Item.class).equalTo("menuCategory.category", category).findAll();
            //final RealmResults<MenuCategory> menuCategories = realm.where(MenuCategory.class).equalTo("category", category).findAll();

            realm.executeTransactionAsync(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm bgRealm) {

                                                  //bgRealm.where(CommentAndRating.class).equalTo("food", usernameAndFood).findAll().deleteAllFromRealm();

                                                  RealmResults<Item> items = bgRealm.where(Item.class).equalTo("menuCategory.category", category).findAll();//.deleteAllFromRealm();

                                                  for (int i = 0; i < items.size(); i++) {
                                                      items.get(i).getFoodCoomments().deleteAllFromRealm();
                                                  }
                                                  items.deleteAllFromRealm();

                                                  bgRealm.where(MenuCategory.class).equalTo("category", category).findAll().deleteAllFromRealm();
                                                  //items.deleteAllFromRealm();
                                                  //menuCategories.deleteAllFromRealm();
                                              }
                                          }, new Realm.Transaction.OnSuccess() {
                                              @Override
                                              public void onSuccess() {
                                                  // Original queries and Realm objects are automatically updated.
                                                  Toast.makeText(context, "Successfully deleted category and all items", Toast.LENGTH_SHORT).show();
                                                  //deleteCategory(context, category);
                                                  //realm.close();
                                              }
                                          }, new Realm.Transaction.OnError() {
                                              @Override
                                              public void onError(Throwable error) {
                                                  Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                              }
                                          }
            );
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if(realm != null && !realm.isClosed()) {
                realm.close();
            }
        }
    }

    public static void deleteCategory(final Context context, final String category) {
        Realm realm = null;
        try {
            final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();

            realm.executeTransactionAsync(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm bgRealm) {

                                                  bgRealm.where(MenuCategory.class).equalTo("category", category).findAll().deleteAllFromRealm();
                                                  //uploadData();
                                                  //realm.insertOrUpdate(user);
                                              }
                                          }, new Realm.Transaction.OnSuccess() {
                                              @Override
                                              public void onSuccess() {
                                                  // Original queries and Realm objects are automatically updated.
                                                  Toast.makeText(context, "Successfully deleted category", Toast.LENGTH_SHORT).show();
                                                  //realm.close();
                                              }
                                          }, new Realm.Transaction.OnError() {
                                              @Override
                                              public void onError(Throwable error) {
                                                  Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                              }
                                          }
            );
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if(realm != null) {
                realm.close();
            }
        }
    }

    public static void addCommentAndRating(final Realm realm, final Context context, final CommentAndRating commentAndRating) {
        try {

            /*final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();*/



            realm.executeTransactionAsync(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm bgRealm) {
                                                  bgRealm.insertOrUpdate(commentAndRating);
                                                  /*Item food = commentAndRating.getItem();
                                                  RealmResults<CommentAndRating> commentAndRatings = bgRealm.where(CommentAndRating.class).equalTo("food.name", food.getName()).findAll();
                                                  food.setRating(String.valueOf(commentAndRatings.average("rating")));
                                                  bgRealm.insertOrUpdate(food);*/
                                              }
                                          }, new Realm.Transaction.OnSuccess() {
                                              @Override
                                              public void onSuccess() {
                                                  // Original queries and Realm objects are automatically updated.
                                                  try {
                                                      Item item = commentAndRating.getItem();
                                                      RealmResults<CommentAndRating> commentAndRatings = realm.where(CommentAndRating.class).equalTo("item.name", item.getName()).findAll();
                                                      realm.beginTransaction();
                                                      //item.setRating(String.valueOf(commentAndRatings.average("rating")));
                                                      item.setRating(String.valueOf(new BigDecimal(commentAndRatings.average("rating")).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()));
                                                      //realm.insertOrUpdate(item);
                                                      realm.commitTransaction();
                                                      Toast.makeText(context, "Successfully added comment and rating", Toast.LENGTH_SHORT).show();
                                                  } catch (Exception ex) {
                                                      Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
                                                  }

                                              }
                                          }, new Realm.Transaction.OnError() {
                                              @Override
                                              public void onError(Throwable error) {
                                                  Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                              }
                                          }
            );
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if(realm != null && !realm.isClosed()) {
                realm.close();
            }
        }
    }

    public static RealmResults<CommentAndRating> getCommentsAndRatings(Realm realm, final Context context, final String itemName) {
        RealmResults<CommentAndRating> realmResults = null;

        try {
            realmResults = realm.where(CommentAndRating.class).equalTo("item.name", itemName).findAll();
            if (realmResults != null) {
                realmResults = realmResults.sort("dateAndTime", Sort.DESCENDING);
            }
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
        return realmResults;
    }

    /*public static void updateRating(Realm realm, final Context context, final String foodName) {

        try {
            Item food = realm.where(Item.class).equalTo("name", foodName).findFirst();
            RealmResults<CommentAndRating> commentAndRatings = realm.where(CommentAndRating.class).equalTo("food.name", food.getName()).findAll();
            realm.beginTransaction();
            food.setRating(String.valueOf(commentAndRatings.average("rating")));
            //realm.insertOrUpdate(food);
            realm.commitTransaction();
            //Toast.makeText(context, "Successfully added comment and rating", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }*/


    public static RealmResults<CommentAndRating> getMyCommentsAndRatings(Realm realm, final Context context, final String username) {
        RealmResults<CommentAndRating> realmResults = null;

        try {
            realmResults = realm.where(CommentAndRating.class).equalTo("user.username", username).findAll();
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
        return realmResults;
    }

    public static void deleteCommentAndRating(Realm realm, final Context context, final String usernameAndFood) {
        try {

           /* if (realm == null || realm.isClosed()) {
                final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
                Realm.setDefaultConfiguration(syncConfiguration);
                realm = Realm.getDefaultInstance();
            }*/
            /*final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), RealmController.REALM_URL_ROS).build();
            Realm.setDefaultConfiguration(syncConfiguration);
            realm = Realm.getDefaultInstance();*/

            realm.executeTransactionAsync(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm bgRealm) {

                                                  bgRealm.where(CommentAndRating.class).equalTo("usernameAndFood", usernameAndFood).findAll().deleteAllFromRealm();

                                              }
                                          }, new Realm.Transaction.OnSuccess() {
                                              @Override
                                              public void onSuccess() {
                                                  Toast.makeText(context, "Successfully deleted comment", Toast.LENGTH_SHORT).show();
                                              }
                                          }, new Realm.Transaction.OnError() {
                                              @Override
                                              public void onError(Throwable error) {
                                                  Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                              }
                                          }
            );
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if(realm != null && !realm.isClosed()) {
                realm.close();
            }
        }
    }

    public static void addOrderPlaced(Realm realm, final Context context, final OrderPlaced orderPlaced) {
        try {

            if (realm == null || realm.isClosed()) {
                final SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.current(), RealmController.REALM_URL_ROS).build();
                Realm.setDefaultConfiguration(syncConfiguration);
                realm = Realm.getDefaultInstance();
            }


            final Realm finalRealm = realm;
            realm.executeTransactionAsync(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm bgRealm) {
                                                  bgRealm.insertOrUpdate(orderPlaced);


                                              }
                                          }, new Realm.Transaction.OnSuccess() {
                                              @Override
                                              public void onSuccess() {
                                                  // Original queries and Realm objects are automatically updated.

                                                  try {
                                                      /*final Item item = finalRealm.where(Item.class).equalTo("name", "Item1").findFirst();
                                                      finalRealm.beginTransaction();
                                                      long test = item.numberTimesOrdered.get();
                                                      item.numberTimesOrdered.increment(1);
                                                      test = item.numberTimesOrdered.get();
                                                      item.numberTimesOrdered.set(3);
                                                      item.numberTimesOrdered.increment(5);
                                                      test = item.numberTimesOrdered.get();
                                                      //item.setRating("3");
                                                      finalRealm.commitTransaction();
                                                      /*OrderPlaced order = finalRealm.where(OrderPlaced.class).equalTo("usernameAndDateTime", orderPlaced.getUsernameAndDateTime()).findFirst();
                                                      finalRealm.beginTransaction();
                                                      for (int i =0; i < order.getItems().size(); i++)
                                                      {
                                                          Item item = order.getItems().get(i);
                                                          item.numberTimesOrdered.increment(1);
                                                          finalRealm.insertOrUpdate( item);
                                                      }
                                                      finalRealm.commitTransaction();*/
                                                      //Toast.makeText(context, "Successfully ordered", Toast.LENGTH_SHORT).show();
                                                  } catch (Exception ex) {
                                                      Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
                                                  }


                                                  Toast.makeText(context, "Successfully ordered", Toast.LENGTH_SHORT).show();

                                              }
                                          }, new Realm.Transaction.OnError() {
                                              @Override
                                              public void onError(Throwable error) {
                                                  Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                                              }
                                          }
            );

        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if(realm != null && !realm.isClosed()) {
                realm.close();
            }
        }
    }

    public static RealmResults<OrderPlaced> getMyOrders(Realm realm, final Context context, final String username) {
        RealmResults<OrderPlaced> realmResults = null;

        try {
            realmResults = realm.where(OrderPlaced.class).equalTo("user.username", username).findAll();
            /*if (realmResults != null) {
                realmResults = realmResults.sort("dateAndTime", Sort.DESCENDING);
            }*/
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
        return realmResults;
    }

    public static RealmResults<OrderPlaced> getMyNotCompletedOrders(Realm realm, final Context context, final String username) {
        RealmResults<OrderPlaced> realmResults = null;

        try {
            realmResults = realm.where(OrderPlaced.class).equalTo("user.username", username).notEqualTo("status", "Completed").or().notEqualTo("status", "Rejected").findAll();
            /*if (realmResults != null) {
                realmResults = realmResults.sort("dateAndTime", Sort.DESCENDING);
            }*/
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
        return realmResults;
    }

    public static RealmResults<OrderPlaced> getAllOrders(Realm realm, final Context context) {
        RealmResults<OrderPlaced> realmResults = null;

        try {
            realmResults = realm.where(OrderPlaced.class).findAll();
            /*if (realmResults != null) {
                realmResults = realmResults.sort("dateAndTime", Sort.DESCENDING);
            }*/
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
        return realmResults;
    }

}

