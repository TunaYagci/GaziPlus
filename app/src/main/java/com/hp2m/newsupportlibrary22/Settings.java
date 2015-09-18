package com.hp2m.newsupportlibrary22;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Settings extends AppCompatActivity {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final String FAKULTE_BILDIRIM_AYARI = "Fakülteden duyuru bildirimleri al";
    private static final String BOLUM_BILDIRIM_AYARI = "Bölümden duyuru bildirimleri al";


    private RecyclerView recyclerView;
    private SharedPreferences sP;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ayarlar");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new SettingsAdapter(this, getData()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        editor = sP.edit();


    }

    private List<ActivityUserInfo> getData() {
        sP = getSharedPreferences("user", Context.MODE_PRIVATE);

        List<ActivityUserInfo> data = new ArrayList<>();

        ActivityUserInfo current;
        current = new ActivityUserInfo();
        current.body = "Bildirimler";
        data.add(current);
        if (sP.getString("bolumHint", "none").equals("nofab")) {
            current = new ActivityUserInfo();
            current.body = FAKULTE_BILDIRIM_AYARI;
            data.add(current);
        } else {
            current = new ActivityUserInfo();
            current.body = BOLUM_BILDIRIM_AYARI;
            data.add(current);
            current = new ActivityUserInfo();
            current.body = FAKULTE_BILDIRIM_AYARI;
            data.add(current);
        }

        return data;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<ActivityUserInfo> data = Collections.emptyList();
        private LayoutInflater inflater;
        private Activity activity;

        public SettingsAdapter(Activity activity, List<ActivityUserInfo> data) {
            inflater = LayoutInflater.from(activity);
            this.activity = activity;
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View view = inflater.inflate(R.layout.settings_header, parent, false);
                return new Header(view);
            } else {
                View view = inflater.inflate(R.layout.settings_body, parent, false);
                return new Body(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Header) {
                final ActivityUserInfo current = data.get(position);
                ((Header) holder).header.setText(current.body);
            } else if (holder instanceof Body) {
                final ActivityUserInfo current = data.get(position);
                ((Body) holder).nameOfSetting.setText(current.body);
                if (current.body.equals(FAKULTE_BILDIRIM_AYARI)) {
                    if (sP.getBoolean("isFakulteNotificationsAllowed", false)) {
                        ((Body) holder).checkboxJohn.setChecked(true);
                    } else
                        ((Body) holder).checkboxJohn.setChecked(false);

                } else if (current.body.equals(BOLUM_BILDIRIM_AYARI)) {
                    if (sP.getBoolean("isBolumNotificationsAllowed", false)) {
                        ((Body) holder).checkboxJohn.setChecked(true);
                    } else
                        ((Body) holder).checkboxJohn.setChecked(false);
                }

                ((Body) holder).checkboxJohn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) { // checked
                            if (current.body.equals(BOLUM_BILDIRIM_AYARI)) {
                                setNotifications(BOLUM_BILDIRIM_AYARI, true);
                            } else if (current.body.equals(FAKULTE_BILDIRIM_AYARI)) {
                                setNotifications(FAKULTE_BILDIRIM_AYARI, true);
                            }
                        } else { // unchecked
                            if (current.body.equals(BOLUM_BILDIRIM_AYARI)) {
                                setNotifications(BOLUM_BILDIRIM_AYARI, false);
                            } else if (current.body.equals(FAKULTE_BILDIRIM_AYARI)) {
                                setNotifications(FAKULTE_BILDIRIM_AYARI, false);
                            }
                        }
                    }
                });
            }
        }

        public void setNotifications(String generalMode, boolean state) {
            boolean wasBothClosed = !sP.getBoolean("isBolumNotificationsAllowed", false) && !sP.getBoolean("isFakulteNotificationsAllowed", false);
            if (generalMode.equals(BOLUM_BILDIRIM_AYARI)) {
                if (state) {
                    editor.putBoolean("isBolumNotificationsAllowed", true);
                } else {
                    editor.putBoolean("isBolumNotificationsAllowed", false);
                }
            } else if (generalMode.equals(FAKULTE_BILDIRIM_AYARI)) {
                if (state) {
                    editor.putBoolean("isFakulteNotificationsAllowed", true);
                } else {
                    editor.putBoolean("isFakulteNotificationsAllowed", false);
                }
            }
            editor.commit();
            alarmSetterCanceller(wasBothClosed);

        }

        private void alarmSetterCanceller(boolean wasBothClosed) {
            PlusMainReceiver receiver = new PlusMainReceiver();
            if (!sP.getBoolean("isFakulteNotificationsAllowed", false) && !sP.getBoolean("isBolumNotificationsAllowed", false)) {
                // and check if NOT sP is active or not
                receiver.CancelAlarm(activity);
                Log.i("gazinotification", "all alarms are cancelled");
            } else if (wasBothClosed) { // and check if NOT sP was online or not, so we don't set alarm twice.
                receiver.SetAlarm(activity);
                Log.i("gazinotification", "all alarms are opened");
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            } else
                return TYPE_ITEM;
        }

        class Header extends RecyclerView.ViewHolder {
            TextView header;

            public Header(View view) {
                super(view);
                header = (TextView) view.findViewById(R.id.header);
            }
        }

        class Body extends RecyclerView.ViewHolder {
            TextView nameOfSetting;
            CheckBox checkboxJohn;

            public Body(View view) {
                super(view);
                nameOfSetting = (TextView) view.findViewById(R.id.nameOfSetting);
                checkboxJohn = (CheckBox) view.findViewById(R.id.checkboxJohn);
            }
        }
    }
}
