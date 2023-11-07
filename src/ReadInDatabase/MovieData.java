package ReadInDatabase;
import java.util.ArrayList;
import java.util.List;

public class MovieData {
    String id;
    String title;
    int year;
    String director;
    List<GenreData> genres;

    boolean isInvalidEntry;

    public MovieData(){
        this.genres = new ArrayList<>();
    }

    public MovieData(String id, String title, int year, String director) {
        this.id = id;
        this.title = title;
        this.year  = year;
        this.director = director;
        this.genres = new ArrayList<>();

    }

    public boolean checkStringValues(){
        return !id.isEmpty() && !title.isEmpty() && !director.isEmpty();
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public List<GenreData> getGenres() {
        return genres;
    }

    public void addGenre(GenreData newGenre){
        if (genres == null) {
            genres = new ArrayList<>();
        }
        genres.add(newGenre);
    }

    public boolean isInvalidEntry() {
        return (isInvalidEntry || !checkStringValues() || genres.isEmpty());//&& checkGenreEntries();
    }
    public boolean checkGenreEntries(){
        return !genres.isEmpty();
    }
    public void setInvalidEntry(boolean invalidEntry) {
        isInvalidEntry = invalidEntry;
    }

    @Override
    public String toString() {
        return "MovieData{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", director='" + director + '\'' +
                ", genres=" + genres +
                '}';
    }
}
