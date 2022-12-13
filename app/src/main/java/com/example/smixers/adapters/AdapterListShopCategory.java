package com.example.smixers.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smixers.R;
import com.example.smixers.models.ShopCategory;

import java.util.ArrayList;
import java.util.List;

public class AdapterListShopCategory extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private List<ShopCategory> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public AdapterListShopCategory(Context context, List<ShopCategory> items) {
        this.items = items;
        ctx = context;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_category, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, ShopCategory obj, int position);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.w("ListCreated","listCretaed9");
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            ShopCategory p = items.get(position);
            view.title.setText(p.title);
            view.brief.setText(p.brief);
            view.image.setImageResource(p.image);
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView brief;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            title = v.findViewById(R.id.title);
            brief = v.findViewById(R.id.brief);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }
}
