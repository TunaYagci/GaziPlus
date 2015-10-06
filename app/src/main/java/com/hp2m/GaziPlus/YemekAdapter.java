package com.hp2m.GaziPlus;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class YemekAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int EMPTY_VIEW = 10;
    List<YemekInformation> data = Collections.emptyList();
    private LayoutInflater inflater;

    public YemekAdapter(Context context, List<YemekInformation> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == EMPTY_VIEW) {
            View view = inflater.inflate(R.layout.empty_view, parent, false);
            EmptyViewHolder emptyViewHolder = new EmptyViewHolder(view);
            return emptyViewHolder;
        } else {
            View view = inflater.inflate(R.layout.custom_row_yemek, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyViewHolder) {
            YemekInformation current = data.get(position);
            ((MyViewHolder) holder).c1.setCardBackgroundColor(current.cardColor);
            ((MyViewHolder) holder).h1.setText(current.header);
            ((MyViewHolder) holder).t1.setText(current.yemek1);
            ((MyViewHolder) holder).t2.setText(current.yemek2);
            ((MyViewHolder) holder).t3.setText(current.yemek3);
            ((MyViewHolder) holder).t4.setText(current.yemek4);
        }


    }

    @Override
    public int getItemCount() {
        return data.size() > 0 ? data.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView h1, t1, t2, t3, t4; //header, text
        CardView c1;

        public MyViewHolder(View itemView) {
            super(itemView);
            c1 = (CardView) itemView.findViewById(R.id.cardView);
            h1 = (TextView) itemView.findViewById(R.id.header);
            t1 = (TextView) itemView.findViewById(R.id.yemek1);
            t2 = (TextView) itemView.findViewById(R.id.yemek2);
            t3 = (TextView) itemView.findViewById(R.id.yemek3);
            t4 = (TextView) itemView.findViewById(R.id.yemek4);


        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
