package elfak.diplomski.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import elfak.diplomski.Model.OrderPlaced;
import elfak.diplomski.R;

/**
 * Created by Neca on 19.11.2017..
 */

public class ListAdapterOrderPlaced extends RecyclerView.Adapter<ListAdapterOrderPlaced.OrderPlacedViewHolder>  {

     private Context mContext;
     private OrderPlaced mOrderPlaced;

    public ListAdapterOrderPlaced(Context context, OrderPlaced orderPlaced) {
        this.mContext = context;
        this.mOrderPlaced = orderPlaced;


    }

    @Override
    public void onBindViewHolder(final OrderPlacedViewHolder holder, final int position) {

        if (!mOrderPlaced.getItems().get(position).isValid()) {
            mOrderPlaced.getItems().remove(position);
            notifyDataSetChanged();
            return;
        }

        holder.name.setText(mOrderPlaced.getItems().get(position).getName());
        holder.menuCategory.setText("Category: " + mOrderPlaced.getItems().get(position).getMenuCategory().getCategory());
        holder.priceXQuantity.setText(mOrderPlaced.getItems().get(position).getPrice() + "   x" + Integer.toString(mOrderPlaced.getQuantitys().get(position)));
        String[] parts = mOrderPlaced.getItems().get(position).getPrice().split(" ");
        String fullPrice = String.valueOf((float) mOrderPlaced.getQuantitys().get(position) * Float.parseFloat(parts[0]) + " " + parts[1]);
        holder.totalPrice.setText(fullPrice);

        try {


        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderPlaced.getItems().remove(position);
                mOrderPlaced.getQuantitys().remove(position);
                notifyDataSetChanged();
            }
        });
        //Set the Image Resource
        holder.imageViewDelete.setImageResource(R.drawable.ic_action_delete);

        // quantity +
        holder.imageViewPlus.setVisibility(View.VISIBLE);
        holder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mOrderPlaced.getItems().get(position).isValid()) {
                    mOrderPlaced.getItems().remove(position);
                    notifyDataSetChanged();
                    return;
                }

                int quant = mOrderPlaced.getQuantitys().get(position) + 1;
                mOrderPlaced.getQuantitys().set(position, quant);
                String fullPrice = "";
                String[] parts = mOrderPlaced.getItems().get(position).getPrice().split(" ");
                fullPrice = String.valueOf((float) quant * Float.parseFloat(parts[0]) + " " + parts[1]);
                holder.priceXQuantity.setText(mOrderPlaced.getItems().get(position).getPrice() + "   x" + Integer.toString(mOrderPlaced.getQuantitys().get(position)));
                holder.totalPrice.setText(fullPrice);
                //notifyDataSetChanged();
            }
        });

        // quantity -
        holder.imageViewMinus.setVisibility(View.VISIBLE);
        holder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mOrderPlaced.getItems().get(position).isValid()) {
                    mOrderPlaced.getItems().remove(position);
                    notifyDataSetChanged();
                    return;
                }

                int quant = mOrderPlaced.getQuantitys().get(position) - 1;
                if (quant > 0) {
                    mOrderPlaced.getQuantitys().set(position, quant);
                    String fullPrice = "";
                    String[] parts = mOrderPlaced.getItems().get(position).getPrice().split(" ");
                    fullPrice = String.valueOf((float) quant * Float.parseFloat(parts[0]) + " " + parts[1]);
                    holder.priceXQuantity.setText(mOrderPlaced.getItems().get(position).getPrice() + "   x" + Integer.toString(mOrderPlaced.getQuantitys().get(position)));
                    holder.totalPrice.setText(fullPrice);
                    //notifyDataSetChanged();
                }
            }
        });

        } catch (Exception ex) {
            Toast.makeText(mContext, ex.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public int getItemCount() {
        return mOrderPlaced.getItems().size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public OrderPlacedViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.food_listview_layout, viewGroup, false);

        return new OrderPlacedViewHolder(itemView);
    }

    public static class OrderPlacedViewHolder extends RecyclerView.ViewHolder {
        //ImageView thumbNail;
        ImageView imageViewPlus;
        ImageView imageViewMinus;
        ImageView imageViewDelete;
        TextView name;
        TextView menuCategory;
        TextView priceXQuantity;
        TextView totalPrice;

        public OrderPlacedViewHolder(View itemView) {
            super(itemView);

            //thumbNail = (ImageView) itemView.findViewById(R.id.thumbnail_listview);
            name = (TextView) itemView.findViewById(R.id.name_listview);
            menuCategory = (TextView) itemView.findViewById(R.id.rating_listview);
            priceXQuantity = (TextView) itemView.findViewById(R.id.describe_listview);
            totalPrice = (TextView) itemView.findViewById(R.id.price_listview);
            imageViewDelete = (ImageView) itemView.findViewById(R.id.btnAdd_listview);
            imageViewPlus = (ImageView) itemView.findViewById(R.id.btnPlus_listview);
            imageViewMinus = (ImageView) itemView.findViewById(R.id.btnMinus_listview);
            //imageViewAdd.setImageBitmap(R.drawable.ic_action_delete);
            //button = (Button) itemView.findViewById(R.id.show_describe_listview);
        }
    }

}
