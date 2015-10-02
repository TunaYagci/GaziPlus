package com.hp2m.newsupportlibrary22;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * Created by Tuna on 3.10.2015.
 */
public class ProfilePhotoFragment extends DialogFragment {

    private ImageView profilePhotoImageView;
    private ImageButton downloadImageButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_photo, container, false);

        profilePhotoImageView = (ImageView) rootView.findViewById(R.id.profilePhotoImageView);
        downloadImageButton = (ImageButton) rootView.findViewById(R.id.downloadImageButton);

        final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        final String number = sharedPreferences.getString("defaultOgrNo", "hata"); // default number'ý sharedPref ile çek

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.detailed_duyuru_error)
                .showImageForEmptyUri(R.drawable.detailed_duyuru_error)
                .showImageOnLoading(R.drawable.detailed_duyuru_loading)
                .bitmapConfig(Bitmap.Config.RGB_565)
                //.displayer(new RoundedBitmapDisplayer(500))
                .imageScaleType(ImageScaleType.EXACTLY)
                //.cacheInMemory(true)
                //.cacheOnDisk(true)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, 700,800, false);
                    }
                })
                .build();

        ImageLoader.getInstance().displayImage(sharedPreferences.getString("avatarLinkFor" + number, ""),
                profilePhotoImageView,
                defaultOptions);

        downloadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "hellow", Toast.LENGTH_SHORT).show();
                MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),((BitmapDrawable)profilePhotoImageView.getDrawable()).getBitmap(), "Gazi+" , "Profil Fotoðrafý");

            }
        });


        return rootView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;

        int dialogWidth = 810; // specify a value here
        int dialogHeight = 1250; // specify a value here

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

        // ... other stuff you want to do in your onStart() method
    }
}
