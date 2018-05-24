package com.arunditti.android.popularmoviesstage2.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arunditti.android.popularmoviesstage2.R;
import com.arunditti.android.popularmoviesstage2.data.FavoritesContract;
import com.arunditti.android.popularmoviesstage2.data.FavoritesContract.FavoriteEntry;
import com.arunditti.android.popularmoviesstage2.model.MovieItem;
import com.arunditti.android.popularmoviesstage2.ui.MainActivity;
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
    private Cursor mCursor;

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieItem movieClicked);
    }

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler, ArrayList<MovieItem> movieItems) {
        this.mClickHandler = clickHandler;
        this.mMovieItems = movieItems;
        this.mContext = context;
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

        //Move the cursor to the appropriate position
//       if(!mCursor.moveToPosition(position));
//       return;

     //   mCursor.moveToPosition(position);
//        String movieTitle = mCursor.getString(MainActivity.INDEX_MOVIE_TITLE);
//        holder.movieName.setText(movieTitle);
//
//        String movieImagePath = mCursor.getString(MainActivity.INDEX_MOVIE_IMAGE_PATH);
//
//        Picasso.with(mContext)
//                .load(movieImagePath)
//                .placeholder(R.drawable.ic_launcher_background)
//                .error(R.drawable.ic_launcher_foreground)
//                .fit()
//                .into(holder.moviePoster);

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
//        if (null == mCursor) return 0;
//        return mCursor.getCount();
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

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
