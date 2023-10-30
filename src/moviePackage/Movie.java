package moviePackage;

public class Movie {
    private String movieName;
    private int quantity;
    private int cost;
    private String remove;
    public Movie(String m,int q,int c, String r) {
        this.movieName = m;
        this.quantity = q;
        this.cost = c;
        this.remove = r;
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
