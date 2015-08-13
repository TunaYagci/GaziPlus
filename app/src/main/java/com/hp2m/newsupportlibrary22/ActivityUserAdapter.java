package com.hp2m.newsupportlibrary22;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class ActivityUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<ActivityUserInfo> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Activity activity;

    public ActivityUserAdapter(Activity activity, List<ActivityUserInfo> data) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_user_body, parent, false);
        return new BodyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ActivityUserInfo info = data.get(position);
        ((BodyHolder) holder).a.setText(info.body);
        if (position == 0) {
            ((BodyHolder) holder).b.setImageResource(info.imageID);
        }
        switch (position) {
            case 0:
                ((BodyHolder) holder).c.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ayarlar intent
                    }
                });
                return;
            case 1:
                ((BodyHolder) holder).c.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // hakkýnda intent
                    }
                });
                return;
            case 2:
                ((BodyHolder) holder).c.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // kütüphane intent
                    }
                });
                return;
            case 3:
                ((BodyHolder) holder).c.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // çýkýþ yap intent
                        new AlertDialog.Builder(activity)
                                .setTitle("Çýkýþ yap")
                                .setMessage("Çýkýþ yapmak istediðinizden emin misiniz?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        SharedPreferences sP = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sP.edit();
                                        editor.putBoolean("isLoginSuccessful", false);
                                        editor.commit();
                                        DuyuruDB db = new DuyuruDB(activity);
                                        db.clearForLogOut();
                                        Intent i = new Intent(activity, WelcomeActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        activity.startActivity(i);
                                        activity.finish();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();

                    }
                });
                return;
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BodyHolder extends RecyclerView.ViewHolder {
        TextView a;
        ImageButton b;
        RelativeLayout c;

        public BodyHolder(View itemView) {
            super(itemView);
            a = (TextView) itemView.findViewById(R.id.text);
            b = (ImageButton) itemView.findViewById(R.id.icon);
            c = (RelativeLayout) itemView.findViewById(R.id.body_layout);
        }
    }
}
