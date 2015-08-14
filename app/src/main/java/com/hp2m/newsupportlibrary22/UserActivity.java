package com.hp2m.newsupportlibrary22;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ImageView avatar, background;
    private TextView name, bolum;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        context = this;

        background = (ImageView) findViewById(R.id.background);
        avatar = (ImageView) findViewById(R.id.avatar);
        name = (TextView) findViewById(R.id.name);
        bolum = (TextView) findViewById(R.id.bolum);


        final String number = "141180068"; // default number'ý sharedPref ile çek
        final SharedPreferences sharedPreferences = getSharedPreferences("avatar", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("IsAvatarDownloadedFor" + number, false)) {
            avatar.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            bolum.setVisibility(View.VISIBLE);
            bolum.setText("Bilgisayar Mühendisliði");
            name.setText("Sadýk Tuna Yaðcý");
            Runnable r = new Runnable() {
                @Override
                public void run() {

                    DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                            .showImageOnFail(R.drawable.detailed_duyuru_error)
                            .showImageForEmptyUri(R.drawable.detailed_duyuru_error)
                            .showImageOnLoading(R.drawable.detailed_duyuru_loading)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .displayer(new RoundedBitmapDisplayer(1000))
                            .imageScaleType(ImageScaleType.EXACTLY)
                            .postProcessor(new BitmapProcessor() {
                                @Override
                                public Bitmap process(Bitmap bmp) {
                                    return Bitmap.createScaledBitmap(bmp, 650, 700, false);
                                }
                            })
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();

                    ImageLoader.getInstance().displayImage(sharedPreferences.getString("avatarLinkFor" + number, ""),
                            avatar,
                            defaultOptions);

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
            name.setText("Öðrenci sistemine giriþ yapýlmadý");
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bus.post(new goToFragment2());
                    finish();
                }
            });
        }
        // misafir mi yoksa kayýtlý kullanýcý mý bir bak, SharedPrefzz


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
        bodyList.add("Hakkýnda");
        bodyList.add("Açýk Kaynak Kütüphaneleri");
        bodyList.add("Çýkýþ Yap");

        for (int i = 0; i < 4; i++) {
            current = new ActivityUserInfo();
            current.body = bodyList.get(i);
            if (i == 0)
                current.imageID = R.drawable.settings;
            data.add(current);
        }

        return data;
    }

}

class goToFragment2 {
}
