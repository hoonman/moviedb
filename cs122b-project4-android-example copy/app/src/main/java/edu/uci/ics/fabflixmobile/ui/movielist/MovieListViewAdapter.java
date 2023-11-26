package edu.uci.ics.fabflixmobile.ui.movielist;

import android.text.TextUtils;
import android.util.Log;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private final ArrayList<Movie> movies;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView subtitle;
        TextView director;
        TextView rating;
        TextView genres;
        TextView stars;
    }

    public MovieListViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.movielist_row, movies);
        this.movies = movies;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        Movie movie = movies.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            for (Movie i : movies) {
                Log.d("MovieListActivity3", "Movie: " + i.getName() + ", Director: " + i.getDirector() + ", Rating: " + i.getRating());
            }
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.movielist_row, parent, false);
            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.subtitle = convertView.findViewById(R.id.subtitle);
//            viewHolder.subtitle = convertView.findViewById(R.id.subtitle);
            viewHolder.director = convertView.findViewById(R.id.director);
            viewHolder.rating = convertView.findViewById(R.id.rating);
            viewHolder.genres = convertView.findViewById(R.id.genres);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            Log.d("or here", "heehee");
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.title.setText(movie.getName());
//        viewHolder.subtitle.setText(movie.getYear() + "");
        String master = "";
        master += movie.getYear() + "\n";
        master += movie.getDirector() + "\n";
        String starString = TextUtils.join(", ", movie.getStars());
        master += starString + "\n";
        String genreString = TextUtils.join(", ", movie.getGenres());
        master += genreString + "\n";
        master += movie.getRating() + "\n";

//        master = TextUtils.join(", ", movie.getStars());
//        viewHolder.director.setText(movie.getDirector());
//        viewHolder.rating.setText(Double.toString(movie.getRating()));

//        String genresString = TextUtils.join(", ", movie.getGenres());
//        viewHolder.genres.setText(genresString);
//
//        String starsString = TextUtils.join(", ", movie.getStars());
//        viewHolder.stars.setText(starsString);
        viewHolder.subtitle.setText(master);

        // Return the completed view to render on screen
        return convertView;
    }
}