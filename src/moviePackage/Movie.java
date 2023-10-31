package moviePackage;

public class Movie {
    private String movieName;
    private int quantity;
    private int cost;
    private String remove;
    private String movieId;
    public Movie(String m,int q,int c, String r, String mid) {
        this.movieName = m;
        this.quantity = q;
        this.cost = c;
        this.remove = r;
        this.movieId = mid;
    }

    public String getName() {
        return this.movieName;
    }
    public int getQuantity() {
        return this.quantity;
    }
    public int getCost() {
        return this.cost;
    }
    public String getRemove() {
        return this.remove;
    }
    public String getMovieId() {return this.movieId;}

    public void setMovieId(String mid) {this.movieId = mid;}

    public void setName(String name) {
        this.movieName = name;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public void setRemove(String remove) {
        this.remove = remove;
    }


}
