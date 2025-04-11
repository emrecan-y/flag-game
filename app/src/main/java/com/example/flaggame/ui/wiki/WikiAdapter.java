package com.example.flaggame.ui.wiki;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flaggame.Flag;
import com.example.flaggame.R;

import java.util.List;

public class WikiAdapter extends RecyclerView.Adapter<WikiAdapter.ViewHolder> {

    private List<Flag> flagList;

    public WikiAdapter(List<Flag> flagList) {
        this.flagList = flagList;
    }

    public void setFlagList(List<Flag> flagList) {
        this.flagList = flagList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wiki_single_flag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Flag flag = flagList.get(position);
        holder.imageView.setImageResource(flag.getDrawableResourceId());
        holder.textView.setText(flag.getCountryName());
    }

    @Override
    public int getItemCount() {
        return flagList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.flag_img);
            textView = itemView.findViewById(R.id.country_name);
        }
    }
}
