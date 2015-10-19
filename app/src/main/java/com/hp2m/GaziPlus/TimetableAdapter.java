package com.hp2m.GaziPlus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by Tuna on 19.10.2015.
 */
public class TimetableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int EMPTY_VIEW = 10;
    List<TimetableInformation> data = Collections.emptyList();
    private LayoutInflater inflater;

    public TimetableAdapter(Context context, List<TimetableInformation> data) {
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
            View view = inflater.inflate(R.layout.custom_row_timetable, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            // hey sadr, so we get the data here from the given position
            TimetableInformation current = data.get(position);
            // and setting text
            ((MyViewHolder) holder).t1.setText(current.text);
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
        TextView t1;

        public MyViewHolder(View itemView) {
            super(itemView);
            t1 = (TextView) itemView.findViewById(R.id.dersText);
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
