package PayUser;

public class PayUser {
    private String firstName;
    private String lastName;
    private String cardNumber;
    private String expDate;

    private boolean authorized;

    public PayUser(String fn, String ln, String cn, String ed, boolean a) {
        this.firstName = fn;
        this.lastName = ln;
        this.cardNumber = cn;
        this.expDate = ed;
        this.authorized = a;
    }

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

    public void setAuthorized(boolean a) {
        this.authorized = a;
    }

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
