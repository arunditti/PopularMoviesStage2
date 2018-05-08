package com.arunditti.android.popularmoviesstage2.ui.adapters;

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
import com.arunditti.android.popularmoviesstage2.model.Review;
import java.util.ArrayList;

/**
 * Created by arunditti on 5/3/18.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();

   private final Context mContext;
    private ArrayList<Review> mReviewItems;

    public ReviewAdapter(Context context, ArrayList<Review> reviewItems) {

       this.mContext = context;
       this.mReviewItems = reviewItems;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        //Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_list_item, parent,false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewAdapterViewHolder holder, int position) {

        holder.authorName.setText(mReviewItems.get(position).getAuthor());
        holder.content.setText(mReviewItems.get(position).getContent());

    }

    @Override
    public int getItemCount() {
        return mReviewItems.size();
    }

    //Create a class within ReviewAdapter  called ReviewAdapterViewHolder
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView authorName;
        TextView content;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv) ;

            authorName = itemView.findViewById(R.id.review_author);
            content = itemView.findViewById(R.id.review_content);
        }
    }

    public void updateReviewList(ArrayList<Review> ReviewItems) {
        this.mReviewItems = ReviewItems;
        this.notifyDataSetChanged();
    }
}
