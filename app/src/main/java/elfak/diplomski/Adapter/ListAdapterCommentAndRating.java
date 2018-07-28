package elfak.diplomski.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import elfak.diplomski.MainActivity;
import elfak.diplomski.Model.CommentAndRating;
import elfak.diplomski.Model.Item;
import elfak.diplomski.R;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Neca on 28.11.2017..
 */

public class ListAdapterCommentAndRating extends RecyclerView.Adapter<ListAdapterCommentAndRating.CommentsAndRatingViewHolder> {

    private RealmResults<CommentAndRating> mCommentAndRatings;
    private volatile Realm realm;
    private boolean enableDeleteBtn = false;
    /**
     * This LinkedList is used to map the position of the item and the image
     */
    //protected LinkedList<Integer> drawableLinkedList;
    Map<String, Integer> nameDrawableList = new HashMap<String, Integer>();
    private Context mContext;
    //private int hideListView = -1;

    public ListAdapterCommentAndRating(Context context, RealmResults<CommentAndRating> followerList, Map<String, Integer> nameDrawableList, Realm realm, boolean enableDeleteBtn) {
        this.mContext = context;
        this.mCommentAndRatings = followerList;
        //this.drawableLinkedList = drawableLinkedList;
        this.nameDrawableList = nameDrawableList;
        this.realm = realm;
        this.enableDeleteBtn = enableDeleteBtn;

        setListenerOnData();
    }

    public void setListenerOnData() {

        mCommentAndRatings.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<CommentAndRating>>() {
            @Override
            public void onChange(RealmResults<CommentAndRating> commentAndRatings, @Nullable OrderedCollectionChangeSet changeSet) {
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onBindViewHolder(ListAdapterCommentAndRating.CommentsAndRatingViewHolder holder, final int position) {

        CommentAndRating commentAndRating = mCommentAndRatings.get(position);

        //final int actionDrawableId = this.drawableLinkedList.get(position);

        int drawableId = R.drawable.ic_action_delete;
        if (nameDrawableList.containsKey(commentAndRating.getUsernameAndFood())) {
            drawableId = nameDrawableList.get(commentAndRating.getUsernameAndFood());
        }

        final int actionDrawableId = drawableId;


        holder.username.setText(commentAndRating.getUser().getUsername());
        holder.foodName.setText(commentAndRating.getItem().getName());
        holder.comment.setText("Comment: " + commentAndRating.getComment());
        //holder.ratingBar.setRating(Float.parseFloat(commentAndRating.getRating()));
        holder.ratingBar.setRating((float) commentAndRating.getRating());
        holder.date.setText(commentAndRating.getDateAndTime());

        if (enableDeleteBtn || MainActivity.username.equals(commentAndRating.getUser().getUsername())) {
            holder.imageViewDel.setImageResource(actionDrawableId);
            holder.imageViewDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMemberClick2(position, mCommentAndRatings, actionDrawableId);
                }
            });
        } else {
            holder.imageViewDel.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mCommentAndRatings.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ListAdapterCommentAndRating.CommentsAndRatingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.comment_and_rating_layout, viewGroup, false);

        return new ListAdapterCommentAndRating.CommentsAndRatingViewHolder(itemView);
    }

    public static class CommentsAndRatingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewDel;
        TextView username;
        RatingBar ratingBar;
        TextView foodName;
        TextView comment;
        TextView date;

        public CommentsAndRatingViewHolder(View itemView) {
            super(itemView);

            username =  itemView.findViewById(R.id.username_comment_and_rating);
            ratingBar =  itemView.findViewById(R.id.ratingBar_comment_and_rating);
            foodName =  itemView.findViewById(R.id.foodName_comment_and_rating);
            comment =  itemView.findViewById(R.id.comment_comment_and_rating);
            imageViewDel =  itemView.findViewById(R.id.btnDelete_comment_and_rating);
            date = itemView.findViewById(R.id.date_comment_and_rating);
        }
    }

    protected void onMemberClick2(final int position, final List<CommentAndRating> followerList, int actionDrawableId) {
        //final CommentAndRating follower = followerList.get(position);
        switch (actionDrawableId) {
            case R.drawable.ic_action_delete:

                try {
                    alertDialogDelete(position);
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
                            //RealmController.deleteCommentAndRating(realm, mContext, mCommentAndRatings.get(position).getUsernameAndFood());

                            String objectName = mCommentAndRatings.get(position).getUsernameAndFood();
                            Item item =  mCommentAndRatings.get(position).getItem();

                            realm.beginTransaction();
                            mCommentAndRatings.get(position).deleteFromRealm();
                            realm.commitTransaction();

                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, mCommentAndRatings.size());
                            nameDrawableList.remove(objectName);


                            RealmResults<CommentAndRating> commentAndRatings = realm.where(CommentAndRating.class).equalTo("item.name", item.getName()).findAll();
                            realm.beginTransaction();
                            item.setRating(String.valueOf(commentAndRatings.average("rating")));
                            //realm.insertOrUpdate(item);
                            realm.commitTransaction();

                            // THIS IS WORKING
                            /*LinkedList<Integer> drawableLinkedListCopy = new LinkedList<>(Collections.nCopies(mCommentAndRatings.size() - 1,R.drawable.ic_action_delete));
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
