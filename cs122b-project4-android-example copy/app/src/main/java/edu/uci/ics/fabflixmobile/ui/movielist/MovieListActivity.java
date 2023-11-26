package edu.uci.ics.fabflixmobile.ui.movielist;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import androidx.appcompat.widget.SearchView;
public class MovieListActivity extends AppCompatActivity {
    private final String host = "3.22.96.105";
    private final String port = "8443";
    private final String domain = "cs122b-project1-api-example";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
    private SearchView searchView;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
//        int currentPage = 1; // Initial page
        int pageSize = 10;   // Page size limit
//        new FetchMovieListTask().execute("", currentPage, pageSize);

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            currentPage++;
            performSearch(searchView.getQuery().toString());
        });

        // Add "Previous" button
        Button prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                performSearch(searchView.getQuery().toString());
            }
        });
    }

    private void performSearch(String query) {
//        int currentPage = 1;
        int pageSize = 10;
        new FetchMovieListTask().execute(query, currentPage, pageSize);
    }


    @SuppressLint("StaticFieldLeak")
    private class FetchMovieListTask extends AsyncTask<Object, Void, ArrayList<Movie>> {
    @Override
        protected ArrayList<Movie> doInBackground(Object... params) {
            String query = (String) params[0];
            int page = (int) params[1];
            int pageSize = (int) params[2];
            return fetchMovieList(query, page, pageSize);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
        ListView listView = findViewById(R.id.list);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie clickedMovie = movies.get(position);
            Intent intent = new Intent(MovieListActivity.this, SingleMovieActivity.class);
            intent.putExtra("MOVIE", clickedMovie);
            intent.putExtra("MOVIE_LIST", movies);
            startActivity(intent);
        });

        updateUI(movies);
        }
    }
    private ArrayList<Movie> fetchMovieList(String query, int page, int pageSize) {
        ArrayList<Movie> movies = new ArrayList<>();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // 11/25 4am edited page to page
            URL url = new URL(baseURL + "/api/movie-list?query=" + query + "&page=" + page + "&pageSize=" + pageSize);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            movies = parseMovieList(response.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return movies;
    }

    private ArrayList<Movie> parseMovieList(String jsonString) throws JSONException {
        ArrayList<Movie> movies = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject movieObject = jsonArray.getJSONObject(i);

            String movieId = movieObject.getString("movie_Id");
            String movieTitle = movieObject.getString("movie_Title");
            String movieYear = movieObject.getString("movie_Year");
            String director = movieObject.getString("director");
            double rating;
            if (movieObject.isNull("rating")) {
                rating = 0.0;
            } else {
                rating = movieObject.getDouble("rating");
            }

            JSONArray genresArray = movieObject.getJSONArray("genres");
            ArrayList<String> genres = new ArrayList<>();
            for (int j = 0; j < genresArray.length(); j++) {
                JSONObject genreObject = genresArray.getJSONObject(j);
                String genreName = genreObject.getString("name");
                genres.add(genreName);
            }

            JSONArray starsArray = movieObject.getJSONArray("stars");
            ArrayList<String> stars = new ArrayList<>();
            for (int k = 0; k < starsArray.length(); k++) {
                JSONObject starObject = starsArray.getJSONObject(k);
                String starName = starObject.getString("name");
                stars.add(starName);
            }

            Movie movie = new Movie(movieId, movieTitle, Short.parseShort(movieYear), director, genres, stars, rating);
            movies.add(movie);
        }

        return movies;
    }

    private void updateUI(ArrayList<Movie> movies) {
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        Log.d("MovieListActivity", "Adapter created with " + adapter.getCount() + " items");

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        for (Movie movie : movies) {
            Log.d("MovieListActivity", "Movie: " + movie.getName() + ", Director: " + movie.getDirector() + ", Rating: " + movie.getRating());
        }

    }
}
