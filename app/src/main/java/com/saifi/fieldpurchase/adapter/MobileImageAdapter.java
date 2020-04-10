package com.saifi.fieldpurchase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.saifi.fieldpurchase.R;
import com.saifi.fieldpurchase.model.ImageModel;


import java.util.ArrayList;

public class MobileImageAdapter extends RecyclerView.Adapter<MobileImageAdapter.ListViewHolder> {
    ArrayList<ImageModel> list;
    Context context;

    public MobileImageAdapter(ArrayList<ImageModel> list, Context context) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MobileImageAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_image, null);

        return new MobileImageAdapter.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MobileImageAdapter.ListViewHolder holder, final int position) {
        final ImageModel imageModel = list.get(position);
        holder.image.setImageBitmap(imageModel.getImageMobile());
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAt(position);
            }
        });
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView close;

        public ListViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.ivGallery);
            close = itemView.findViewById(R.id.badge_view);
        }

    }
}
