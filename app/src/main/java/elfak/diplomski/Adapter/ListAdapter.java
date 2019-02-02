package elfak.diplomski.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import elfak.diplomski.Model.Item;
import elfak.diplomski.R;
import elfak.diplomski.Realm.RealmController;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by Neca on 14.11.2017..
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.FoodViewHolder> {

    //private List<Item> mItemList;
    private RealmResults<Item> mItemList;
    private volatile Realm realm;
    /**
     * This LinkedList is used to map the position of the item and the image
     */
    //protected LinkedList<Integer> drawableLinkedList;
    private Context mContext;
    private RelativeLayout relativeLayout;
    private ConstraintLayout linearLayout_showFullItem;
    private int hideListView = -1;
    ///private LinkedList<String> itemsNameLinkedList;
    Map<String, Integer> nameDrawableList = new HashMap<String, Integer>();
    private int defaultDrawable = R.drawable.listview_add_touch;
    RealmResults<Item> itemBestSellers;

    public ListAdapter(Context context, RealmResults<Item> followerList, Map<String, Integer> nameDrawableList, RelativeLayout relativeLayout, ConstraintLayout linearLayout_showFullItem, Realm realm) {
        this.mContext = context;
        this.mItemList = followerList;
        //this.drawableLinkedList = drawableLinkedList;
        this.nameDrawableList = nameDrawableList;
        this.relativeLayout = relativeLayout;
        this.linearLayout_showFullItem = linearLayout_showFullItem;
        this.realm = realm;

        Iterator<Item> foodIterator;
        foodIterator = mItemList.iterator();
        foodIterator.hasNext();

        itemBestSellers = RealmController.getFood(realm, mContext, "", "Best sellers", 10);

        setListenerOnData();
    }

    public void setListenerOnData() {

        /*for (int i=0; i<mItemList.size(); i++) {
            //itemsNameLinkedList.add(mItemList.get(i).getName());
            nameDrawableList.put(mItemList.get(i).getName(), i);
        }*/

        if (nameDrawableList.containsValue(R.drawable.ic_action_delete)) {
            defaultDrawable = R.drawable.ic_action_delete;
        }

        mItemList.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Item>>() {
            @Override
            public void onChange(RealmResults<Item> items, @Nullable OrderedCollectionChangeSet changeSet) {

                /*if (mItemList.size() != drawableLinkedList.size()) {
                    // Option 1: Only add new object in HashMap (save time)
                    // Option 2: Make new HashMap (save memory)
                    /**
                     * Da se napravi lista string(food name), drawable i da se posalje kroz konstruktor
                     * Na osnovu pozicije moze da se nadje food name iz liste da bise doslo do drawable
                     * a drawable lista da se doda novi item ili izbrise iz lista ako je moguce
                     * **/

                    //nameDrawableList
                //}

                /*try {
                    if (MainActivity.orderPlaced.getItems() != null) {
                        for (int i = 0; i < MainActivity.orderPlaced.getItems().size(); i++) {
                            if (!items.contains(MainActivity.orderPlaced.getItems().get(i))) {
                                MainActivity.orderPlaced.getItems().deleteFromRealm(i);
                                //MainActivity.orderPlaced.getItems().
                                i--;
                            }
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(mContext, ex.toString(), Toast.LENGTH_SHORT).show();
                }*/


                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, final int position) {

        Item item = mItemList.get(position);
        //final int actionDrawableId = this.drawableLinkedList.get(position);

        int drawableId = defaultDrawable;
        if (nameDrawableList.containsKey(item.getName())) {
            drawableId = nameDrawableList.get(item.getName());
        }

        final int actionDrawableId = drawableId;

        holder.name.setText(item.getName());
        if (item.getRating() != null) {
            holder.rating.setText("Rating: " + String.valueOf(item.getRating()));
        } else {
            holder.rating.setText("Rating: unrated");
        }
        //Use Glide to load the Image
        //Glide.with(mContext).load(movie.getThumbnailUrl()).centerCrop().into(holder.thumbNail);
        holder.thumbNail.setImageBitmap(byteToBitmap(item.getImage()));
        holder.thumbNail.setScaleType(ImageView.ScaleType.FIT_XY);
        /*int newWidth = rowView.getResources().getDisplayMetrics().widthPixels;
        //int newHeight = (newWidth*bitmaps[position].getHeight())/bitmaps[position].getWidth();
        int newHeight = rowView.getResources().getDisplayMetrics().heightPixels;
        //holder.imageView.setImageBitmap(bitmaps[position]);
        holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmaps[position], (int)(newWidth * 0.4), (int)(newHeight * 0.2), true));
        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);*/
        // genre

        holder.describe.setText("Describe: " + item.getDescribe());
        holder.price.setText("Price: " + String.valueOf(item.getPrice()));

        if (item.getRecommended())
            holder.ivRecommendation.setVisibility(View.VISIBLE);
        else
            holder.ivRecommendation.setVisibility(View.INVISIBLE);
        if (item.getOnDiscount()) {
            holder.ivOnDiscount.setVisibility(View.VISIBLE);

            String textPrice = "Price: ";


            SpannableString spannable = new SpannableString(textPrice + String.valueOf(item.getPrice()) + "  " + item.getDiscountPrice());
            spannable.setSpan(new StrikethroughSpan(), textPrice.length(), textPrice.length() + String.valueOf(item.getPrice()).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            //remoteViews.setTextViewText(R.id.itemText, spannable);
            holder.price.setText(spannable, TextView.BufferType.SPANNABLE);
            //String text = "<strike><font color=\'#888888\'>" + String.valueOf(item.getPrice()) + "</font></strike>";
            //holder.price.setText("Price: " + spannable + "  " + item.getDiscountPrice(), TextView.BufferType.SPANNABLE);
        }
        else
            holder.ivOnDiscount.setVisibility(View.INVISIBLE);
        if (itemBestSellers != null && itemBestSellers.size() > 0)
        {
            for (int i = 0; i < itemBestSellers.size(); i++) {
                if (itemBestSellers.get(i).getName().equals(item.getName()))
                {
                    holder.ivBestSeller.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
        else
            holder.ivBestSeller.setVisibility(View.INVISIBLE);

        /**
         * Set OnClickListener on the Button.
         * We pass in 3 parameters:
         * @param position :Position of the object on the List
         * @param mMovieList Movie Object
         * @param actionDrawableId Drawable ID
         */
        holder.imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onMemberClick(position, mItemList, actionDrawableId);
                onMemberClick2(position, mItemList, actionDrawableId);
            }
        });
        //Set the Image Resource
        holder.imageViewAdd.setImageResource(actionDrawableId);

        holder.describe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Clicked!", Toast.LENGTH_SHORT).show();
                try {
                    hideListView = position;
                    relativeLayout.setVisibility(View.INVISIBLE);
                    linearLayout_showFullItem.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    Toast.makeText(mContext, ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Clicked!", Toast.LENGTH_SHORT).show();
                try {
                    hideListView = position;
                    relativeLayout.setVisibility(View.INVISIBLE);
                    linearLayout_showFullItem.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    Toast.makeText(mContext, ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.thumbNail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Clicked!", Toast.LENGTH_SHORT).show();
                try {
                    hideListView = position;
                    relativeLayout.setVisibility(View.INVISIBLE);
                    linearLayout_showFullItem.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    Toast.makeText(mContext, ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public long getItemId(int position) {
        //return position;
        if (hideListView != -1) {
            int posReturn = hideListView;
            hideListView = -1;
            return posReturn;
        }

        /*if (drawableLinkedList.get(position) == R.drawable.listview_added_touch){
            return position;
        } else {
            return -1;
        }*/

        if (nameDrawableList.get(mItemList.get(position).getName()) == R.drawable.listview_added_touch){
            return position;
        } else {
            return -1;
        }
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.food_listview_layout, viewGroup, false);

        return new FoodViewHolder(itemView);
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbNail;
        ImageView imageViewAdd;
        TextView name;
        TextView rating;
        TextView describe;
        TextView price;
        //Button button;
        ImageView ivOnDiscount;
        ImageView ivRecommendation;
        ImageView ivBestSeller;

        public FoodViewHolder(View itemView) {
            super(itemView);

            thumbNail = (ImageView) itemView.findViewById(R.id.thumbnail_listview);
            name = (TextView) itemView.findViewById(R.id.name_listview);
            rating = (TextView) itemView.findViewById(R.id.rating_listview);
            describe = (TextView) itemView.findViewById(R.id.describe_listview);
            price = (TextView) itemView.findViewById(R.id.price_listview);
            imageViewAdd = (ImageView) itemView.findViewById(R.id.btnAdd_listview);
            //button = (Button) itemView.findViewById(R.id.show_describe_listview);
            ivOnDiscount = itemView.findViewById(R.id.ivOnDiscount);
            ivRecommendation = itemView.findViewById(R.id.ivRecommendation);
            ivBestSeller = itemView.findViewById(R.id.ivBestSeller);
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

    /*protected void onMemberClick(final int position, final List<Item> followerList,int actionDrawableId) {
        final Item follower = followerList.get(position);
        switch (actionDrawableId) {
            case R.drawable.listview_add_touch:

                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        for (int i = 0; i < 1; i++) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Thread.interrupted();
                            }
                        }
                        return "Complete";
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        if (result.equals("Complete")) {
                            Toast.makeText(mContext, follower.getName(), Toast.LENGTH_SHORT).show();
                            drawableLinkedList.remove(position);
                            drawableLinkedList.add(position, R.drawable.listview_added_touch);
                            notifyDataSetChanged();

                        } else {
                            drawableLinkedList.remove(position);
                            drawableLinkedList.add(position, R.drawable.listview_error_touch);
                            Toast.makeText(mContext, mContext.getString(R.string.text_something_went_wrong),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
                break;

            case R.drawable.listview_added_touch:
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        for (int i = 0; i < 1; i++) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Thread.interrupted();
                            }
                        }
                        return "Complete";
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        if (result.equals("Complete")) {
                            drawableLinkedList.remove(position);
                            drawableLinkedList.add(position, R.drawable.listview_add_touch);
                            notifyDataSetChanged();
                        } else {
                            drawableLinkedList.remove(position);
                            drawableLinkedList.add(position, R.drawable.listview_error_touch);
                            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
                break;
            case R.drawable.listview_error_touch:
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        for (int i = 0; i < 1; i++) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Thread.interrupted();
                            }
                        }
                        return "Complete";
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        if (result.equals("Complete")) {
                            drawableLinkedList.remove(position);
                            drawableLinkedList.add(position, R.drawable.listview_added_touch);
                            notifyDataSetChanged();
                        } else {
                            drawableLinkedList.remove(position);
                            drawableLinkedList.add(position, R.drawable.listview_error_touch);
                            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
                break;
        }
    }*/

    protected void onMemberClick2(final int position, final List<Item> followerList, int actionDrawableId) {
        final Item follower = followerList.get(position);
        switch (actionDrawableId) {
            case R.drawable.listview_add_touch:
                            //Toast.makeText(mContext, follower.getName(), Toast.LENGTH_SHORT).show();
                            /*drawableLinkedList.remove(position);
                            drawableLinkedList.add(position, R.drawable.listview_added_touch);*/
                            nameDrawableList.put(follower.getName(), R.drawable.listview_added_touch);
                            notifyDataSetChanged();
                break;

            case R.drawable.listview_added_touch:
                            /*drawableLinkedList.remove(position);
                            drawableLinkedList.add(position, R.drawable.listview_add_touch);*/
                            nameDrawableList.put(follower.getName(),  R.drawable.listview_add_touch);
                            notifyDataSetChanged();
                break;
            case R.drawable.listview_error_touch:
                            /*drawableLinkedList.remove(position);
                            drawableLinkedList.add(position, R.drawable.listview_added_touch);*/
                            nameDrawableList.put(follower.getName(),  R.drawable.listview_added_touch);
                            notifyDataSetChanged();
            case R.drawable.ic_action_delete:

                try {
                    alertDialogDelete(position);
                } catch (Exception ex) {
                    Toast.makeText(mContext, ex.toString(), Toast.LENGTH_SHORT).show();
                }


               /* RealmController.deleteFoodOrDrink(realm, mContext, mItemList.get(position).getName());
                drawableLinkedList.remove(position);
                mItemList.remove(position);
                notifyDataSetChanged();*/
                break;
            default:
                /*drawableLinkedList.remove(position);
                drawableLinkedList.add(position, R.drawable.listview_error_touch);*/
                nameDrawableList.put(follower.getName(),  R.drawable.listview_error_touch);
                notifyDataSetChanged();
                break;
        }
    }

    private void alertDialogDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Delete item ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        try {
                           // RealmController.deleteFoodOrDrink(realm, mContext, mItemList.get(position).getName());

                            String objectName = mItemList.get(position).getName();
                            realm.beginTransaction();
                            mItemList.get(position).deleteFromRealm();
                            realm.commitTransaction();

                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, mItemList.size());
                            nameDrawableList.remove(objectName);
                            //notifyDataSetChanged();

                            // THIS IS WORKING
                           /* LinkedList<Integer> drawableLinkedListCopy = new LinkedList<>(Collections.nCopies(mItemList.size() - 1,R.drawable.ic_action_delete));
                            List<Item> mFoodListCopy = new ArrayList<>();

                            for (int i=0; i < mItemList.size(); i++) { //for (int i=0; i < mItemList.size() - 1; i++)
                                if (i != position) {
                                    mFoodListCopy.add(mItemList.get(i));
                                }
                            }

                            drawableLinkedList = drawableLinkedListCopy;
                            mItemList = mFoodListCopy;
                            notifyDataSetChanged();*/


                        } catch (Exception ex) {
                            Toast.makeText(mContext, ex.toString(), Toast.LENGTH_SHORT).show();
                        }


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

    public boolean isItemBestSeller(String itemName) {

        boolean isItemBestSeller = false;

        if (itemBestSellers != null && itemBestSellers.size() > 0)
        {
            for (int i = 0; i < itemBestSellers.size(); i++) {
                if (itemBestSellers.get(i).getName().equals(itemName))
                {
                    isItemBestSeller = true;
                    break;
                }
            }
        }

        return isItemBestSeller;
    }

}


