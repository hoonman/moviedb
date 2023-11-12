package Star;

import java.util.Date;

public class Star {
    private String starName;
    private int birthYear;

    //setters and getters
    public Star() {
        this.starName = "";
        this.birthYear = 0000;
    }
    public Star(String starName, int d) {
        this.starName = starName;
        this.birthYear = d;
    }

    public String getStarName() {
        return this.starName;
    }
    public int getDate() {
        return this.birthYear;
    }

    public void setStarName(String s) {
        this.starName = s;
    }
    public void setBirthYear(int d) {
        this.birthYear = d;
    }
}
