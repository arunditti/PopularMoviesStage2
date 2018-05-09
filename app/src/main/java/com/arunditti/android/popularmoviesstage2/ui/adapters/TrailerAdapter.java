package com.arunditti.android.popularmoviesstage2.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arunditti.android.popularmoviesstage2.R;
import com.arunditti.android.popularmoviesstage2.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by arunditti on 5/4/18.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public static final String YOU_TUBE_VIDEO_URL = "http://www.youtube.com/watch?v=";
    public static final String YOUTUBE_URI = "vnd.youtube:";
    final String TRAILER_KEY = "key";
    private static final String YOUTUBE_IMAGE_URL_PREFIX = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_IMAGE_URL_SUFFIX = "/0.jpg";

    public ArrayList<Trailer> mTrailerItems;

    private final TrailerAdapterOnClickHandler mClickHandler;
    private final Context mContext;

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailerClicked);
    }

    public TrailerAdapter(Context mContext, TrailerAdapterOnClickHandler clickHandler, ArrayList<Trailer> mTrailerItems) {
        this.mClickHandler = clickHandler;
        this.mTrailerItems = mTrailerItems;
        this.mContext = mContext;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        //Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailerAdapterViewHolder holder, int position) {

        String trailerThumbnailUrl = YOUTUBE_IMAGE_URL_PREFIX + mTrailerItems.get(position) + YOUTUBE_IMAGE_URL_SUFFIX;
        Log.d(LOG_TAG, trailerThumbnailUrl);
        Picasso.with(mContext)
                .load(mTrailerItems.get(position).getTrailerImage())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .fit()
                .into(holder.trailerImageView);
    }

    @Override
    public int getItemCount() {
        return this.mTrailerItems.size();
    }

    //Create a class within TrailerAdapter  called TrailerAdapterViewHolder
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;

        ImageView trailerImageView;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv) ;

            trailerImageView = itemView.findViewById(R.id.trailer_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Trailer trailerClicked = mTrailerItems.get(clickedPosition);
            mClickHandler.onClick(trailerClicked);
        }
    }

    public void updateTrailerList(ArrayList<Trailer> trailerItems) {
        this.mTrailerItems = trailerItems;
        this.notifyDataSetChanged();
    }
}
