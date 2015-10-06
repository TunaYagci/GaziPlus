package com.hp2m.GaziPlus;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class UserActivity extends AppCompatActivity {

    final EventBus bus = EventBus.getDefault();
    private final String OGRENCI_NO = "ogrNoKey";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ImageView avatar, background;
    private TextView name, bolum;
    // private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //context = this;

        background = (ImageView) findViewById(R.id.background);
        avatar = (ImageView) findViewById(R.id.avatar);
        name = (TextView) findViewById(R.id.name);
        bolum = (TextView) findViewById(R.id.bolum);


        final SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String number = sharedPreferences.getString("defaultOgrNo", "hata"); // default number'� sharedPref ile �ek
        if (sharedPreferences.getBoolean("IsAvatarDownloadedFor" + number, false)) {
            avatar.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            bolum.setVisibility(View.VISIBLE);
            bolum.setText(sharedPreferences.getString("bolumAdi", "-"));
            name.setText(sharedPreferences.getString("defaultOgrName", "-"));
            Runnable r = new Runnable() {
                @Override
                public void run() {

                    DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                            .showImageOnFail(R.drawable.detailed_duyuru_error)
                            .showImageForEmptyUri(R.drawable.detailed_duyuru_error)
                            .showImageOnLoading(R.drawable.detailed_duyuru_loading)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .displayer(new RoundedBitmapDisplayer(500))
                            .imageScaleType(ImageScaleType.EXACTLY)
                            .postProcessor(new BitmapProcessor() {
                                @Override
                                public Bitmap process(Bitmap bmp) {
                                    return Bitmap.createScaledBitmap(bmp, 450, 200, false);
                                }
                            })
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();

                    ImageLoader.getInstance().displayImage(sharedPreferences.getString("avatarLinkFor" + number, ""),
                            avatar,
                            defaultOptions);

                    avatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentManager fm = getSupportFragmentManager();
                            ProfilePhotoFragment popup = new ProfilePhotoFragment();

                            // set ogrenci no, send to bundle
                            Bundle bdl = new Bundle(1);
                            bdl.putString(OGRENCI_NO, number);
                            popup.setArguments(bdl);
                            // ------------------------------

                            popup.show(fm, "fragment_edit_name");

                        }
                    });

                    /*Picasso.with(context)
                            .load(sharedPreferences.getString("avatarLinkFor"+number, ""))
                            .placeholder(R.drawable.loading_notlar_avatar_2)
                            .error(R.drawable.loading_error_1)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .resize(450, 470)
                            .transform(new CircleTransform())
                            .into(avatar);*/
                }
            };
            r.run();
        } else {
            // this is a user who hasn't logged in credentials yet
            name.setVisibility(View.VISIBLE);
            background.setMaxHeight(50);
            name.setText("��renci sistemine giri� yap�lmad�");
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bus.post(new goToFragment2());
                    finish();
                }
            });
        }
        // misafir mi yoksa kay�tl� kullan�c� m� bir bak, SharedPrefzz


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new ActivityUserAdapter(this, getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void goBackFromUserActivity(View v) {
        finish();
    }

    private List<ActivityUserInfo> getData() {
        List<ActivityUserInfo> data = new ArrayList<>();
        ActivityUserInfo current;

        ArrayList<String> bodyList = new ArrayList<>();
        bodyList.add("Ayarlar");
        bodyList.add("Hakk�m�zda");
       // bodyList.add("A��k Kaynak K�t�phaneleri");
        bodyList.add("Kullan�c� S�zle�mesi");
        bodyList.add("��k�� Yap");

        for (int i = 0; i < 4; i++) {
            current = new ActivityUserInfo();
            current.body = bodyList.get(i);
            if (i == 0)
                current.imageID = R.drawable.ic_settings_black_24dp;
            data.add(current);
        }

        return data;
    }

}

class goToFragment2 {
}
