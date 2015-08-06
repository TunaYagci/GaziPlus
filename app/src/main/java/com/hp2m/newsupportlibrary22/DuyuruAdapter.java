package com.hp2m.newsupportlibrary22;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

public class DuyuruAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int EMPTY_VIEW = 2;
    private static final int LOADING_VIEW = 3;
    final EventBus bus = EventBus.getDefault();
    List<DuyuruInformation> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public DuyuruAdapter(Context context, List<DuyuruInformation> data,
                         OnItemClickListener onItemClickListener) {

        mOnItemClickListener = onItemClickListener;
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.custom_row_duyuru_body, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.custom_row_duyuru_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == EMPTY_VIEW) {
            View view = inflater.inflate(R.layout.empty_view, parent, false);
            return new EmptyViewHolder(view);
        } else if (viewType == LOADING_VIEW) {
            View view = inflater.inflate(R.layout.empty_card_loading_view, parent, false);
            return new LoadingViewHolder(view);
        }


        throw new RuntimeException("bla bla bla, not right type");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            DuyuruInformation current = data.get(position);
            ((ItemViewHolder) holder).c1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            ((ItemViewHolder) holder).h1.setText(current.header);
            ((ItemViewHolder) holder).b1.setText(current.body);
            ((ItemViewHolder) holder).t1.setText(current.dateDiff);
        } else if (holder instanceof HeaderViewHolder) {
            DuyuruInformation current = data.get(position);
            ((HeaderViewHolder) holder).i1.setImageResource(current.imageID);
            //((HeaderViewHolder) holder).i1.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), current.imageID, 150, 150));
            ((HeaderViewHolder) holder).bolum.setText(current.bolum);

        } else if (holder instanceof LoadingViewHolder) {
            final LoadingViewHolder holder2 = (LoadingViewHolder) holder;
            ((LoadingViewHolder) holder).layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("tuna", "onClick to load history");
                    holder2.loadHistory.setVisibility(View.GONE);
                    holder2.loadingBar.setVisibility(View.VISIBLE);
                    bus.post(new LoadOldDuyuru());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        //return data.size() > 0 ? data.size() : 1;
        if (data.size() > 0)
            return data.size() + 1;
        else
            return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.size() == 0) {
            return EMPTY_VIEW;
        } else if (isPositionHeader(position))
            return TYPE_HEADER;
        else if (position == data.size()) {

            return LOADING_VIEW;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof HeaderViewHolder) {
            BitmapDrawable drawable = (BitmapDrawable) ((HeaderViewHolder) holder).i1.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            final RecyclerView.ViewHolder holder2 = holder;
            Palette.generateAsync(bitmap,
                    new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            Palette.Swatch vibrant =
                                    palette.getDarkVibrantSwatch();
                            if (vibrant != null) {
                                // If we have a vibrant color
                                // update the title TextView
                                ((HeaderViewHolder) holder2).bolum.setBackgroundColor(
                                        vibrant.getRgb());
                                ((HeaderViewHolder) holder2).bolum.setTextColor(
                                        vibrant.getBodyTextColor());
                            }
                        }
                    });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView h1, b1, t1; // header, body
        CardView c1;

        public ItemViewHolder(View itemView) {
            super(itemView);
            c1 = (CardView) itemView.findViewById(R.id.cardView);
            h1 = (TextView) itemView.findViewById(R.id.header);
            b1 = (TextView) itemView.findViewById(R.id.body);
            t1 = (TextView) itemView.findViewById(R.id.tarih);


        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView i1; // header, body
        CardView c_header;
        TextView bolum;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            c_header = (CardView) itemView.findViewById(R.id.cardView);
            i1 = (ImageView) itemView.findViewById(R.id.bolum_image);
            bolum = (TextView) itemView.findViewById(R.id.bolum_text);


        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        TextView loadHistory;
        ProgressBar loadingBar;
        RelativeLayout layout;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            loadHistory = (TextView) itemView.findViewById(R.id.loadHistory);
            loadingBar = (ProgressBar) itemView.findViewById(R.id.loadingBar);
            layout = (RelativeLayout) itemView.findViewById(R.id.empty_card_loading_view_layout);

        }
    }

}

class LoadOldDuyuru {
}