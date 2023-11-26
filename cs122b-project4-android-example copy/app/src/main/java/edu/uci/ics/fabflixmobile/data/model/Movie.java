package edu.uci.ics.fabflixmobile.data.model;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie implements Parcelable{
    private final String movieId;
    private final String title;
    private final short year;
    private final String director;
    private ArrayList<String> genres = null;
    private ArrayList<String> stars = null;
    private final double rating;


    public Movie(String movieId, String title, short year, String director, ArrayList<String> genres, ArrayList<String> stars, double rating) {
        this.movieId = movieId;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
        this.rating = rating;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getName() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public short getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public ArrayList<String> getStars() {
        return stars;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
        dest.writeString(title);
        dest.writeInt(year);
        dest.writeString(director);
//        dest.writeStringList(genres);
//        dest.writeStringList(stars);
        if (genres != null) {
            dest.writeByte((byte) 1); // Not null
            dest.writeStringList(genres);
        } else {
            dest.writeByte((byte) 0); // Null
        }

        // Write stars, handling null case
        if (stars != null) {
            dest.writeByte((byte) 1); // Not null
            dest.writeStringList(stars);
        } else {
            dest.writeByte((byte) 0); // Null
        }
        dest.writeDouble(rating);
    }

    // Parcelable CREATOR
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    // Constructor that reads from a Parcel

    private Movie(Parcel in) {
        movieId = in.readString();
        title = in.readString();
        year = (short) in.readInt();
        director = in.readString();

        // Read genres, handling null case
        genres = new ArrayList<>();
        if (in.readByte() == 1) { // Check if genres is not null
            in.readStringList(genres);
        }

        // Read stars, handling null case
        stars = new ArrayList<>();
        if (in.readByte() == 1) { // Check if stars is not null
            in.readStringList(stars);
        }

        rating = in.readDouble();
    }
}
