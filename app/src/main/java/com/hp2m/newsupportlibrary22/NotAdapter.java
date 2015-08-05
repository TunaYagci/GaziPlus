package com.hp2m.newsupportlibrary22;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Collections;
import java.util.List;

public class NotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_SUBHEADER = 1;
    private static final int TYPE_ITEM = 2;
    private static final int EMPTY_VIEW = 10;
    List<NotInformation> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;


    public NotAdapter(Context context, List<NotInformation> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.custom_row_notlar_body, parent, false);
            //view.setOnClickListener(new DersClickListener());
            return new NotItemViewHolder(view);
        } else if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.custom_row_notlar_header, parent, false);
            return new NotHeaderViewHolder(view);
        } else if (viewType == EMPTY_VIEW) {
            View view = inflater.inflate(R.layout.not_empty_view, parent, false);
            return new NotEmptyView(view);
        } else if (viewType == TYPE_SUBHEADER) {
            View view = inflater.inflate(R.layout.custom_row_notlar_subheader, parent, false);
            return new NotSubHeaderViewHolder(view);
        }

        throw new RuntimeException("bla bla bla, not right type");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NotItemViewHolder) {
            // ((NotItemViewHolder) holder).topLayout.setVisibility(View.GONE);
            ((NotItemViewHolder) holder).bottomLayout.setVisibility(View.GONE);
            final NotInformation current = data.get(position);
            ((NotItemViewHolder) holder).topAndBottomHolder.setOnClickListener(new DersClickListener());
            ((NotItemViewHolder) holder).dersKodu.setText(current.dersKodu);
            ((NotItemViewHolder) holder).dersAdi.setText(current.dersAdi);
            ((NotItemViewHolder) holder).vizeNotu.setText(current.vizeNotu);
            ((NotItemViewHolder) holder).finalNotu.setText(current.finalNotu);
            ((NotItemViewHolder) holder).butNotu.setText(current.butNotu);
            ((NotItemViewHolder) holder).seeDetailedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, NotlarDetailed.class);
                    i.putExtra("dersKodu", current.dersKodu);
                    Log.i("tuna", "bundle put " + current.dersKodu);
                    context.startActivity(i);
                }
            });
            //Log.i("tuna", (((NotItemViewHolder) holder).topLayout.getVisibility()==View.VISIBLE) + " binding notItemViewHolder");
        } else if (holder instanceof NotHeaderViewHolder) {
            NotInformation current = data.get(position);
            ((NotHeaderViewHolder) holder).name.setText(current.name);
            ((NotHeaderViewHolder) holder).ogrNo.setText(current.ogrNo);
            ((NotHeaderViewHolder) holder).genelOrtNumber.setText(current.genelOrtNumber);
            YoYo.with(Techniques.SlideInLeft)
                    .duration(500)
                    .playOn(((NotHeaderViewHolder) holder).name);
            YoYo.with(Techniques.SlideInLeft)
                    .duration(500)
                    .playOn(((NotHeaderViewHolder) holder).ogrNo);
            YoYo.with(Techniques.DropOut)
                    .duration(1000)
                    .playOn(((NotHeaderViewHolder) holder).genelOrtNumber);


            Picasso picasso = new Picasso.Builder(context)
                    .downloader(new OkHttpDownloader(new OkHttpClient()))
                    .listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            Log.i("tuna", "Picasso exception= " + exception.toString());
                            Log.i("tuna", "Picasso uri= " + uri.toString());
                            exception.printStackTrace();
                        }
                    })
                    .build();

            Log.i("tuna", "gonna use picasso");
            SharedPreferences sharedPreferences = context.getSharedPreferences("avatar", Context.MODE_PRIVATE);
            picasso.with(((NotHeaderViewHolder) holder).sahip.getContext())
                    .load(current.imageLink)
                    .placeholder(R.drawable.arrow_right)
                    .error(R.drawable.error_notlar_avatar)
                    .transform(new CircleTransform())
                    .into(((NotHeaderViewHolder) holder).sahip);
            if (!sharedPreferences.getBoolean("IsAvatarDownloadedFor" + current.ogrNo, false)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("IsAvatarDownloadedFor" + current.ogrNo, true);
                editor.apply();
            }
            Log.i("tuna", "picasso used");
        }




           /*picassoClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .build();
                    return chain.proceed(newRequest);
                }
            });*/

        //Picasso picasso = new Picasso.Builder(context).downloader(new OkHttpDownloader(picassoClient)).build();
        //Picasso picass2 = new Picasso.Builder(context).downloader(new OkHttpDownloader(MAX_CACHE_SIZE)).build();

            /*picasso.with(((NotHeaderViewHolder) holder).sahip.getContext())
                    .load(current.imageLink)
                    .transform(new CircleTransform())
                    .into(((NotHeaderViewHolder) holder).sahip);*/


            /*Log.i("tuna", "gonna use picasso");
            SharedPreferences sharedPreferences = context.getSharedPreferences("avatar", Context.MODE_PRIVATE);
            Picasso.with(((NotHeaderViewHolder) holder).sahip.getContext())
                    .load(current.imageLink)
                    .placeholder(R.drawable.arrow_right)
                    .error(R.drawable.error_notlar_avatar)
                    .transform(new CircleTransform())
                    .into(((NotHeaderViewHolder) holder).sahip);
            if (!sharedPreferences.getBoolean("IsAvatarDownloadedFor" + current.ogrNo, false)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("IsAvatarDownloadedFor" + current.ogrNo, true);
                editor.apply();
            }
            Log.i("tuna", "picasso used");
        } */


        else if (holder instanceof NotSubHeaderViewHolder) {
            NotInformation current = data.get(position);
            ((NotSubHeaderViewHolder) holder).donemAdi.setText(current.donemAdi);
        }


    }

    @Override
    public int getItemCount() {
        return data.size() > 0 ? data.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        try {
            NotInformation current = data.get(position);
            if (data.size() == 0)
                return EMPTY_VIEW;
            else if (isPositionHeader(position))
                return TYPE_HEADER;
            else if (current.toString().startsWith("2") && current.toString().endsWith("ý")) {
                Log.i("tuna", "sub header created-> " + data.get(position).toString());
                return TYPE_SUBHEADER;
            }
        } catch (NullPointerException e) {
            return TYPE_ITEM;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}

class NotItemViewHolder extends RecyclerView.ViewHolder {
    LinearLayout topLayout, donemLayout, topAndBottomHolder;
    RelativeLayout bottomLayout;
    TextView donemAdi, dersKodu, dersAdi, vizeNotu, finalNotu, butNotu; //basariNotu, kredi, sinifOrt;
    ImageButton seeDetailedButton;

    public NotItemViewHolder(View itemView) {
        super(itemView);
        topAndBottomHolder = (LinearLayout) itemView.findViewById(R.id.topAndBottomHolder);
        donemLayout = (LinearLayout) itemView.findViewById(R.id.donem_layout);
        topLayout = (LinearLayout) itemView.findViewById(R.id.topLayout);
        donemAdi = (TextView) itemView.findViewById(R.id.donemAdi);
        bottomLayout = (RelativeLayout) itemView.findViewById(R.id.bottomLayout);
        dersKodu = (TextView) itemView.findViewById(R.id.dersKodu);
        dersAdi = (TextView) itemView.findViewById(R.id.dersAdi);
        vizeNotu = (TextView) itemView.findViewById(R.id.vizeNotu);
        finalNotu = (TextView) itemView.findViewById(R.id.finalNotu);
        butNotu = (TextView) itemView.findViewById(R.id.butNotu);
        seeDetailedButton = (ImageButton) itemView.findViewById(R.id.seeDetailedButton);
    }
}


class DersClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        View someView = v.findViewById(R.id.bottomLayout);
        if (someView.getVisibility() == View.GONE)
            someView.setVisibility(View.VISIBLE);
        else if (someView.getVisibility() == View.VISIBLE)
            someView.setVisibility(View.GONE);
    }
}


class NotHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView name, ogrNo, genelOrtNumber;
    ImageView sahip;

    public NotHeaderViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        ogrNo = (TextView) itemView.findViewById(R.id.ogrNo);
        sahip = (ImageView) itemView.findViewById(R.id.sahip);
        genelOrtNumber = (TextView) itemView.findViewById(R.id.genelOrtNumber);
    }
}

class NotSubHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView donemAdi;

    public NotSubHeaderViewHolder(View itemView) {
        super(itemView);
        donemAdi = (TextView) itemView.findViewById(R.id.donemAdi);
    }
}

class NotEmptyView extends RecyclerView.ViewHolder {
    public NotEmptyView(View itemView) {
        super(itemView);
    }
}

class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}
