package elfak.diplomski.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import elfak.diplomski.MainActivity;
import elfak.diplomski.Model.CommentAndRating;
import elfak.diplomski.Model.OrderPlaced;
import elfak.diplomski.R;
import elfak.diplomski.Realm.RealmController;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Neca on 4.12.2017..
 */

public class ListAdapterOrders extends RecyclerView.Adapter<ListAdapterOrders.OrdersViewHolder> {

    //private List<OrderPlaced> mOrderPlaceds;
    private RealmResults<OrderPlaced> mOrderPlaceds;
    private volatile Realm realm;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout_showFullItem;
    private int hideListView = -1;
    private ScrollView scrollView;
    //private boolean enableDeleteBtn = false;
    /**
     * This LinkedList is used to map the position of the item and the image
     */
    protected LinkedList<Integer> drawableLinkedList;
    private Context mContext;
    //private int hideListView = -1;

    public ListAdapterOrders(final Context context, RealmResults<OrderPlaced> followerList, final LinkedList<Integer> drawableLinkedList, Realm realm, RelativeLayout relativeLayout, LinearLayout linearLayout_showFullItem, ScrollView scrollView) {
        this.mContext = context;
        this.mOrderPlaceds = followerList;
        this.drawableLinkedList = drawableLinkedList;
        this.realm = realm;
        this.relativeLayout = relativeLayout;
        this.linearLayout_showFullItem = linearLayout_showFullItem;
        this.scrollView = scrollView;
        //this.enableDeleteBtn = enableDeleteBtn;

        setListenerOnData();
    }

    public void setListenerOnData() {
        mOrderPlaceds.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<OrderPlaced>>() {
            @Override
            public void onChange(RealmResults<OrderPlaced> orderPlaceds, @Nullable OrderedCollectionChangeSet changeSet) {
                //Toast.makeText(mContext, Integer.toString(orderPlaceds.size()) + " == " + Integer.toString(mOrderPlaceds.size()), Toast.LENGTH_SHORT).show();
                if (orderPlaceds.size() != drawableLinkedList.size()) {
                    /*LinkedList<Integer> drawableLinkedList2 = new LinkedList<>(Collections.nCopies(orderPlaceds.size(),R.drawable.ic_action_details));
                    drawableLinkedList = drawableLinkedList2;*/
                    //LinkedList<Integer> drawableLinkedListCopy = new LinkedList<>(Collections.nCopies(orderPlaceds.size(),R.drawable.ic_action_details));
                    //drawableLinkedList = drawableLinkedListCopy;
                    drawableLinkedList =  new LinkedList<>(Collections.nCopies(orderPlaceds.size(),R.drawable.ic_action_details));
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBindViewHolder(ListAdapterOrders.OrdersViewHolder holder, final int position) {

        OrderPlaced orderPlaced = mOrderPlaceds.get(position);

        final int actionDrawableId = this.drawableLinkedList.get(position);

        if (orderPlaced.getUser() != null) {
            holder.username.setText(orderPlaced.getUser().getUsername());
            holder.fullName.setText(orderPlaced.getUser().getName() + " " + orderPlaced.getUser().getLastname());
        }
        else
        {
            holder.username.setText("This account is deleted");
            holder.fullName.setText("");
        }
        holder.status.setText("Status: " + orderPlaced.getStatus());
        holder.ratingBar.setVisibility(View.INVISIBLE);
        holder.date.setText(orderPlaced.getDateAndTime());
        holder.price.setText("Price: " + orderPlaced.getTotalPrice());
        holder.imageViewBtn.setImageResource(actionDrawableId);

        holder.imageViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMemberClick2(position, mOrderPlaceds, actionDrawableId);
            }
        });

        /*if (enableDeleteBtn || MainActivity.username.equals(commentAndRating.getUser().getUsername())) {
            holder.imageViewDel.setImageResource(actionDrawableId);
            holder.imageViewDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMemberClick2(position, mCommentAndRatings, actionDrawableId);
                }
            });
        } else {
            holder.imageViewDel.setVisibility(View.INVISIBLE);
        }*/


    }

    @Override
    public int getItemCount() {
        return mOrderPlaceds.size();
    }

    @Override
    public long getItemId(int position) {

        if (hideListView != -1) {
            int posReturn = hideListView;
            hideListView = -1;
            return posReturn;
        }

        return position;
    }

    @Override
    public ListAdapterOrders.OrdersViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.comment_and_rating_layout, viewGroup, false);

        return new ListAdapterOrders.OrdersViewHolder(itemView);
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewBtn;
        TextView username;
        RatingBar ratingBar;
        TextView fullName;
        TextView status;
        TextView date;
        TextView price;

        public OrdersViewHolder(View itemView) {
            super(itemView);

            username =  itemView.findViewById(R.id.username_comment_and_rating);
            ratingBar =  itemView.findViewById(R.id.ratingBar_comment_and_rating);
            fullName =  itemView.findViewById(R.id.foodName_comment_and_rating);
            status =  itemView.findViewById(R.id.comment_comment_and_rating);
            imageViewBtn =  itemView.findViewById(R.id.btnDelete_comment_and_rating);
            date = itemView.findViewById(R.id.date_comment_and_rating);
            price = itemView.findViewById(R.id.rating_text);
        }
    }

    protected void onMemberClick2(final int position, final List<OrderPlaced> followerList, int actionDrawableId) {
        //final CommentAndRating follower = followerList.get(position);
        switch (actionDrawableId) {
            case R.drawable.ic_action_delete:

                try {
                    alertDialogDelete(position);
                } catch (Exception ex) {
                    Toast.makeText(mContext, ex.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.drawable.ic_action_details:
                try {
                    hideListView = position;
                    relativeLayout.setVisibility(View.INVISIBLE);
                    linearLayout_showFullItem.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    //Toast.makeText(mContext, "CLICK DETAILS ", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(mContext, ex.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void alertDialogDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Delete comment ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        try {
                           /* RealmController.deleteCommentAndRating(realm, mContext, mCommentAndRatings.get(position).getUsernameAndFood());

                            LinkedList<Integer> drawableLinkedListCopy = new LinkedList<>(Collections.nCopies(mCommentAndRatings.size() - 1,R.drawable.ic_action_delete));
                            List<CommentAndRating> mComAndRatingsCopy = new ArrayList<>();

                            for (int i=0; i < mCommentAndRatings.size(); i++) {
                                if (i != position) {
                                    mComAndRatingsCopy.add(mCommentAndRatings.get(i));
                                }
                            }

                            drawableLinkedList = drawableLinkedListCopy;
                            mCommentAndRatings = mComAndRatingsCopy;
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


}
