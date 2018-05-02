package com.arunditti.android.popularmoviesstage2.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arunditti.android.popularmoviesstage2.R;
import com.arunditti.android.popularmoviesstage2.model.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by arunditti on 5/2/18.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<MovieItem> mMovieItems;

    private final MovieAdapterOnClickHandler mClickHandler;
    private final Context mContext;

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieItem movieClicked);
    }

    public MovieAdapter(Context mContext, MovieAdapterOnClickHandler clickHandler, ArrayList<MovieItem> mMovieItems) {
        this.mClickHandler = clickHandler;
        this.mMovieItems = mMovieItems;
        this.mContext = mContext;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        //Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder holder, int position) {

        holder.movieName.setText(mMovieItems.get(position).mMovieTitle);
        Picasso.with(mContext)
                .load(mMovieItems
                        .get(position).mImagePath)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .fit()
                .into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return this.mMovieItems.size();
    }

    //Create a class within MovieAdapter  called MovieAdapterViewHolder
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        ImageView moviePoster;
        TextView movieName;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv) ;

            moviePoster = itemView.findViewById(R.id.movie_poster);
            movieName = itemView.findViewById(R.id.movie_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            MovieItem movieClicked = mMovieItems.get(clickedPosition);
            mClickHandler.onClick(movieClicked);
        }
    }

    public void updateMovieList(ArrayList<MovieItem> movieItems) {
        this.mMovieItems = movieItems;
        this.notifyDataSetChanged();
    }
}
