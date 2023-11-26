// SingleMovieActivity.java
package edu.uci.ics.fabflixmobile.ui.movielist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import androidx.appcompat.widget.Toolbar;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

import java.util.ArrayList;


public class SingleMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Movie movie = getIntent().getParcelableExtra("MOVIE");
        ArrayList<Movie> movieList = getIntent().getParcelableArrayListExtra("MOVIE_LIST");

        TextView titleTextView = findViewById(R.id.title);
        TextView yearTextView = findViewById(R.id.year);
        TextView directorTextView = findViewById(R.id.director); // Assuming you have a TextView for director
        TextView genresTextView = findViewById(R.id.genres); // Assuming you have a TextView for genres
        TextView starsTextView = findViewById(R.id.stars);

        titleTextView.setText(movie.getName());
        yearTextView.setText(String.valueOf(movie.getYear()));

        if (movie.getDirector() != null) {
            directorTextView.setText("Director: " + movie.getDirector());
        } else {
            directorTextView.setText("Director: N/A");
        }

        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            String formattedGenres = TextUtils.join(", ", movie.getGenres());
            genresTextView.setText("Genres: " + formattedGenres);
        } else {
            genresTextView.setText("Genres: N/Aa");
        }

        if (movie.getStars() != null && !movie.getStars().isEmpty()) {
            String formattedStars = TextUtils.join(", ", movie.getStars());
            starsTextView.setText("Stars: " + formattedStars);
        } else {
            starsTextView.setText("Stars: N/A");
        }

    }

    // Example: You might call this method when a back button is clicked
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//        super.onBackPressed();
//        finish();
        goBackToMovieList();
    }
    private void goBackToMovieList() {
        Intent movieListPage = new Intent(SingleMovieActivity.this, MovieListActivity.class);
        startActivity(movieListPage);
        finish();
    }


}
