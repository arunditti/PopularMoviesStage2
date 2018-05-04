package com.arunditti.android.popularmoviesstage2.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arunditti.android.popularmoviesstage2.R;
import com.arunditti.android.popularmoviesstage2.model.Trailer;

import java.util.ArrayList;

/**
 * Created by arunditti on 5/4/18.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private static final String TAG = TrailerAdapter.class.getSimpleName();

    private ArrayList<Trailer> mTrailerItems;

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

        holder.trailerName.setText(mTrailerItems.get(position).getTrailerName());
    }

    @Override
    public int getItemCount() {
        return this.mTrailerItems.size();
    }

    //Create a class within TrailerAdapter  called TrailerAdapterViewHolder
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;

        TextView trailerName;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv) ;

            trailerName = itemView.findViewById(R.id.trailer_title);
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
