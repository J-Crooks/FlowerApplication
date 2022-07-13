package com.example.flowerapplication;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FlowerItemAdapator extends RecyclerView.Adapter<FlowerItemAdapator.ViewHolder> {
    private List<FlowerItem> items;
    private OnItemListener listener;

    public  FlowerItemAdapator(List<FlowerItem> items, OnItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context ctx = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.flower_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        //Gets each item and assign each value to a unique viewholder
        FlowerItem item = items.get(i);
        TextView dataitem_tv = viewHolder.dataitem_tv;
        dataitem_tv.setText(item.getName());

        ImageView dataitem_img = viewHolder.dataitem_img;
        byte[] decodedString = Base64.decode(item.getImgEncode(), Base64.DEFAULT);
        dataitem_img.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Class for each data items UI and variables
        public ImageView dataitem_img;
        public TextView dataitem_tv;
        OnItemListener onListener;

        public ViewHolder(@NonNull View itemView, OnItemListener onListener) {
            super(itemView);
            dataitem_img = itemView.findViewById(R.id.dataitemn_img);
            dataitem_tv = itemView.findViewById(R.id.dataitem_name);
            this.onListener = onListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
