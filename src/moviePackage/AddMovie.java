package moviePackage;

public class AddMovie {
    private String movieTitle;
    private int movieYear;
    private String movieDirector;
    private String starName;
    private String genreName;

    public AddMovie() {
        this.movieTitle = "";
        this.movieYear = 0;
        this.movieDirector = "";
        this.starName = "";
        this.genreName = "";
    }

    public AddMovie(String mt, int my, String md, String sn, String gn) {
        this.movieTitle = mt;
        this.movieYear = my;
        this.movieDirector = md;
        this.starName = sn;
        this.genreName = gn;
    }

    // Getters
    public String getMovieTitle() {
        return movieTitle;
    }

    public int getMovieYear() {
        return movieYear;
    }

    public String getMovieDirector() {
        return movieDirector;
    }

    public String getStarName() {
        return starName;
    }

    public String getGenreName() {
        return genreName;
    }

    // Setters
    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setMovieYear(int movieYear) {
        this.movieYear = movieYear;
    }

    public void setMovieDirector(String movieDirector) {
        this.movieDirector = movieDirector;
    }

    public void setStarName(String starName) {
        this.starName = starName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
