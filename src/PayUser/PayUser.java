package PayUser;

public class PayUser {
    private String firstName;
    private String lastName;
    private String cardNumber;
    private String expDate;
    private String movieId;

    private boolean authorized;
    private String saleId;

    public PayUser(String fn, String ln, String cn, String ed, boolean a, String mid, String sid) {
        this.firstName = fn;
        this.lastName = ln;
        this.cardNumber = cn;
        this.expDate = ed;
        this.authorized = a;
        this.movieId = mid;
        this.saleId = sid;
    }

    public String getSaleId() {return this.saleId;}
    public String getMovieId() {return this.movieId;}
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean getAuthorized() {
        return this.authorized;
    }

    public void setSaleId(String sid) {this.saleId = sid;}
    public void setAuthorized(boolean a) {
        this.authorized = a;
    }

    public void setMovieId(String movieId) {this.movieId = movieId;}

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }
}
