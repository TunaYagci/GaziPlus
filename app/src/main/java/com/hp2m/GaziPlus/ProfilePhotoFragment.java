package com.hp2m.GaziPlus;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Tuna on 3.10.2015.
 */
public class ProfilePhotoFragment extends DialogFragment {

    private final String OGRENCI_NO = "ogrNoKey";
    private ImageView profilePhotoImageView;
    private ImageButton downloadImageButton;
    private Bitmap showedImage;
    private boolean hasImageDownloadedSuccessfully = false;
    private SharedPreferences sharedPreferences;
    private String ogrNo;

    public static final String insertImage(ContentResolver cr,
                                           Bitmap source,
                                           String title,
                                           String description) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.PNG, 100, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    private static final Bitmap storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind) {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND,kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID,(int)id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT,thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH,thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_photo, container, false);

        ogrNo = getArguments().getString(OGRENCI_NO);

        profilePhotoImageView = (ImageView) rootView.findViewById(R.id.profilePhotoImageView);
        downloadImageButton = (ImageButton) rootView.findViewById(R.id.downloadImageButton);

       sharedPreferences = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        //final String number = sharedPreferences.getString("defaultOgrNo", "hata"); // default number'ý sharedPref ile çek
        final String number = ogrNo;

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.detailed_duyuru_error)
                .showImageForEmptyUri(R.drawable.detailed_duyuru_error)
                .showImageOnLoading(R.drawable.detailed_duyuru_loading)
                //.bitmapConfig(Bitmap.Config.ARGB_4444)
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
                defaultOptions, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        //spinner.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        showedImage = loadedImage;
                        hasImageDownloadedSuccessfully = true;
                    }
                });


                downloadImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadImage();


                    }
                });


        return rootView;
    }

    private void downloadImage() {
       if(hasImageDownloadedSuccessfully) {
           try {
               insertImage(this.getActivity().getContentResolver(), showedImage, sharedPreferences.getString("defaultOgrNo", "profil_fotografi"),
                       "profil_fotografi");
               Toast.makeText(getActivity(), "Fotoðraf galeriye kaydedildi", Toast.LENGTH_SHORT).show();
           } catch (Exception e) {
               Toast.makeText(getActivity(), "Bir hata oluþtu, fotoðraf kaydedilemedi", Toast.LENGTH_SHORT).show();
           }
       }
    }

    /*@Override
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
    }*/
}
