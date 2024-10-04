package com.example.signuploginrealtime.ui.sport;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.signuploginrealtime.databinding.FragmentExistingSportsBinding;

import java.util.List;

public class ExistingSportRecyclerViewAdapter extends RecyclerView.Adapter<ExistingSportRecyclerViewAdapter.ViewHolder> {
    static String LOG_TAG = "ExistingSportRecyclerViewAdapter";
    private List<Sport> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ExistingSportRecyclerViewAdapter(List<Sport> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    public void setItemList(List<Sport> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(FragmentExistingSportsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = itemList.get(position);
        holder.mIdView.setText(String.valueOf(position));
        String contentText = itemList.get(position).topic + " " + itemList.get(position).getSport();
        holder.mContentView.setText(contentText);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public Sport mItem;

        public ViewHolder(FragmentExistingSportsBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setOnClickListener(this::onClick);
            mIdView = binding.itemNumber;
            mContentView = binding.content;
        }

        public void onClick(View v) {
            Log.d(LOG_TAG, "onClickItem");
            int position = getAbsoluteAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position);
            }

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}