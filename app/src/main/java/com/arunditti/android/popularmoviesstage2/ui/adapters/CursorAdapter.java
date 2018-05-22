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
import com.arunditti.android.popularmoviesstage2.model.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by arunditti on 5/21/18.
 */

public class CursorAdapter extends RecyclerView.Adapter<CursorAdapter.CursorAdapterViewHolder> {

    private static final String TAG = CursorAdapter.class.getSimpleName();

    private ArrayList<MovieItem> mMovieItems;

    private final Context mContext;
    private Cursor mCursor;

    public interface CursorAdapterOnClickHandler {
        void onClick(MovieItem movieClicked);
    }

    public CursorAdapter(Context context, ArrayList<MovieItem> movieItems) {
        this.mMovieItems = movieItems;
        this.mContext = context;
    }

    @Override
    public CursorAdapter.CursorAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        Context context = parent.getContext();
        //Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new CursorAdapter.CursorAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CursorAdapter.CursorAdapterViewHolder holder, int position) {

        mCursor.moveToPosition(position);


        holder.movieName.setText(mMovieItems.get(position).mMovieTitle);
        Picasso.with(mContext)
                .load(mCursor.getString(mCursor.getColumnIndex(FavoritesContract.FavoriteEntry.COLUMN_MOVIE_IMAGE_PATH)))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .fit()
                .into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        if(mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    //Create a class within MovieAdapter  called MovieAdapterViewHolder
    public class CursorAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        ImageView moviePoster;
        TextView movieName;

        public CursorAdapterViewHolder(View itemView) {
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
        }
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
